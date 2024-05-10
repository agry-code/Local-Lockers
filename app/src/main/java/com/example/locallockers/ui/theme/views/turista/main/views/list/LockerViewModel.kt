package com.example.locallockers.ui.theme.views.turista.main.views.list

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locallockers.model.LockerModel
import com.example.locallockers.model.Reservation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
    fun updateReservationCapacity(lockerId: String, numberOfBags: Int, reservationDate: Date) {
        val db = Firebase.firestore
        val lockerRef = db.collection("Lockers").document(lockerId)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = dateFormat.format(reservationDate)

        lockerRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val reservas = document.data?.get("reservas") as? Map<String, Map<String, Any>>
                reservas?.let {
                    val dayReservation = it[dateStr]
                    dayReservation?.let {
                        val currentCapacity = (it["capacidad"]?.toString()?.toIntOrNull() ?: 0)
                        if (currentCapacity >= numberOfBags) {
                            val newCapacity = currentCapacity - numberOfBags
                            val updatedReservations = HashMap(it)
                            updatedReservations["capacidad"] = newCapacity

                            // Actualizar solo el mapa de la reserva del día específico
                            lockerRef.update("reservas.$dateStr.capacidad", newCapacity)
                                .addOnSuccessListener {
                                    Log.d("UpdateCapacity", "Capacidad actualizada con éxito")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("UpdateCapacityError", "Error actualizando la capacidad", e)
                                }
                        } else {
                            Log.d("UpdateCapacity", "No hay suficiente capacidad")
                        }
                    } ?: Log.d("UpdateCapacity", "No reservation found for this day")
                } ?: Log.d("UpdateCapacity", "No reservations map found")
            } else {
                Log.d("UpdateCapacity", "Document not found")
            }
        }.addOnFailureListener { e ->
            Log.e("FirestoreError", "Error getting document", e)
        }
    }

    /**
     * Función para obtener el día de la reserva en el mapa. El mapa solo reserva para hoy.
     */
    fun getTodayReservation(lockerId: String): LiveData<Reservation?> {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val result = MutableLiveData<Reservation?>()
        val reservationRef = Firebase.firestore.collection("Lockers").document(lockerId)

        reservationRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val reservas = document.data?.get("reservas") as? Map<String, Map<String, Any>>
                reservas?.let {
                    val todayReserva = it[today]
                    todayReserva?.let {
                        val capacidad = (it["capacidad"]?.toString()?.toIntOrNull() ?: 0)
                        val precio = it["precio"] as? Double ?: 0.0
                        result.value = Reservation(capacidad, precio)
                    } ?: run {
                        Log.d("GetTodayReservation", "No reservation found for today")
                        result.value = null
                    }
                } ?: run {
                    Log.d("GetTodayReservation", "No reservations map found")
                    result.value = null
                }
            } else {
                Log.d("GetTodayReservation", "Document not found")
                result.value = null
            }
        }.addOnFailureListener {
            Log.e("FirestoreError", "Error getting document", it)
            result.value = null
        }

        return result
    }

    fun updateReservationCapacity(lockerId: String, numberOfBags: Int) {
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


    fun createReservation(userId: String, userEmail: String, lockerId: String, lockerName: String, startTime: java.sql.Timestamp, endTime: java.sql.Timestamp, userName: String) {
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