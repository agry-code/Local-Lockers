package com.example.locallockers.ui.theme.views.turista.main.views.book

import BookModel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow


class BookViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val reservationsFlow = MutableStateFlow<List<BookModel>>(emptyList())
    val reservations = reservationsFlow.asLiveData()
    fun loadReservations(userId: String, role: String) {
        Log.d("ProblemaReserva", "Role loadReservations ${role}")
        Log.d("ProblemaReserva", "UserId loadReservations ${userId}")

        if (role == "Huesped") {
            loadReservationsForGuest(userId)
        } else {
            loadReservationsForUser(userId)
        }
    }

    fun updateReservationStatus(reservationId: String, newStatus: String) {
        db.collection("Reservations").document(reservationId)
            .update("status", newStatus)
            .addOnSuccessListener {
                Log.d("ReservationUpdate", "Reservation status updated successfully")
            }
            .addOnFailureListener { e ->
                Log.d("ReservationUpdate", "Error updating reservation status", e)
            }
    }

    //Métodos para cargar Turista y Huesped
    private fun loadReservationsForUser(userId: String) {
        db.collection("Reservations")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                val fetchedReservations = mutableListOf<BookModel>()
                for (doc in value!!) {
                    doc.toObject<BookModel>()?.let {
                        fetchedReservations.add(it)
                    }
                }
                reservationsFlow.value = fetchedReservations
            }
    }


    private fun loadReservationsForGuest(userId: String) {
        getLockerIdFromUserId(userId) { lockerId ->
            if (lockerId != null) {
                db.collection("Reservations")
                    .whereEqualTo("lockerId", lockerId)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            println("Listen failed: $e")
                            return@addSnapshotListener
                        }

                        val fetchedReservations = mutableListOf<BookModel>()
                        for (doc in value!!) {
                            doc.toObject(BookModel::class.java)?.let {
                                fetchedReservations.add(it)
                            }
                        }
                        reservationsFlow.value = fetchedReservations
                    }
            } else {
                println("No lockerId found for userId: $userId")
            }
        }
    }


    private fun getLockerIdFromUserId(userId: String, callback: (String?) -> Unit) {
        db.collection("Users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    println("No matching user found")
                    callback(null)
                } else {
                    val lockerId = documents.documents[0].getString("lockerId")
                    callback(lockerId)
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
                callback(null)
            }
    }

    fun loadRequest(userId: String, role: String) {
        if (role == "Huesped") {
            getLockerIdFromUserId(userId) { lockerId ->
                if (lockerId != null) {
                    db.collection("Reservations")
                        .whereEqualTo("lockerId", lockerId)
                        .whereEqualTo("status", "pendiente")  // Añadido filtro por estado pendiente
                        .addSnapshotListener { value, e ->
                            if (e != null) {
                                Log.e("FirestoreError", "Listen failed.", e)
                                return@addSnapshotListener
                            }

                            val pendingReservations = mutableListOf<BookModel>()
                            for (doc in value!!) {
                                doc.toObject(BookModel::class.java)?.let { reservation ->
                                    if (reservation.status == "pendiente") {  // Comprobación adicional si es necesaria
                                        pendingReservations.add(reservation)
                                    }
                                }
                            }
                            reservationsFlow.value = pendingReservations
                        }
                } else {
                    Log.e("FirestoreError", "No lockerId found for userId: $userId")
                }
            }
        }
    }


}