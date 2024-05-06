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

class MapViewModel : ViewModel(){
    private val auth: FirebaseAuth = Firebase.auth
    private val _lockers = MutableLiveData<List<LockerModel>>()
    val lockers: LiveData<List<LockerModel>> = _lockers
    fun signOut(){
     auth.signOut()
    }

    fun loadLockers() {
        val database = Firebase.firestore
        database.collection("Lockers").get().addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                Log.d("FirestoreData", "No documents found")
             } else {
                val lockerList = documents.mapNotNull { document ->
                    try {
                        Log.d("FirestoreData", "Processing document: ${document.id}")
                        document.toObject(LockerModel::class.java)
                    } catch (e: Exception) {
                        Log.e("FirestoreError", "Error converting document", e)
                        null
                    }
                }
                _lockers.value = lockerList // Actualiza LiveData con la lista obtenida
                Log.d("lockers", "Lista de lockers cargada: ${lockerList.size} items")
            }
        }.addOnFailureListener { exception ->
            Log.w("LoadLockers", "Error getting documents: ", exception)
        }
    }



}