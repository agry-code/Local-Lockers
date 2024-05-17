package com.example.locallockers.ui.theme.views.turista.main.views.list

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
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

    fun updateReservationCapacity(lockerId: String, numberOfBags: Int, startDate: Date, endDate: Date) {
        val db = Firebase.firestore
        val lockerRef = db.collection("Lockers").document(lockerId)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        // Lista de fechas para las que se debe actualizar la capacidad
        val reservationDates = mutableListOf<String>()
        calendar.time = startDate
        while (!calendar.time.after(endDate)) {
            reservationDates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DATE, 1)
        }

        lockerRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val reservas = document.data?.get("reservas") as? Map<String, Map<String, Any>>
                if (reservas != null) {
                    val updates = mutableMapOf<String, Any>()
                    var allDaysHaveCapacity = true

                    // Primero verificamos que todos los días tengan suficiente capacidad
                    for (dateStr in reservationDates) {
                        val dayReservation = reservas[dateStr]
                        if (dayReservation != null) {
                            val currentCapacity = (dayReservation["capacidad"]?.toString()?.toIntOrNull() ?: 0)
                            if (currentCapacity < numberOfBags) {
                                allDaysHaveCapacity = false
                                break
                            }
                        } else {
                            allDaysHaveCapacity = false
                            break
                        }
                    }

                    // Si todos los días tienen suficiente capacidad, procedemos a actualizar
                    if (allDaysHaveCapacity) {
                        for (dateStr in reservationDates) {
                            val dayReservation = reservas[dateStr]!!
                            val currentCapacity = (dayReservation["capacidad"]?.toString()?.toIntOrNull() ?: 0)
                            val newCapacity = currentCapacity - numberOfBags
                            updates["reservas.$dateStr.capacidad"] = newCapacity
                        }

                        lockerRef.update(updates)
                            .addOnSuccessListener {
                                Log.d("UpdateCapacity", "Capacidad actualizada con éxito para todos los días")
                            }
                            .addOnFailureListener { e ->
                                Log.e("UpdateCapacityError", "Error actualizando la capacidad", e)
                            }
                    } else {
                        Log.d("UpdateCapacity", "No hay suficiente capacidad para todos los días")
                    }
                } else {
                    Log.d("UpdateCapacity", "No reservations map found")
                }
            } else {
                Log.d("UpdateCapacity", "Document not found")
            }
        }.addOnFailureListener { e ->
            Log.e("FirestoreError", "Error getting document", e)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getReservationForDate(lockerId: String, date: LocalDate): LiveData<Reservation?> {
        val result = MutableLiveData<Reservation?>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = dateFormat.format(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()))

        val reservationRef = Firebase.firestore.collection("Lockers").document(lockerId)
        reservationRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val reservas = document.data?.get("reservas") as? Map<String, Map<String, Any>>
                reservas?.let {
                    val reservationData = it[dateStr]
                    reservationData?.let {
                        val capacidad = (it["capacidad"]?.toString()?.toIntOrNull() ?: 0)
                        val precio = it["precio"] as? Double ?: 0.0
                        result.value = Reservation(capacidad, precio)
                    } ?: run {
                        Log.d("GetReservationForDate", "No reservation found for $dateStr")
                        result.value = null
                    }
                } ?: run {
                    Log.d("GetReservationForDate", "No reservations map found")
                    result.value = null
                }
            } else {
                Log.d("GetReservationForDate", "Document not found")
                result.value = null
            }
        }.addOnFailureListener {
            Log.e("FirestoreError", "Error getting document", it)
            result.value = null
        }

        return result
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


    fun createReservation(
        userId: String,
        userEmail: String,
        lockerId: String,
        lockerName: String,
        startTime: java.sql.Timestamp,
        endTime: java.sql.Timestamp,
        userName: String
    ) {
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