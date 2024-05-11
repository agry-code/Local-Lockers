package com.example.locallockers.ui.theme.views.Register.ui

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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    var showAlert by mutableStateOf(false)

    // Variables de estado para latitud y longitud
    private val _latitude = MutableLiveData<Double>()
    val latitude: LiveData<Double> = _latitude

    private val _longitude = MutableLiveData<Double>()
    val longitude: LiveData<Double> = _longitude

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
    fun onLatitudeChanged(lat: Double) {
        _latitude.value = lat
    }

    fun onLongitudeChanged(long: Double) {
        _longitude.value = long
    }

    fun onLocalNameChanged(localName: String){
        _localName.value = localName
    }

    fun onOpenHoursChanged(openHours: String) {
        _openHours.value = openHours
    }

    // Variables de estado para manejar los datos del formulario
    var userType by mutableStateOf("Turista")  // "Turista" es el valor predeterminado
    private val _role = MutableLiveData<String>("Turista")

    fun onRoleChanged(role: String) {
        _role.value = role
        userType = role  // Asegura que userType y role están sincronizados
    }
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


    fun createUser(email: String, password: String, userName: String,lat: Double,long: Double, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            if (_role.value == "Huesped") {
                                saveUser(userName, "Huesped", currentUser.uid) {
                                    saveLocker(currentUser.uid,lat,long) { lockerId ->
                                        updateUserWithLockerId(currentUser.uid, lockerId, onSuccess)
                                    }
                                }
                            } else {
                                saveUser(userName, "Turista", currentUser.uid, onSuccess)
                            }
                        }
                    } else {
                        Log.d("Error en Firebase", "Error al crear usuario")
                        showAlert = true
                    }
                }
            } catch (e: Exception) {
                Log.d("Error en Jetpack", "Error ${e.localizedMessage}")
                showAlert = true
            }
        }
    }

    private fun saveUser(userName: String, role: String, userId: String, onSuccess: () -> Unit = {}) {
        val user = UserModel(
            userId = userId,
            email = auth.currentUser?.email ?: "",
            userName = userName,
            role = role,
            lockerId = "" //puede probocar problemas
        ).toMap()

        FirebaseFirestore.getInstance().collection("Users")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Guardado", "Usuario guardado correctamente")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.d("Error al guardar", "Error al guardar en Firestore", e)
                showAlert = true
            }
    }
    private fun saveLocker(ownerId: String,lat: Double,long: Double, onSuccess: (String) -> Unit) {
        //Pasamos location a coordenadas

        // Crear un LockerModel sin ID específico
        val locker = LockerModel(
            name = _localName.value ?: "",
            latitude = lat,  // Estos valores deben ser establecidos de alguna manera antes de guardar
            longitude = long,
            openHours = _openHours.value ?: "",
            owner = ownerId
        )
        Log.d("RegisterViewModel","${locker.toString()}")

        FirebaseFirestore.getInstance().collection("Lockers")
            .add(locker.toMap())
            .addOnSuccessListener { documentReference ->
                val generatedId = documentReference.id
                // Actualiza el documento para incluir el ID como un campo en él
                documentReference.update("id", generatedId).addOnCompleteListener { updateTask ->
                    if (updateTask.isSuccessful) {
                        Log.d("DocumentUpdate", "Documento actualizado con su propio ID")
                        onSuccess(generatedId)
                    } else {
                        Log.e("DocumentUpdateError", "Error al actualizar documento con su propio ID")
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Error al guardar", "Error al guardar Locker en Firestore", e)
                showAlert = true
            }

    }


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
                Log.e("UpdateUser", "Error al actualizar usuario", e)
                showAlert = true  // Mostrar alerta en la UI si es necesario
            }
    }
    fun closeAlert() {
        showAlert = false
    }

    fun showError(message: String) {
        Log.e("RegistrationError", message)
        showAlert = true
    }

}