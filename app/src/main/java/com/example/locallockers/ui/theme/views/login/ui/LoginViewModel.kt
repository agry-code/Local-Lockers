package com.example.locallockers.ui.theme.views.login.ui

import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    var showAlert by mutableStateOf(false)

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginEnable = MutableLiveData<Boolean>()
    val loginEnable: LiveData<Boolean> = _loginEnable

    var userRole = mutableStateOf("Huesped")// No se modifica el userRole

    private fun fetchUserRole(onRoleFetched: () -> Unit) {
        auth.currentUser?.let { user ->
            val userId = user.uid
            val db = Firebase.firestore
            db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                val role = document.getString("role") ?: "Turista"  // Asumiendo "Turista" como rol por defecto
                userRole.value = role
                Log.d("Firebase", "El rol del usuario es: ${role}")
                onRoleFetched()  // Llamada al callback
            }.addOnFailureListener {
                Log.d("Firebase", "Error al obtener el rol del usuario: ${it.message}")
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        fetchUserRole {
                            onSuccess()  // Esto se llama después de actualizar el rol
                        }
                    } else {
                        Log.d("Error en Firebase", "Usuario o contraseña incorrecto")
                        showAlert = true
                    }
                }
            } catch (e: Exception) {
                Log.d("Error en Jetpack", "Error ${e.localizedMessage}")
            }
        }
    }


    fun signInWithGoogleCredential(credential: AuthCredential, home:() -> Unit)
    = viewModelScope.launch {
        try {
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        Log.d("LocalLocker","Logueado con Google satisfactoriamente")
                        home()
                    }
                }.addOnFailureListener {
                    Log.d("LocalLocker","Fallo al loguear con Google")
                }
        }catch (ex:Exception){
            Log.d("LocalLocker","Exception al loguear con Google: "
            +"${ex.localizedMessage}")
        }

    }
    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPass(password)

    }

    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    private fun isValidPass(password: String): Boolean = password.length >= 6

    fun closeAlert(){
        showAlert = false
    }
}