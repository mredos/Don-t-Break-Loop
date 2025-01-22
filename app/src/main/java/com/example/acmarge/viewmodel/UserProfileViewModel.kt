package com.example.acmarge.viewmodel
import User
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acmarge.network.RetrofitClient
import kotlinx.coroutines.launch
class UserProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading
    fun fetchUserProfile(userId: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val user = RetrofitClient.profileApiService.getUserProfile(userId)
                Log.d("UserProfileViewModel", "User fetched: $user")
                this@UserProfileViewModel._user.value = user
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error: ${e.message}")
                _user.value = null
            } finally {
                _loading.value = false
            }
        }
    }
    fun updateUserProfile(name: String, profession: String, email: String, profilePhoto: String?) {
        viewModelScope.launch {
            try {
                _loading.value = true
                // Mevcut kullanıcı bilgilerini alıyoruz
                val currentUser = user.value
                // Yeni user objesi oluşturuyoruz
                val updatedUser = User(
                    _id = currentUser?._id ?: "",
                    name = name,
                    profession = profession,
                    email = email,
                    profilePhoto = profilePhoto ?: (currentUser?.profilePhoto ?: "")
                )
                // Sunucuya PATCH/PUT (veya update) isteği
                val result = RetrofitClient.profileApiService.updateUserProfile(
                    updatedUser._id,
                    updatedUser
                )
                if (result.profilePhoto == profilePhoto) {
                    Log.d("UserProfileViewModel", "Profile photo successfully updated!")
                } else {
                    Log.e("UserProfileViewModel", "Failed to update profile photo on the server.")
                }
                // Geri dönen güncel user verisini LiveData'ya atıyoruz
                _user.value = result
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error updating profile: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}