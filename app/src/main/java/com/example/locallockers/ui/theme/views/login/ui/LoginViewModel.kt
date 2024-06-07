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
/**
 * ViewModel para manejar la funcionalidad de inicio de sesión de usuarios en la aplicación Local Lockers.
 * Administra el estado y la lógica asociada con la autenticación de usuarios y la obtención de sus perfiles.
 */
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

    /**
     * Carga los datos iniciales del usuario si ya hay un usuario autenticado.
     * Establece isLoading en true mientras se obtienen los datos y en false una vez terminado.
     */
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
                            role = document.getString("role") ?: "Turista",
                            lockerId = document.getString("lockerId") ?: ""
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
    /**
     * Obtiene los detalles del usuario desde Firestore basado en el usuario autenticado actual.
     * Llama al callback onDetailsFetched una vez que los detalles se obtienen correctamente.
     *
     * @param onDetailsFetched Callback que se invoca después de obtener los detalles del usuario.
     */
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
                        role = document.getString("role") ?: "Turista",
                        lockerId = document.getString("lockerId") ?: ""

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
    /**
     * Autentica al usuario con correo electrónico y contraseña.
     * En caso de inicio de sesión exitoso, obtiene los detalles del usuario e invoca el callback onSuccess.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     * @param onSuccess Callback que se invoca después de un inicio de sesión exitoso.
     */
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
    /**
     * Inicia sesión del usuario utilizando credenciales de Google.
     * En caso de inicio de sesión exitoso, invoca el callback home.
     *
     * @param credential Credencial de autenticación de Google.
     * @param home Callback que se invoca después de un inicio de sesión exitoso.
     */
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
    /**
     * Actualiza el correo electrónico, la contraseña y el estado del botón de inicio de sesión basado en los valores proporcionados.
     *
     * @param email Correo electrónico del usuario.
     * @param password Contraseña del usuario.
     */
    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _loginEnable.value = isValidEmail(email) && isValidPass(password)
    }
    /**
     * Valida el formato del correo electrónico.
     *
     * @param email Correo electrónico a validar.
     * @return True si el formato del correo electrónico es válido, de lo contrario false.
     */
    private fun isValidEmail(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    /**
     * Valida la longitud de la contraseña.
     *
     * @param password Contraseña a validar.
     * @return True si la longitud de la contraseña es al menos 6 caracteres, de lo contrario false.
     */
    private fun isValidPass(password: String): Boolean = password.length >= 6
    /**
     * Cierra el diálogo de alerta.
     */
    fun closeAlert(){
        showAlert = false
    }
    /**
     * Obtiene los detalles del usuario basado en el correo electrónico proporcionado.
     *
     * @param email Correo electrónico del usuario cuyos detalles se van a obtener.
     */
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
                            role = document.getString("role") ?: "Turista",
                            lockerId = document.getString("lockerId") ?: ""

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