package com.example.quizzify.domainLayer.utils

import androidx.lifecycle.ViewModel
import com.example.quizzify.dataLayer.common.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.asLiveData
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val userPreferencesFlow = userPreferencesRepository.userPreferencesFlow.asLiveData()

    suspend fun setLoggedIn(){
        userPreferencesRepository.updateIsLoggedIn(true)
    }
}