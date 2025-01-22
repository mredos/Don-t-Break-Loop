package com.example.acmarge

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ComputerVisionApi {

    // Azure API anahtarınızı başlıkta gönderiyorsunuz
    @Headers("Ocp-Apim-Subscription-Key: 9oRrseladMaxCKEfahACDHz3SeWVXgXlF7r4dCw4h20Es2kT3Ln1JQQJ99BAAC5RqLJXJ3w3AAAFACOG93iY")
    @Multipart // Dosya yüklemesi için @Multipart kullanıyoruz
    @POST("vision/v3.2/analyze")
    fun analyzeImage(
        @Part image: MultipartBody.Part, // Dosya parametresi
        @Query("visualFeatures") features: String = "Categories,Tags,Description" // Görsel özellikleri
    ): Call<ImageAnalysisResponse>
}