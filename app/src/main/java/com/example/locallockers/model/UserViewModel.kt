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

class UserViewModel : ViewModel() {
    var showCustomizationDialog by mutableStateOf(false)
    var snackbarMessage by mutableStateOf("")
    var showSnackbar by mutableStateOf(false)
    val currentUser = MutableLiveData<UserModel?>()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        //updateUser() Neceistamos aplicar una lógica para que no se pueda moficiar el rol del user. Creo que si lo dejamos en blanco no se modifica pero tengo que mirarlo
        loadUserInfo()
    }

    private fun loadUserInfo() {
        auth.currentUser?.let { firebaseUser ->
            val userId = firebaseUser.uid
            db.collection("Users").document(userId).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = UserModel(
                        userId = document.getString("userId") ?: userId,
                        email = document.getString("email") ?: "",
                        userName = document.getString("userName") ?: "",
                        role = document.getString("role") ?: "Turista"
                    )
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
                            role = "Turista" //MODIFICAR POR SI ES HUESPED SE QUEDE HUESPED
                        )
                    } else {
                        Log.d("Firestore", "No such document")
                        // Fallback to default name if document does not exist
                        currentUser.value = UserModel(
                            userId = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            userName = "Default Name",
                            role = "Turista" /*TODO*/
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
    fun dismissSnackbar() {
        showSnackbar = false
    }
}
