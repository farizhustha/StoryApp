package com.farizhustha.storyapp.ui.auth.signin

import androidx.lifecycle.ViewModel
import com.farizhustha.storyapp.data.AuthRepository
import com.farizhustha.storyapp.model.User

class SignInViewModel(private val authRepository: AuthRepository) : ViewModel() {
    fun getUserToken() = authRepository.getUserToken()

    fun login(user: User) = authRepository.login(user)
}