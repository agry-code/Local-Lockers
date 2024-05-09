package com.example.locallockers.ui.theme.views.local.calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

class CalendarViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _reservaDetails = MutableLiveData<Map<String, Any>>()
    val reservaDetails: LiveData<Map<String, Any>> = _reservaDetails

    private val _snackbarMessage = MutableLiveData<String>()
    val snackbarMessage: LiveData<String> = _snackbarMessage

    fun loadReservaDetails(lockerId: String, date: String) {
        db.collection("Lockers").document(lockerId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val reservas = document.data?.get("reservas") as? Map<String, Any>
                    val detallesFecha = reservas?.get(date) as? Map<String, Any>
                    _reservaDetails.postValue(detallesFecha ?: emptyMap())
                } else {
                    _reservaDetails.postValue(emptyMap())
                }
            }
            .addOnFailureListener { e ->
                // Manejar el error, podría ser loggear o mostrar un mensaje de error.
            }
    }

    // Función para guardar la capacidad y precio modificados

    private fun isNumeric(input: String): Boolean {
        return input.all { it.isDigit() || it == '.' }
    }

    fun saveLockerDetails(
        lockerId: String,
        date: LocalDate,
        newCapacity: String,
        newPrice: String
    ) {
        if (newCapacity.isNotEmpty() && newPrice.isNotEmpty() && isNumeric(newCapacity) && isNumeric(newPrice)) {
            try {
                val reservaUpdate = hashMapOf(
                    "reservas.$date.capacidad" to newCapacity.toInt(),
                    "reservas.$date.precio" to newPrice.toDouble()
                )

                db.collection("Lockers")
                    .document(lockerId)
                    .update(reservaUpdate as Map<String, Any>)
                    .addOnSuccessListener {
                        _snackbarMessage.postValue("Datos de la reserva guardados correctamente.")
                    }
                    .addOnFailureListener { e ->
                        _snackbarMessage.postValue("Error al guardar los datos: ${e.message}")
                    }
            } catch (e: NumberFormatException) {
                _snackbarMessage.postValue("Por favor, asegúrate de que la capacidad y el precio son números válidos.")
            }
        } else {
            _snackbarMessage.postValue("Capacidad y precio deben ser completados con números válidos.")
        }
    }
}