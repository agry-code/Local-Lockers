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
import com.example.locallockers.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _currentUser = MutableStateFlow<UserModel?>(null)
    val currentUser: StateFlow<UserModel?> = _currentUser.asStateFlow()

    var isLoading by mutableStateOf(false)


    init {
        loadInitialData()
    }
    private fun loadInitialData() {
        viewModelScope.launch {
            isLoading = true
            val currentUser = auth.currentUser
            if (currentUser != null) {
                val userId = currentUser.uid
                Firebase.firestore.collection("Users").document(userId).get().addOnSuccessListener { document ->
                    viewModelScope.launch {  // Asegurarse de que emit se llama dentro de una coroutine
                        _currentUser.emit(UserModel(
                            userId = document.getString("userId") ?: userId,
                            email = document.getString("email") ?: "",
                            userName = document.getString("userName") ?: "",
                            role = document.getString("role") ?: "Turista"
                        ))
                        isLoading = false
                    }
                }.addOnFailureListener { exception ->
                    isLoading = false
                    Log.d("LoginVM", "Error al cargar los datos del usuario: ${exception.message}")
                }
            } else {
                isLoading = false
                Log.d("LoginVM", "No hay usuario autenticado al inicializar")
            }
        }
    }

    private fun fetchUserDetails(onDetailsFetched: () -> Unit) {
        isLoading = true
        auth.currentUser?.let { firebaseUser ->
            val userId = firebaseUser.uid
            Firebase.firestore.collection("Users").document(userId).get().addOnSuccessListener { document ->
                viewModelScope.launch {
                    _currentUser.emit(UserModel(
                        userId = document.getString("userId") ?: userId,
                        email = document.getString("email") ?: "",
                        userName = document.getString("userName") ?: "",
                        role = document.getString("role") ?: "Turista"
                    ))
                    onDetailsFetched()
                    isLoading = false
                }
            }.addOnFailureListener {
                isLoading = false
                Log.d("Firebase", "Error al obtener los detalles del usuario: ${it.message}")
            }
        }
    }

fun login(email: String, password: String, onSuccess: () -> Unit) {
    viewModelScope.launch {
        isLoading = true
        try {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchUserDetails {
                        onSuccess()
                        isLoading = false
                        Log.d("ProblemaRol", "Detalles del usuario actualizados LVM : ${_currentUser.value}")
                    }
                } else {
                    isLoading = false
                    Log.d("Error en Firebase", "Usuario o contraseña incorrecto: ${task.exception?.message}")
                    showAlert = true
                }
            }
        } catch (e: Exception) {
            isLoading = false
            Log.d("Error en Jetpack", "Error durante el login: ${e.localizedMessage}")
            showAlert = true
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
    fun fetchUserDetailsByEmail(email: String) {
        isLoading = true
        Firebase.firestore.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents.first()
                    viewModelScope.launch {
                        _currentUser.emit(UserModel(
                            userId = document.getString("userId") ?: "",
                            email = document.getString("email") ?: "",
                            userName = document.getString("userName") ?: "",
                            role = document.getString("role") ?: "Turista"
                        ))
                        Log.d("ProblemaRol", "Detalles del usuario actualizados LVM : ${_currentUser.value}")
                        isLoading = false
                    }
                } else {
                    isLoading = false
                    Log.d("ProblemaRol", "No se encontró el usuario con el correo: $email")
                }
            }
            .addOnFailureListener {
                isLoading = false
                Log.d("ProblemaRol", "Error al buscar el usuario: ${it.message}")
            }
    }
}