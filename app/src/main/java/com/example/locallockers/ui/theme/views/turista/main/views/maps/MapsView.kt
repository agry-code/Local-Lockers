package com.example.locallockers.ui.theme.views.turista.main.views.maps

import UserViewModel
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locallockers.R
import com.example.locallockers.model.LockerModel
import com.example.locallockers.model.Reservation
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import dateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

    var showInformativeDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

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

                lockerViewModel.updateReservationCapacity(selectedLocker!!.id,
                    numberOfBags,
                    startTime,
                    endTime,
                    onSuccess = {
                        val dateOnlyFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dialogTitle = "Reserva Exitosa"
                        dialogMessage = "Reserva realizada con éxito para los días ${
                            dateOnlyFormat.format(startDate)
                        } al ${dateOnlyFormat.format(endDate)}."
                        showInformativeDialog = true
                    },
                    onFailure = { insufficientDate ->
                        dialogTitle = "Capacidad Insuficiente"
                        dialogMessage = "No hay suficiente capacidad para el día $insufficientDate."
                        showInformativeDialog = true
                    }
                )
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
    reservation: Reservation?,
    onDismiss: () -> Unit,
    onConfirm: (Int, Date, Date) -> Unit,
    context: Context = LocalContext.current  // Obtiene el contexto actual
) {
    if (reservation != null) {
        var numberOfBagsText by remember { mutableStateOf("1") } // Manejo del número de bolsas como texto para entrada flexible
        var startDate by remember { mutableStateOf<Date?>(null) }
        var endDate by remember { mutableStateOf<Date?>(null) }
        val startDateText = remember { mutableStateOf("") }
        val endDateText = remember { mutableStateOf("") }
        var errorText by remember { mutableStateOf("") } // Para mostrar mensajes de error

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Reserva para ${locker.name}") },
            text = {
                Column {
                    Text("Capacidad disponible: ${reservation.capacidad}")
                    Text("Precio por bolsa: ${reservation.precio}")
                    TextField(
                        value = numberOfBagsText,
                        onValueChange = { newValue ->
                            numberOfBagsText = newValue
                            val num = newValue.toIntOrNull()
                            if (num != null && num > 0 && num <= reservation.capacidad) {
                                errorText = "" // No hay error, limpiar cualquier mensaje anterior
                            } else {
                                errorText =
                                    "Introduzca un número válido (1 a ${reservation.capacidad})"
                            }
                        },
                        label = { Text("Número de bolsas") },
                        isError = errorText.isNotEmpty() // Se activa el estado de error si hay un mensaje
                    )
                    if (errorText.isNotEmpty()) {
                        Text(
                            errorText,
                            color = MaterialTheme.colorScheme.error
                        ) // Muestra el mensaje de error
                    }
                    Text("Fecha de entrada:")
                    TextField(
                        value = startDateText.value,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("DD/MM/AAAA") },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Build, contentDescription = "Seleccionar fecha",
                                modifier = Modifier.clickable {
                                    showDatePicker(context, null, startDate ?: Date()) { newDate ->
                                        startDate = newDate
                                        startDateText.value = SimpleDateFormat(
                                            "dd/MM/yyyy",
                                            Locale.getDefault()
                                        ).format(newDate)
                                    }
                                }
                            )
                        }
                    )
                    Text("Fecha de salida:")
                    TextField(
                        value = endDateText.value,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("DD/MM/AAAA") },
                        trailingIcon = {
                            Icon(Icons.Default.Build, contentDescription = "Seleccionar fecha",
                                modifier = Modifier.clickable {
                                    startDate?.let {
                                        showDatePicker(context, it, endDate ?: it) { newDate ->
                                            endDate = newDate
                                            endDateText.value = SimpleDateFormat(
                                                "dd/MM/yyyy",
                                                Locale.getDefault()
                                            ).format(newDate)
                                        }
                                    }
                                }
                            )
                        }
                    )

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val numberOfBags = numberOfBagsText.toIntOrNull()
                        if (startDate != null && endDate != null && numberOfBags != null && numberOfBags > 0 && errorText.isEmpty()) {
                            onConfirm(numberOfBags, startDate!!, endDate!!)
                        } else {
                            errorText = "Revise los datos introducidos."
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    )
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    )
                ) {
                    Text("Cancelar")
                }
            }
        )
    } else {
        // Manejo cuando no hay reservas
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("No hay reservas disponibles hoy para ${locker.name}") },
            confirmButton = {
                Button(onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    )) {
                    Text("Cerrar")
                }
            }
        )
    }
}


fun showDatePicker(
    context: Context,
    minDate: Date? = null,
    currentDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val calendar = Calendar.getInstance().apply {
        time = currentDate
    }
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            onDateSelected(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = minDate?.time
            ?: System.currentTimeMillis() // Establece la fecha mínima a hoy si minDate es null
    }
    datePickerDialog.show()
}
