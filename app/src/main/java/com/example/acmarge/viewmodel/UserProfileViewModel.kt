package com.example.acmarge.viewmodel
import User
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acmarge.network.RetrofitClient
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel : ViewModel() {
    val user = MutableLiveData<UserProfile?>()
    val loading = MutableLiveData<Boolean>()

    fun fetchUserProfile() {
        loading.value = true
        val firestore = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            user.postValue(null)
            loading.postValue(false)
            return
        }

        viewModelScope.launch {
            try {
                val document = firestore.collection("Profiles").document(currentUser.uid).get().await()
                val name = document.getString("name")
                val email = document.getString("email")
                val job = document.getString("job")
                val profilePhoto = document.getString("profilePhoto")

                user.postValue(UserProfile(name, email, job, profilePhoto))
            } catch (e: Exception) {
                user.postValue(null)
            } finally {
                loading.postValue(false)
            }
        }
    }
}
data class UserProfile(
    val name: String? = null,
    val email: String? = null,
    val job: String? = null,
    val profilePhoto: String? = null
)
