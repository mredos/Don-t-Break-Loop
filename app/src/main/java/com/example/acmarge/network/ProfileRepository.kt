import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val name: String = "",
    val profession: String = "",
    val email: String = "",
    val profilePhoto: String? = null // Fotoğraf URL'sini depolamak için
)



class ProfileRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Firestore'dan kullanıcı profilini çeken yöntem
    suspend fun getUserProfile(userId: String): UserProfile {
        try {
            // Kullanıcı belgesini Firestore'dan alın
            val userDocument = firestore.collection("Users").document(userId).get().await()

            // Belgedeki alanları al
            val name = userDocument.getString("name") ?: ""
            val profession = userDocument.getString("profession") ?: ""
            val email = userDocument.getString("email") ?: ""
            val profilePhotoPath = userDocument.getString("profilePhotoPath")

            // Fotoğraf URL'sini Firebase Storage'dan al
            val profilePhotoUrl = if (profilePhotoPath != null) {
                storage.reference.child(profilePhotoPath).downloadUrl.await().toString()
            } else {
                null
            }

            // UserProfile nesnesi oluştur ve döndür
            return UserProfile(
                name = name,
                profession = profession,
                email = email,
                profilePhoto = profilePhotoUrl
            )
        } catch (e: Exception) {
            throw Exception("Failed to fetch user profile: ${e.message}")
        }
    }
}
