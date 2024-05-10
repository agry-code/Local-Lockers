package com.example.locallockers.ui.theme.views.turista.main.views.maps

import UserViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locallockers.model.LockerModel
import com.example.locallockers.model.Reservation
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.list.parseDate
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.util.Date

@Composable
fun MapsView(lockers: List<LockerModel>, lockerViewModel: LockerViewModel) {
    val malagaCenter = LatLng(36.71407355134514, -4.424203447255011)
    val cameraPosition = CameraPosition.fromLatLngZoom(malagaCenter, 13f)

    val cameraState = rememberCameraPositionState { position = cameraPosition }
    var mapLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedLocker by remember { mutableStateOf<LockerModel?>(null) }
    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraState,
            onMapLoaded = {
                mapLoading = false
            }
        ) {
            lockers.forEach { locker ->
                val markerState =
                    rememberMarkerState(position = LatLng(locker.latitude!!, locker.longitude!!))
                Marker(
                    state = markerState,
                    title = locker.name,
                    snippet = "Horario: ${locker.openHours}",
                    onClick = {
                            selectedLocker = locker
                            showDialog = true
                        true // Indica que el evento de clic ha sido manejado
                    }
                )
            }

        }

        if (mapLoading) {
            AnimatedVisibility(
                visible = mapLoading,
                modifier = Modifier.matchParentSize(),
                enter = EnterTransition.None,
                exit = fadeOut()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.wrapContentSize()
                )
            }
        }
    }
    if (showDialog && selectedLocker != null) {
        val todayReservation by lockerViewModel.getTodayReservation(selectedLocker!!.id)
            .observeAsState()
        ShowReservationDialog(
            locker = selectedLocker!!,
            reservation = todayReservation,
            onDismiss = { showDialog = false },
            onConfirm = { numberOfBags, startDate, endDate ->
                // Convertir las fechas al tipo java.sql.Timestamp
                val startTime = java.sql.Timestamp(startDate.time)
                val endTime = java.sql.Timestamp(endDate.time)

                lockerViewModel.updateReservationCapacity(selectedLocker!!.id,numberOfBags, startTime)
                lockerViewModel.createReservation(
                    user!!.userId,
                    user!!.email,
                    selectedLocker!!.id,
                    selectedLocker!!.name,
                    startTime,
                    endTime,
                    user!!.userName
                )

                // Cerrar el diálogo
                showDialog = false
            }
        )
    }
}

@Composable
fun ShowReservationDialog(
    locker: LockerModel,
    reservation: Reservation?, // Asume que este es el modelo de datos para la reserva
    onDismiss: () -> Unit,
    onConfirm: (Int, Date, Date) -> Unit
) {
    if (reservation != null) {
        var numberOfBags by remember { mutableStateOf(1) } // Estado inicial
        val (startDateText, setStartDateText) = remember { mutableStateOf("") }
        val (endDateText, setEndDateText) = remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Reserva para ${locker.name}") },
            text = {
                Column {
                    if (reservation != null) {
                        Text("Capacidad disponible: ${reservation.capacidad}")
                        Text("Precio por bolsa: ${reservation.precio}")
                        TextField(
                            value = numberOfBags.toString(),
                            onValueChange = { numberOfBags = it.toIntOrNull() ?: 1 },
                            label = { Text("Número de bolsas") }
                        )
                        Text("Fecha de entrada:")
                        TextField(
                            value = startDateText,
                            onValueChange = setStartDateText,
                            label = { Text("DD/MM/AAAA") }
                        )
                        Text("Fecha de salida:")
                        TextField(
                            value = endDateText,
                            onValueChange = setEndDateText,
                            label = { Text("DD/MM/AAAA") }
                        )
                    } else {
                        Text("No hay reservas disponibles para hoy.")
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val startDate = parseDate(startDateText)
                    val endDate = parseDate(endDateText)
                    if (startDate != null && endDate != null && numberOfBags > 0) {
                        onConfirm(numberOfBags, startDate, endDate)
                    } else {
                        // Aquí puedes manejar un error si las fechas no son válidas
                    }
                    onDismiss()
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    } else {
        // Código para manejar la situación donde no hay reserva para hoy
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("No hay reservas disponibles hoy para ${locker.name}") },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        )
    }
}
