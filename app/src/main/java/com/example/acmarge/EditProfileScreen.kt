import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

@Composable
fun UserProfileEditScreen(
    userName: String,
    userProfession: String,
    userEmail: String,
    currentPhoto: String?,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var name by remember { mutableStateOf(userName) }
    var profession by remember { mutableStateOf(userProfession) }
    var email by remember { mutableStateOf(userEmail) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(currentPhoto?.let { Uri.parse(it) }) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            Toast.makeText(context, "Fotoğraf seçildi!", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF4285F4),
            contentColor = Color.White
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "Edit Profile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Profile Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Profile Photo
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 80.dp)
            ) {
                AsyncImage(
                    model = selectedImageUri ?: "https://static.vecteezy.com/system/resources/previews/009/292/244/non_2x/default-avatar-icon-of-social-media-user-vector.jpg",
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                )
                IconButton(
                    onClick = {
                        pickImageLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF4285F4))
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Photo",
                        tint = Color.White
                    )
                }
            }

            // Form Fields
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            TextField(
                value = profession,
                onValueChange = { profession = it },
                label = { Text("Profession") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Save Button
            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        if (selectedImageUri != null) {
                            // Upload photo to Firebase Storage
                            val storageRef = storage.reference.child("profilePhotos/$userId.jpg")
                            storageRef.putFile(selectedImageUri!!)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { photoUrl ->
                                        updateUserData(
                                            userId = userId,
                                            name = name,
                                            profession = profession,
                                            email = email,
                                            profilePhotoPath = "profilePhotos/$userId.jpg",
                                            firestore = firestore,
                                            onSuccess = {
                                                Toast.makeText(context, "Profil başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                                            },
                                            onFailure = { errorMessage ->
                                                Toast.makeText(context, "Hata: $errorMessage", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(context, "Fotoğraf yükleme hatası: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            updateUserData(
                                userId = userId,
                                name = name,
                                profession = profession,
                                email = email,
                                profilePhotoPath = null,
                                firestore = firestore,
                                onSuccess = {
                                    Toast.makeText(context, "Profil başarıyla güncellendi!", Toast.LENGTH_SHORT).show()
                                },
                                onFailure = { errorMessage ->
                                    Toast.makeText(context, "Hata: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Text("Save", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

fun updateUserData(
    userId: String,
    name: String,
    profession: String,
    email: String,
    profilePhotoPath: String?,
    firestore: FirebaseFirestore,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val updates = mutableMapOf<String, Any>(
        "name" to name,
        "profession" to profession,
        "email" to email
    )
    if (profilePhotoPath != null) {
        updates["profilePhotoPath"] = profilePhotoPath // Storage'daki yolu saklıyoruz
    }

    firestore.collection("Users").document(userId)
        .update(updates)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { e -> onFailure(e.message ?: "Bilinmeyen bir hata oluştu.") }
}
