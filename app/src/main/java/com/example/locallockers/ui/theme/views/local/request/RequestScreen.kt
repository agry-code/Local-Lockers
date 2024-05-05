package com.example.locallockers.ui.theme.views.local.request

import BookModel
import UserViewModel
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestScreen(
    navController: NavController,
    mapViewModel: MapViewModel = viewModel(),
    bookViewModel: BookViewModel = viewModel()
) {
    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    val reservations by bookViewModel.reservations.observeAsState(emptyList())

    LaunchedEffect(user?.userId, user?.role) {
        if (user?.userId != null && user?.role != null) {
            bookViewModel.loadRequest(
                user?.userId!!,
                user?.role!!
            ) //cargamos las reservas con estado pendiente
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Lista de Solicitudes") },
                navigationIcon = {
                    IconButton(onClick = {
                        mapViewModel.signOut()
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
                    }
                })
        },
        bottomBar = {
            BottomNav(navController, user?.role ?: "Turista")
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn {
                items(reservations) { reservation ->
                    RequestItem(
                        reservation,
                        onAccept = {
                            Log.d("ProblemaStatus", "Reservation ID is null or invalid ${reservation.id}")
                            Log.d("ProblemaStatus", "Reservation ID is null or invalid ${reservation.userName}")
                            if (reservation.id != null) {
                                bookViewModel.updateReservationStatus(reservation.id, "aceptada")
                            }
                        },
                        onDecline = {
                            if (reservation.id != null) {
                                bookViewModel.updateReservationStatus(reservation.id, "denegada")
                            }
                            Log.d("ProblemaStatus", "Error: Reservation ID is null or invalid")
                        }
                    )

                }
            }
        }
    }
}

@Composable
fun RequestItem(reservation: BookModel, onAccept: () -> Unit, onDecline: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Solicitud de: ${reservation.userName}")
            Text("Para: ${reservation.lockerName}")
            Text("Desde: ${reservation.startTime} Hasta: ${reservation.endTime}")
            Row {
                Button(onClick = { onAccept() }) {
                    Text("Aceptar")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { onDecline() }) {
                    Text("Denegar")
                }
            }
        }
    }
}
