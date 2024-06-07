package com.example.locallockers.ui.theme.views.Register.ui

// Importaciones necesarias para la funcionalidad del ViewModel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locallockers.model.LockerModel
import com.example.locallockers.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// ViewModel para la pantalla de registro
class RegisterViewModel : ViewModel() {
    // Instancia de FirebaseAuth para la autenticación de usuarios
    private val auth: FirebaseAuth = Firebase.auth

    // Variables de estado utilizando mutableStateOf para Jetpack Compose
    var showAlert by mutableStateOf(false)
    var alertMessage by mutableStateOf("")

    // LiveData para manejar la latitud y longitud
    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double> = _latitude

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double> = _longitude

    // LiveData para manejar los datos del formulario de registro
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _confirmPassword = MutableLiveData<String>()
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _openHours = MutableLiveData<String>()
    val openHours: LiveData<String> = _openHours

    private val _localName = MutableLiveData<String>()
    val localName: LiveData<String> = _localName

    // Funciones para actualizar los valores de LiveData
    fun onLocalNameChanged(localName: String) {
        _localName.value = localName
    }

    fun onOpenHoursChanged(openHours: String) {
        _openHours.value = openHours
    }

    // Variables de estado y LiveData para manejar el tipo de usuario
    var userType by mutableStateOf("Turista")  // "Turista" es el valor predeterminado
    private val _role = MutableLiveData<String>("Turista")

    fun onRoleChanged(role: String) {
        _role.value = role
        userType = role  // Asegura que userType y role están sincronizados
    }

    // Funciones para actualizar los valores de LiveData del formulario
    fun onEmailChanged(email: String) {
        _email.value = email
    }

    fun onNameChanged(name: String) {
        _name.value = name
    }

    fun onPasswordChanged(password: String) {
        _password.value = password
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _confirmPassword.value = confirmPassword
    }

    // Función para crear un nuevo usuario
    fun createUser(
        email: String, password: String, userName: String,
        lat: Double, long: Double, onSuccess: () -> Unit
    ) {
        // Lanzar una coroutine en el scope del ViewModel
        viewModelScope.launch {
            try {
                // Crear un usuario con email y contraseña usando FirebaseAuth
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            if (_role.value == "Huesped") {
                                // Guardar usuario y locker si el rol es "Huesped"
                                saveUser(userName, "Huesped", currentUser.uid) {
                                    saveLocker(currentUser.uid, lat, long) { lockerId ->
                                        updateUserWithLockerId(currentUser.uid, lockerId, onSuccess)
                                    }
                                }
                            } else {
                                // Guardar solo el usuario si el rol es "Turista"
                                saveUser(userName, "Turista", currentUser.uid, onSuccess)
                            }
                        }
                    } else {
                        // Mostrar error si no se pudo crear el usuario
                        showError("Error al crear usuario, el email debe ser válido")
                        Log.d("Error en Firebase", "Error al crear usuario")
                    }
                }
            } catch (e: Exception) {
                // Capturar cualquier excepción y mostrar el error
                showError("Error en Jetpack Compose: ${e.localizedMessage}")
                Log.d("Error en Jetpack", "Error ${e.localizedMessage}")
            }
        }
    }

    // Función para guardar el usuario en Firestore
    private fun saveUser(userName: String, role: String, userId: String, onSuccess: () -> Unit = {}) {
        // Crear un modelo de usuario
        val user = UserModel(
            userId = userId,
            email = auth.currentUser?.email ?: "",
            userName = userName,
            role = role,
            lockerId = ""
        ).toMap()

        // Guardar el usuario en la colección "Users" de Firestore
        FirebaseFirestore.getInstance().collection("Users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Guardado", "Usuario guardado correctamente")
                onSuccess()
            }
            .addOnFailureListener { e ->
                showError("Error al guardar usuario en Firestore")
                Log.d("Error al guardar", "Error al guardar en Firestore", e)
            }
    }

    // Función para guardar un locker en Firestore
    private fun saveLocker(ownerId: String, lat: Double, long: Double, onSuccess: (String) -> Unit) {
        // Crear un modelo de locker
        val locker = LockerModel(
            name = _localName.value ?: "",
            latitude = lat,
            longitude = long,
            openHours = _openHours.value ?: "",
            owner = ownerId
        )

        // Agregar el locker a la colección "Lockers" en Firestore
        FirebaseFirestore.getInstance().collection("Lockers")
            .add(locker.toMap())
            .addOnSuccessListener { documentReference ->
                val generatedId = documentReference.id
                // Actualizar el documento para incluir su propio ID
                documentReference.update("id", generatedId).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Log.d("DocumentUpdate", "Documento actualizado con su propio ID")
                        onSuccess(generatedId)
                    } else {
                        showError("Error al actualizar documento con su propio ID")
                        Log.e("DocumentUpdateError", "Error al actualizar documento con su propio ID")
                    }
                }
            }
            .addOnFailureListener { e ->
                showError("Error al guardar Locker en Firestore")
                Log.e("Error al guardar", "Error al guardar Locker en Firestore", e)
            }
    }

    // Función para actualizar el usuario con el ID del locker
    fun updateUserWithLockerId(userId: String, lockerId: String, onSuccess: () -> Unit) {
        // Obtener la referencia al documento del usuario
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(userId)

        // Actualizar el documento con el nuevo lockerId
        userRef.update("lockerId", lockerId)
            .addOnSuccessListener {
                Log.d("UpdateUser", "Usuario actualizado con nuevo locker ID")
                onSuccess()  // Llamar al callback de éxito
            }
            .addOnFailureListener { e ->
                showError("Error al actualizar usuario con locker ID")
                Log.e("UpdateUser", "Error al actualizar usuario", e)
            }
    }

    // Función para cerrar la alerta
    fun closeAlert() {
        showAlert = false
        alertMessage = ""
    }

    // Función para mostrar un error
    fun showError(message: String) {
        Log.e("RegistrationError", message)
        alertMessage = message
        showAlert = true
    }

    // Variable y función para manejar el estado de carga
    var isLoading by mutableStateOf(false)
    fun showLoading(show: Boolean) {
        isLoading = show
    }
}
