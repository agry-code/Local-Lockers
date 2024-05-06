package com.example.locallockers.ui.theme.views.turista.main.views.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locallockers.model.LockerModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LockerViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _lockers = MutableLiveData<List<LockerModel>>()
    val lockers: LiveData<List<LockerModel>> = _lockers

    init {
        loadLockers()
    }

    private fun loadLockers() {
        db.collection("Lockers")
            .get()
            .addOnSuccessListener { result ->
                val lockerList = mutableListOf<LockerModel>()
                for (document in result) {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val capacity = document.getLong("capacity")?.toInt() ?: 0
                    val openHours = document.getString("openHours") ?: ""
                    val owner = document.getString("owner") ?: ""  // Extracción del campo 'owner'
                    val longitude = document.getDouble("longitude")
                    val latitude = document.getDouble("latitude")
                    lockerList.add(
                        LockerModel(
                            id = id,
                            name = name,
                            capacity = capacity,
                            openHours = openHours,
                            owner = owner,
                            longitude = longitude,
                            latitude = latitude

                            )
                    )
                }
                _lockers.value = lockerList
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error getting documents: ", exception)
            }
    }

//Se reserva hoy y acaba mañana
    fun reserveLocker(lockerId: String, numberOfBags: Int) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val lockerRef = db.collection("Lockers").document(lockerId)

                // Realizar la transacción y esperar su resultado
                val result = db.runTransaction { transaction ->
                    val snapshot = transaction.get(lockerRef)
                    val currentCapacity = snapshot.getLong("capacity") ?: 0

                    if (currentCapacity >= numberOfBags) {
                        val newCapacity = currentCapacity - numberOfBags
                        transaction.update(lockerRef, "capacity", newCapacity)
                        newCapacity
                    } else {
                        throw Exception("No hay suficiente capacidad")
                    }
                }.await()  // Espera a que la transacción se complete

                // Actualizar el LiveData dentro del scope correcto
                val updatedLockers = _lockers.value?.map { locker ->
                    if (locker.id == lockerId) locker.copy(capacity = result.toInt()) else locker
                }
                _lockers.postValue(updatedLockers ?: listOf())

            } catch (e: Exception) {
                // Manejar errores, como la falta de capacidad
                println("Transaction failure: $e")
            }
        }
    }

    fun createReservation(userId: String, userEmail: String, lockerId: String, lockerName: String, startTime: Timestamp, endTime: Timestamp, userName: String) {
        // Crear un nuevo documento con un ID generado automáticamente
        val newDocRef = db.collection("Reservations").document()

        // Crear un HashMap con los datos de la reserva y añadir el ID generado al mapa
        val reservation = hashMapOf(
            "id" to newDocRef.id, // Asignar el ID del documento aquí
            "userId" to userId,
            "userEmail" to userEmail,
            "lockerId" to lockerId,
            "lockerName" to lockerName,
            "startTime" to startTime,
            "endTime" to endTime,
            "userName" to userName,
            "status" to "pendiente"
        )

        // Usar set en lugar de add para usar el documento con el ID específico
        newDocRef.set(reservation)
            .addOnSuccessListener {
                Log.d("Firestore", "DocumentSnapshot written with ID: ${newDocRef.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding document", e)
            }
    }

}