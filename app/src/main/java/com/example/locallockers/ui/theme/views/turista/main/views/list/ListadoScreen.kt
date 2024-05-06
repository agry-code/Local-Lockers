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
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.util.Date

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
            TextField(
                value = neededCapacity.toString(),
                onValueChange = { neededCapacity = it.toIntOrNull() ?: 0 },
                label = { Text("Capacidad necesaria") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = {
                selectedDate = LocalDate.parse(dateText)
                //lockerViewModel.filterLockersByDateAndCapacity(selectedDate, neededCapacity)
            }) {
                Text("Filtrar Lockers")
            }
            LazyColumn {
                items(lockers) { locker ->
                    LockerItem(locker) {
                        selectedLocker = locker
                        showDialog = true
                    }
                }
            }
            if (showDialog && selectedLocker != null && selectedLocker!!.capacity!=0) {
                ShowReservationDialog(
                    locker = selectedLocker!!,
                    onDismiss = { showDialog = false },
                    onConfirm = { numberOfBags ->
                        val startTime = Timestamp.now()  // Asumiendo que la reserva comienza ahora
                        val endTime =  Timestamp(Date(System.currentTimeMillis() + 86400000))  // Asumiendo reserva de un día
                        lockerViewModel.reserveLocker(selectedLocker!!.id, numberOfBags)
                        lockerViewModel.createReservation(
                            user!!.userId,
                            user!!.email,
                            selectedLocker!!.id,
                            selectedLocker!!.name,
                            startTime,
                            endTime,
                            user!!.userName
                        )
                        showDialog = false
                    }
                )
            }
        }
    }
}


@Composable
fun LockerItem(locker: LockerModel, onItemClicked: (LockerModel) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(locker) }
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${locker.name}")
            Text("Horario: ${locker.openHours}")
            if (locker.capacity == 0) {
                Text("Este locker está lleno", color = MaterialTheme.colorScheme.error)
            }else{
                Text("Capacidad: ${locker.capacity} maletas")
            }
        }
    }
}

@Composable
fun ShowReservationDialog(locker: LockerModel, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    val (text, setText) = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reservar en ${locker.name}") },
        text = {
            Column {
                Text("Indica cuántas maletas quieres reservar:")
                // Asegurarse de que solo se introduzcan números
                TextField(
                    value = text,
                    onValueChange = { newText ->
                        if (newText.all { it.isDigit() }) setText(newText)
                    },
                    label = { Text("Número de maletas") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Verificar que el texto no esté vacío y sea un número válido
                    text.toIntOrNull()?.let {
                        onConfirm(it)
                    }
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
