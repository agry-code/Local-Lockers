package com.example.locallockers.ui.theme.views.turista.main.views.list

import UserViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.model.LockerModel
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.ShowReservationDialog
import java.sql.Timestamp
import java.time.LocalDate
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.locallockers.R
import dateFormat
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(
    navController: NavController,
    mapViewModel: MapViewModel,
    lockerViewModel: LockerViewModel
) {
    // Aquí estamos dentro del contexto composable, por lo que es seguro obtener el contexto local
    val context = LocalContext.current
    val lockers by lockerViewModel.lockers.observeAsState(initial = emptyList())
    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedLocker by remember { mutableStateOf<LockerModel?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var dateText by remember { mutableStateOf("") }


    var showInformativeDialog by remember { mutableStateOf(false) }
    var dialogTitle by remember { mutableStateOf("") }
    var dialogMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Listado de Reservas") },
                navigationIcon = {
                    IconButton(onClick = {
                        mapViewModel.signOut()
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorResource(id = R.color.white),
                    titleContentColor = colorResource(id = R.color.primary),
                    navigationIconContentColor = colorResource(id = R.color.primary)
                )
            )
        },
        bottomBar = {
            BottomNav(navController, user?.role ?: "Turista")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Spacer(modifier = Modifier.padding(8.dp))
            TextField(
                value = dateText,
                onValueChange = { /* No se permite cambiar el valor porque es de solo lectura */ },
                label = { Text("Fecha (YYYY-MM-DD)", color = colorResource(id = R.color.primary)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = colorResource(id = R.color.primary),
                    unfocusedIndicatorColor = colorResource(id = R.color.secundary),
                    cursorColor = colorResource(id = R.color.primary),
                    disabledTextColor = colorResource(id = R.color.primary),
                    disabledLabelColor = colorResource(id = R.color.primary)
                )
            )
            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    showDatePicker(context, selectedDate) { newDate ->
                        selectedDate = newDate
                        dateText = newDate.toString()
                    }
                    Modifier.fillMaxWidth()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary),
                    contentColor = colorResource(id = R.color.white)
                )
            ) {
                Text("Seleccionar Fecha para filtrar")
            }
            Spacer(modifier = Modifier.padding(8.dp))
            LazyColumn {
                items(lockers) { locker ->
                    LockerItem(locker, selectedDate, lockerViewModel) {
                        selectedLocker = locker
                        showDialog = true
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
                        val startTime = Timestamp(startDate.time)
                        val endTime = Timestamp(endDate.time)
                        lockerViewModel.updateReservationCapacity(
                            selectedLocker!!.id,
                            numberOfBags,
                            startTime,
                            endTime,
                            onSuccess = {
                                val dateOnlyFormat =
                                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                dialogTitle = "Reserva Exitosa"
                                dialogMessage = "Reserva realizada con éxito para los días ${
                                    dateOnlyFormat.format(startDate)
                                } al ${dateOnlyFormat.format(endDate)}."
                                showInformativeDialog = true
                                lockerViewModel.createReservation(
                                    user!!.userId,
                                    user!!.email,
                                    selectedLocker!!.id,
                                    selectedLocker!!.name,
                                    startTime,
                                    endTime,
                                    user!!.userName
                                )
                            },
                            onFailure = { insufficientDate ->
                                dialogTitle = "Capacidad Insuficiente"
                                dialogMessage =
                                    "No hay suficiente capacidad para el día $insufficientDate."
                                showInformativeDialog = true
                                // hay que salir para que no se cree la reserva. No se debe crear porque no hay capacidad
                            }
                        )
                        // Cerrar el diálogo
                        showDialog = false

                    }
                )
            }
            if (showInformativeDialog) {
                InformativeDialog(
                    title = dialogTitle,
                    message = dialogMessage,
                    onDismiss = { showInformativeDialog = false }
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LockerItem(
    locker: LockerModel,
    date: LocalDate,
    lockerViewModel: LockerViewModel,
    onItemClicked: (LockerModel) -> Unit
) {
    val reservation by lockerViewModel.getReservationForDate(locker.id, date).observeAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = reservation != null && reservation!!.capacidad > 0) {
                onItemClicked(locker)
            }
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.item),
            contentColor = colorResource(id = R.color.primary)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${locker.name}", style = MaterialTheme.typography.titleMedium)
            Text("Horario: ${locker.openHours}", style = MaterialTheme.typography.bodySmall)
            if (reservation != null) {
                Text("Capacidad disponible: ${reservation!!.capacidad}")
                Text("Precio por bolsa: ${reservation!!.precio}")
            } else {
                Text("No hay información de reserva para esta fecha")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun showDatePicker(context: Context, currentDate: LocalDate, onDateSelected: (LocalDate) -> Unit) {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.YEAR, currentDate.year)
        set(Calendar.MONTH, currentDate.monthValue - 1)
        set(Calendar.DAY_OF_MONTH, currentDate.dayOfMonth)
    }

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            onDateSelected(LocalDate.of(year, monthOfYear + 1, dayOfMonth))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate =
            System.currentTimeMillis() // Establece la fecha mínima a hoy si minDate es null
    }

    datePickerDialog.show()
}


@Composable
fun InformativeDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary),
                    contentColor = colorResource(id = R.color.white)
                )
            ) {
                Text("OK")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = message)
        }
    )
}
