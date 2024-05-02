package com.example.locallockers.ui.theme.views.turista.main.views.book

import BookModel
import UserViewModel
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(navController: NavController, mapViewModel: MapViewModel = viewModel(), bookViewModel: BookViewModel = viewModel() ) {
    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    val reservations by bookViewModel.reservations.observeAsState(emptyList())

    LaunchedEffect(user?.userId, user?.role) {
        if (user?.userId != null && user?.role != null) {
            bookViewModel.loadReservations(user?.userId!!, user?.role!!)
        }
    }

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
                })
        },
        bottomBar = {
            BottomNav(navController, user?.role ?: "Turista")
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn {
                items(reservations) { reservation ->
                    ReservationItem(reservation, user?.role ?: "Turista")
                }
            }
        }
    }
}

@Composable
fun ReservationItem(reservation: BookModel, userRole: String) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            if(userRole == "Turista"){
                Text("Reserva en: ${reservation.lockerName}")
            }
            Text("Inicio: ${reservation.startTime}")
            Text("Fin: ${reservation.endTime}")
            if(userRole == "Huesped"){
                Text(text = "Email:  ${reservation.userEmail}")
                Text(text = "Turista:  ${reservation.userName}")
            }
        }
    }
}