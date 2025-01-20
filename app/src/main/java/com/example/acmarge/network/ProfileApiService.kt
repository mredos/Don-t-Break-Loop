import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApiService {
    @GET("profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): User

    @PUT("profile/{userId}")
    suspend fun updateUserProfile(
        @Path("userId") userId: String,
        @Body updatedProfile: User
    ): User
}