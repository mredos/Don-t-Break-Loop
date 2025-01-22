package com.example.acmarge

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.acmarge.network.RetrofitClient
import com.example.acmarge.ui.theme.ACMArgeTheme
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var selectedDate by mutableStateOf(getDateList()[30])
    private var currentPhotoPath: String? = null
    private var tasks = mutableMapOf<String, MutableList<String>>()
    private var completedTasks by mutableStateOf(
        mutableMapOf<String, MutableList<String>>()
    )
    // Kamera izni kontrolü ve başlatıcı
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // Kamera için ActivityResult launcher
    private val takePictureResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoPath?.let {
                    analyzeImage(Uri.fromFile(File(it)))
                }
            } else {
                Toast.makeText(this, "Photo capture failed", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ACMArgeTheme {
                TaskManagementScreen(
                    modifier = Modifier.fillMaxSize(),
                    tasks = tasks,
                    selectedDate = selectedDate,
                    onDateSelected = { newDate -> selectedDate = newDate },
                    onCameraRequest = { requestCameraPermission() },

                    // MainActivity’deki completedTasks'i doğrudan parametre olarak veriyoruz
                    completedTasks = completedTasks,

                    // completedTasks değiştiğinde MainActivity’nin state’ini güncelleyecek callback
                    onCompletedTasksChange = { newMap ->
                        completedTasks = newMap
                    }
                )
            }
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                it
            )
            currentPhotoPath = it.absolutePath
            takePictureResult.launch(photoURI)
        }
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )
            val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
        } catch (ex: IOException) {
            Log.e("MainActivity", "Error creating image file: ${ex.message}")
            null
        }
    }

    //    private fun analyzeImage(imageUri: Uri) {
//        // File path doğrudan currentPhotoPath üzerinden alınacak.
//        val file = File(currentPhotoPath ?: return)
//        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
//
//        RetrofitClient.api.analyzeImage(image = part)
//            .enqueue(object : Callback<ImageAnalysisResponse> {
//                override fun onResponse(
//                    call: Call<ImageAnalysisResponse>,
//                    response: Response<ImageAnalysisResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val tags = response.body()?.tags?.map { it.name } ?: emptyList()
//                        val habitMatched = determineHabit(tags)
//
//                        if (habitMatched != null) {
//                            Log.d("HabitTracker", "Habit Detected: $habitMatched")
//                        } else {
//                            Log.d("HabitTracker", "No habit detected.")
//                        }
//                    } else {
//                        Log.e("HabitTracker", "API Error: ${response.errorBody()?.string()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<ImageAnalysisResponse>, t: Throwable) {
//                    Log.e("HabitTracker", "Request failed: ${t.message}")
//                }
//            })
//    }
    private fun analyzeImage(imageUri: Uri) {
        if (currentPhotoPath.isNullOrEmpty()) {
            showToast("Error: currentPhotoPath is null or empty!")
            return
        }

        val file = File(currentPhotoPath!!)
        if (!file.exists()) {
            showToast("Error: File does not exist at path: $currentPhotoPath")
            return
        }

        val requestBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)

        RetrofitClient.api.analyzeImage(image = part)
            .enqueue(object : Callback<ImageAnalysisResponse> {
                override fun onResponse(
                    call: Call<ImageAnalysisResponse>,
                    response: Response<ImageAnalysisResponse>
                ) {
                    if (response.isSuccessful) {
                        val tags = response.body()?.tags?.map { it.name } ?: emptyList()
                        val habitMatched = determineHabit(tags)

                        if (habitMatched != null) {
                            // Görevler arasında eşleşme kontrolü
                            val currentTasks = tasks[selectedDate] ?: emptyList()

                            // Örneğin "Sports or Fitness" gibi bir habitMatched elde ettiniz.
                            // tasks içinde "Sports or Fitness at 12:00 PM" vb. olarak geçiyor olabilir.
                            // Aşağıda bu habit’in adını (ör. "Sports or Fitness") içeren görevi yakalıyoruz:
                            val matchedTask = currentTasks.find {
                                it.contains(habitMatched, ignoreCase = true)
                            }

                            if (matchedTask != null) {
                                showToast("Matched Habit: $habitMatched in tasks!")

                                // 3) Burada completedTasks'i güncelleyip checkbox'ı işaretlemiş olacağız
                                //    (Zaten Compose state olduğu için arayüz otomatik yenilenir).
                                val oldMap = completedTasks.toMutableMap()
                                val oldList = oldMap[selectedDate] ?: mutableListOf()

                                // Eğer aynı görev tekrar eklenmesin derseniz, eklemeden önce kontrol edebilirsiniz.
                                if (!oldList.contains(matchedTask)) {
                                    oldList.add(matchedTask)
                                }
                                oldMap[selectedDate] = oldList

                                // Değişikliği state’e yansıtalım
                                completedTasks = oldMap

                            } else {
                                showToast("Habit Detected: $habitMatched, but no match in tasks.")
                            }
                        } else {
                            showToast("No habit detected.")
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                        showToast("API Error: $errorMessage")
                    }
                }

                override fun onFailure(call: Call<ImageAnalysisResponse>, t: Throwable) {
                    val failureMessage = t.message ?: "Unknown error"
                    showToast("Request failed: $failureMessage")
                }
            })
    }


    // Kullanıcıya Toast göstermek için bir yardımcı fonksiyon
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            Log.d("Toast", message) // Doğrulama için Log
        }
    }


    // Alışkanlık belirleme
    private fun determineHabit(tags: List<String>): String? {
        val sportsKeywords = listOf("sports", "exercise", "running", "gym", "soccer", "workout", "fitness")
        val studyingKeywords = listOf("study", "notebook", "writing", "laptop", "desk", "book", "library")
        val cookingKeywords = listOf("cooking", "chef", "kitchen", "recipe", "food", "meal", "ingredients")
        val musicKeywords = listOf("music", "instrument", "guitar", "piano", "violin", "singing")
        val paintingKeywords = listOf("painting", "art", "drawing", "canvas", "brush", "color")
        val cleaningKeywords = listOf("cleaning", "housework", "vacuum", "organizing", "tidying")
        val walkingKeywords = listOf("walking", "outdoor", "park", "nature", "steps", "walk")
        val gardeningKeywords = listOf("gardening", "plants", "flowers", "soil", "watering", "garden")
        val skincareKeywords = listOf("skincare", "face", "cream", "beauty", "routine", "selfcare")
        val meditationKeywords = listOf("meditation", "calm", "relax", "breathing", "mindfulness", "peace")

        return when {
            tags.any { it in sportsKeywords } -> "Sports or Fitness"
            tags.any { it in studyingKeywords } -> "Studying"
            tags.any { it in cookingKeywords } -> "Cooking"
            tags.any { it in musicKeywords } -> "Playing an Instrument"
            tags.any { it in paintingKeywords } -> "Painting or Drawing"
            tags.any { it in cleaningKeywords } -> "Cleaning or Organizing"
            tags.any { it in walkingKeywords } -> "Walking"
            tags.any { it in gardeningKeywords } -> "Gardening"
            tags.any { it in skincareKeywords } -> "Skincare Routine"
            tags.any { it in meditationKeywords } -> "Meditation"
            else -> null
        }
    }
}