package com.example.locallockers.ui.theme.views.Register.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locallockers.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.initialize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    var showAlert by mutableStateOf(false)

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
    fun createUser (email: String, password: String, userName: String, onSucess:() -> Unit){
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        task->
                    if(task.isSuccessful){
                        saveUser(userName)
                        onSucess()
                    }else{
                        Log.d("Error en Firebase","Error al crear usuario")
                        showAlert = true
                    }
                }
            }catch (e:Exception){
                Log.d("Error en Jetpack","Error ${e.localizedMessage}")
            }
        }
    }
    //Usa userId es facil de obtener información pero es menos segura
    private fun saveUser(userName: String) {
        val id = auth.currentUser?.uid  // UID del usuario autenticado
        val email = auth.currentUser?.email
        viewModelScope.launch(Dispatchers.IO) {
            val user = UserModel(
                userId = id.toString(),
                email = email.toString(),
                userName = userName
            ).toMap()
            if (id != null) {
                FirebaseFirestore.getInstance().collection("Users")
                    .document(id)  // Usa el UID como el Document ID
                    .set(user)
                    .addOnSuccessListener {
                        Log.d("Guardado", "Guardado correctamente")
                    }
                    .addOnFailureListener {
                        Log.d("Error al guardar", "Error al guardar en Firestore")
                    }
            }
        }
    }
    fun closeAlert(){
        showAlert = false
    }
}