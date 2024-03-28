package com.example.locallockers.ui.theme.Register.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password
    
    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword
    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onNameChanged(it: String) {
        _name.value = it
    }

    fun onPasswordChanged(it: String) {
        _password.value = it
    }

    fun onConfirmPasswordChanged(it: String) {
        _confirmPassword.value = it
    }
}