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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListadoScreen(
    navController: NavController,
    mapViewModel: MapViewModel = viewModel(),
    lockerViewModel: LockerViewModel = viewModel()
) {
    val lockers by lockerViewModel.lockers.observeAsState(initial = emptyList())
    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedLocker by remember { mutableStateOf<LockerModel?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var neededCapacity by remember { mutableStateOf(0) }
    var dateText by remember { mutableStateOf("") }

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
                }
            )
        },
        bottomBar = {
            BottomNav(navController, user?.role ?: "Turista")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TextField(
                value = dateText,
                onValueChange = { dateText = it },
                label = { Text("Fecha (YYYY-MM-DD)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = {
                selectedDate = LocalDate.parse(dateText)
            }) {
                Text("Filtrar Lockers")
            }
            LazyColumn {
                items(lockers) { locker ->
                    LockerItem(locker,selectedDate,lockerViewModel) {
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
                            startTime
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
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LockerItem(locker: LockerModel, date: LocalDate, lockerViewModel: LockerViewModel, onItemClicked: (LockerModel) -> Unit) {
    val reservation by lockerViewModel.getReservationForDate(locker.id, date).observeAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
//            .clickable { onItemClicked(locker) }
            .clickable(enabled = reservation != null && reservation!!.capacidad > 0) {
                onItemClicked(locker)
            }
            .padding(8.dp)
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

// Función de ayuda para parsear fechas
fun parseDate(dateStr: String): Date? {
    return try {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dateStr)
    } catch (e: ParseException) {
        null
    }
}


