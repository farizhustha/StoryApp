package com.farizhustha.storyapp.ui.auth.signup

import androidx.lifecycle.ViewModel
import com.farizhustha.storyapp.data.AuthRepository
import com.farizhustha.storyapp.model.User

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun register(user: User) = authRepository.register(user)
}