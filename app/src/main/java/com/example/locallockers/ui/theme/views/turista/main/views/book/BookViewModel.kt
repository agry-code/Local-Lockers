package com.example.locallockers.ui.theme.views.turista.main.views.book

import BookModel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.MutableStateFlow



// ViewModel para manejar las reservas
class BookViewModel : ViewModel() {
    // Instancia de Firestore para interactuar con la base de datos
    private val db = FirebaseFirestore.getInstance()

    // MutableStateFlow para almacenar las reservas, inicializado como una lista vacía
    private val reservationsFlow = MutableStateFlow<List<BookModel>>(emptyList())

    // LiveData derivada de reservationsFlow para observar los cambios en la interfaz de usuario
    val reservations = reservationsFlow.asLiveData()

    // Función para cargar las reservas según el rol del usuario
    fun loadReservations(userId: String, role: String) {
        if (role == "Huesped") {
            // Si el usuario es un huésped, cargar las reservas relacionadas con el locker del huésped
            loadReservationsForGuest(userId)
        } else {
            // Si el usuario es un turista, cargar las reservas relacionadas con el turista
            loadReservationsForUser(userId)
        }
    }

    // Función para actualizar el estado de una reserva
    fun updateReservationStatus(reservationId: String, newStatus: String) {
        // Actualizar el campo 'status' de la reserva en la colección 'Reservations' de Firestore
        db.collection("Reservations").document(reservationId)
            .update("status", newStatus)
            .addOnSuccessListener {
                // Log de éxito si la actualización es exitosa
                Log.d("ReservationUpdate", "Reservation status updated successfully")
            }
            .addOnFailureListener { e ->
                // Log de error si la actualización falla
                Log.d("ReservationUpdate", "Error updating reservation status", e)
            }
    }

    // Método para cargar reservas del usuario turista
    private fun loadReservationsForUser(userId: String) {
        // Escuchar cambios en las reservas donde el userId coincida
        db.collection("Reservations")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { value, e ->
                if (e != null) {
                    // Log de error si falla la escucha
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }
                // Convertir los documentos recibidos a objetos BookModel y almacenarlos en la lista
                val fetchedReservations = mutableListOf<BookModel>()
                for (doc in value!!) {
                    doc.toObject<BookModel>()?.let {
                        fetchedReservations.add(it)
                    }
                }
                // Actualizar el valor de reservationsFlow con la nueva lista de reservas
                reservationsFlow.value = fetchedReservations
            }
    }

    // Método para cargar reservas del huésped
    private fun loadReservationsForGuest(userId: String) {
        // Obtener el lockerId asociado al userId
        getLockerIdFromUserId(userId) { lockerId ->
            if (lockerId != null) {
                // Escuchar cambios en las reservas donde el lockerId coincida
                db.collection("Reservations")
                    .whereEqualTo("lockerId", lockerId)
                    .addSnapshotListener { value, e ->
                        if (e != null) {
                            // Log de error si falla la escucha
                            println("Listen failed: $e")
                            return@addSnapshotListener
                        }
                        // Convertir los documentos recibidos a objetos BookModel y almacenarlos en la lista
                        val fetchedReservations = mutableListOf<BookModel>()
                        for (doc in value!!) {
                            doc.toObject(BookModel::class.java)?.let {
                                fetchedReservations.add(it)
                            }
                        }
                        // Actualizar el valor de reservationsFlow con la nueva lista de reservas
                        reservationsFlow.value = fetchedReservations
                    }
            } else {
                // Log de error si no se encuentra lockerId para el userId
                println("No lockerId found for userId: $userId")
            }
        }
    }

    // Método para obtener el lockerId a partir del userId
    private fun getLockerIdFromUserId(userId: String, callback: (String?) -> Unit) {
        // Obtener el documento del usuario donde el userId coincida
        db.collection("Users")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // Log de error si no se encuentra ningún usuario
                    println("No matching user found")
                    callback(null)
                } else {
                    // Obtener el lockerId del primer documento encontrado
                    val lockerId = documents.documents[0].getString("lockerId")
                    callback(lockerId)
                }
            }
            .addOnFailureListener { exception ->
                // Log de error si falla la obtención de documentos
                println("Error getting documents: $exception")
                callback(null)
            }
    }

    // Método para cargar solicitudes pendientes para el rol de huésped
    fun loadRequest(userId: String, role: String) {
        if (role == "Huesped") {
            // Obtener el lockerId asociado al userId
            getLockerIdFromUserId(userId) { lockerId ->
                if (lockerId != null) {
                    // Escuchar cambios en las reservas pendientes donde el lockerId coincida
                    db.collection("Reservations")
                        .whereEqualTo("lockerId", lockerId)
                        .whereEqualTo("status", "pendiente")  // Añadido filtro por estado pendiente
                        .addSnapshotListener { value, e ->
                            if ( e != null) {
                                // Log de error si falla la escucha
                                Log.e("FirestoreError", "Listen failed.", e)
                                return@addSnapshotListener
                            }
                            // Convertir los documentos recibidos a objetos BookModel y almacenarlos en la lista
                            val pendingReservations = mutableListOf<BookModel>()
                            for (doc in value!!) {
                                doc.toObject(BookModel::class.java)?.let { reservation ->
                                    if (reservation.status == "pendiente") {  // Comprobación adicional si es necesaria
                                        pendingReservations.add(reservation)
                                    }
                                }
                            }
                            // Actualizar el valor de reservationsFlow con la nueva lista de reservas pendientes
                            reservationsFlow.value = pendingReservations
                        }
                } else {
                    // Log de error si no se encuentra lockerId para el userId
                    Log.e("FirestoreError", "No lockerId found for userId: $userId")
                }
            }
        }
    }
}