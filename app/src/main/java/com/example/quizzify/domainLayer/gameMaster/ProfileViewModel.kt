package com.example.quizzify.domainLayer.gameMaster

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizzify.dataLayer.common.Resource
import com.example.quizzify.dataLayer.common.UserPreferencesRepository
import com.example.quizzify.domainLayer.useCase.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val loading: Boolean = false,

    val errorOccurred: Boolean = false,
    val errorMessage: String = "",

    val name: String = "User",
    val email: String = "User@mail.com",
    val imageUrl: String = "https://i.scdn.co/image/ab67616d00001e02ff9ca10b55ce82ae553c8228",
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _profileState = mutableStateOf(ProfileState(loading = true))
    val profileState: State<ProfileState> = _profileState

    /**
     * Fetch the user profile from Spotify.
     */
    fun getProfile() {
        viewModelScope.launch {
            getUserProfileUseCase().collect { res ->
                when (res) {
                    is Resource.Success -> {
                        _profileState.value = _profileState.value.copy(
                            name = res.data!!.username,
                            email = res.data.email,
                            imageUrl = res.data.image,
                        )
                    }
                    is Resource.Error -> {
                        _profileState.value = _profileState.value.copy(
                            errorOccurred = true,
                            errorMessage = res.message!!
                        )
                    }
                    is Resource.Loading -> {
                        Log.d("PROFILE", "Loading")
                    }
                }
            }
            _profileState.value = _profileState.value.copy(loading = false)
        }
    }

    suspend fun setLoggedOut(){
        userPreferencesRepository.updateIsLoggedIn(false)
    }


}