import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locallockers.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

// ViewModel para gestionar los usuarios y la autenticación en Firebase.
class UserViewModel : ViewModel() {
    val currentUser = MutableLiveData<UserModel?>()  // LiveData para el usuario actual.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()  // Instancia de FirebaseAuth.
    val guests = MutableLiveData<List<UserModel>>()  // LiveData para la lista de huéspedes.

    private val db = FirebaseFirestore.getInstance()  // Instancia de FirebaseFirestore.
    var showCustomizationDialog by mutableStateOf(false)  // Estado para mostrar el diálogo de personalización.
    var snackbarMessage by mutableStateOf("")  // Mensaje de la snackbar.
    var showSnackbar by mutableStateOf(false)  // Estado para mostrar la snackbar.

    // Bloque init para cargar la información del usuario al inicializar el ViewModel.
    init {
        loadUserInfo()
    }

    // Carga la información del usuario actual desde Firestore.
    private fun loadUserInfo() {
        auth.currentUser?.let { firebaseUser ->
            val userId = firebaseUser.uid
            db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Si el documento existe, crea un objeto UserModel con los datos del documento.
                    val user = UserModel(
                        userId = document.getString("userId") ?: userId,
                        email = document.getString("email") ?: "",
                        userName = document.getString("userName") ?: "",
                        role = document.getString("role") ?: "Turista",
                        lockerId = document.getString("lockerId") ?: ""
                    )
                    // Actualiza el LiveData con el usuario cargado.
                    viewModelScope.launch {
                        currentUser.value = user
                    }
                } else {
                    Log.d("Firestore", "No such document")
                }
            }.addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting document: ", exception)
            }
        } ?: run {
            Log.d("UserVM", "No user is currently logged in")
        }
    }

    // Carga la lista de huéspedes desde Firestore.
    fun loadGuests() {
        db.collection("Users").whereEqualTo("role", "Huesped").get()
            .addOnSuccessListener { result ->
                val guestList = result.mapNotNull { document ->
                    document.toObject(UserModel::class.java)
                }
                guests.value = guestList
            }
            .addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting guests: ", exception)
            }
    }

    // Elimina un huésped y su taquilla asociada de Firestore.
    fun deleteGuestAndLocker(guestId: String, lockerId: String?) {
        val guestRef = db.collection("Users").document(guestId)

        db.runBatch { batch ->
            batch.delete(guestRef)
            if (lockerId != null) {
                val lockerRef = db.collection("Lockers").document(lockerId)
                batch.delete(lockerRef)
            }
        }.addOnSuccessListener {
            Log.d("DeleteViewModel", "Guest and locker deleted successfully")
            loadGuests() // Actualiza la lista de huéspedes después de la eliminación.
        }.addOnFailureListener { e ->
            Log.e("DeleteViewModel", "Error deleting guest and locker", e)
        }
    }

    // Actualiza la información del usuario actual en Firestore.
    private fun updateUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            db.collection("Users").document(firebaseUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userName = document.getString("userName") ?: "Unknown"
                        val email = firebaseUser.email ?: ""
                        currentUser.value = UserModel(
                            userId = firebaseUser.uid,
                            email = email,
                            userName = userName,
                            role = "Turista", // MODIFICAR POR SI ES HUESPED SE QUEDE HUESPED
                            lockerId = document.getString("lockerId") ?: ""
                        )
                    } else {
                        Log.d("Firestore", "No such document")
                        // Fallback al nombre por defecto si el documento no existe.
                        currentUser.value = UserModel(
                            userId = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            userName = "Default Name",
                            role = "Turista", /* TODO: Cambiar el rol si es necesario */
                            lockerId = document.getString("lockerId") ?: ""
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Firestore", "Error getting document: ", exception)
                }
        } else {
            currentUser.value = null
        }
    }

    // Actualiza el nombre de usuario en Firestore.
    fun updateUserName(newName: String) {
        val userId = auth.currentUser?.uid ?: return
        db.collection("Users").document(userId).update("userName", newName)
            .addOnSuccessListener {
                val updatedUser = currentUser.value?.copy(userName = newName)
                currentUser.postValue(updatedUser)
                snackbarMessage = "Name updated successfully"
                showSnackbar = true
            }
            .addOnFailureListener { e ->
                snackbarMessage = "Error updating name"
                showSnackbar = true
            }
    }

    // Oculta la snackbar.
    fun dismissSnackbar() {
        showSnackbar = false
    }
}