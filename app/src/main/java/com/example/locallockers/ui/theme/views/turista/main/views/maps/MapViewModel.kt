package com.example.locallockers.ui.theme.views.turista.main.views.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locallockers.model.LockerModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// ViewModel que gestiona los datos relacionados con los lockers en la pantalla del mapa
class MapViewModel : ViewModel() {
    // Instancia de FirebaseAuth para gestionar la autenticación
    private val auth: FirebaseAuth = Firebase.auth
    // LiveData mutable para almacenar la lista de lockers
    private val _lockers = MutableLiveData<List<LockerModel>>()
    // LiveData inmutable para exponer la lista de lockers
    val lockers: LiveData<List<LockerModel>> = _lockers

    // Método para cerrar sesión
    fun signOut() {
        auth.signOut()
    }

    // Método para cargar los lockers desde Firestore
    fun loadLockers() {
        // Accede a la instancia de Firestore
        val database = Firebase.firestore
        // Obtiene la colección "Lockers"
        database.collection("Lockers").get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // Si no se encontraron documentos, registra un mensaje de advertencia
                Log.d("FirestoreData", "No se encontraron documentos")
            } else {
                // Si se encontraron documentos, mapea cada documento a un objeto LockerModel
                val lockerList = documents.mapNotNull { document ->
                    try {
                        Log.d("FirestoreData", "Procesando documento: ${document.id}")
                        document.toObject(LockerModel::class.java)
                    } catch (e: Exception) {
                        // Si hay algún error al convertir el documento, registra un mensaje de error
                        Log.e("FirestoreError", "Error al convertir documento", e)
                        null
                    }
                }
                // Actualiza el LiveData con la lista de lockers obtenida
                _lockers.value = lockerList
                // Registra un mensaje de éxito con la cantidad de lockers cargados
                Log.d("lockers", "Lista de lockers cargada: ${lockerList.size} elementos")
            }
        }.addOnFailureListener { exception ->
            // Si ocurre un error al obtener los documentos, registra un mensaje de error
            Log.w("LoadLockers", "Error al obtener documentos: ", exception)
        }
    }
}