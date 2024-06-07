package com.example.locallockers.ui.theme.views.local.request

import BookModel
import UserViewModel
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.R
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
/**
 * Función composable que muestra la pantalla de solicitudes donde los usuarios pueden ver y gestionar sus solicitudes de reserva.
 * Incluye una barra superior con un botón de cierre de sesión, una lista de solicitudes de reserva pendientes y una barra de navegación inferior.
 *
 * @param navController el controlador de navegación utilizado para navegar entre pantallas
 * @param mapViewModel el ViewModel responsable de manejar las operaciones relacionadas con el mapa
 * @param bookViewModel el ViewModel responsable de manejar las operaciones relacionadas con las reservas
 */
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
            TopAppBar(
                title = { Text(text = "Lista de Solicitudes") },
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
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn {
                items(reservations) { reservation ->
                    RequestItem(
                        reservation,
                        onAccept = {
                            Log.d(
                                "ProblemaStatus",
                                "Reservation ID is null or invalid ${reservation.id}"
                            )
                            Log.d(
                                "ProblemaStatus",
                                "Reservation ID is null or invalid ${reservation.userName}"
                            )
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
/**
 * Función composable que muestra un solo elemento de solicitud con los detalles de una reserva y
 * botones para aceptar o rechazar la reserva.
 *
 * @param reservation los detalles de la reserva a mostrar
 * @param onAccept la función de callback a ejecutar cuando se hace clic en el botón de aceptar
 * @param onDecline la función de callback a ejecutar cuando se hace clic en el botón de rechazar
 */
@Composable
fun RequestItem(reservation: BookModel, onAccept: () -> Unit, onDecline: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.item),
            contentColor = colorResource(id = R.color.primary)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Solicitud de: ${reservation.userName}")
            Text("Para: ${reservation.lockerName}")
            Text("Desde: ${reservation.startTime} Hasta: ${reservation.endTime}")
            Row {
                Button(
                    onClick = { onAccept()
                        Toast.makeText(context, context.getString(R.string.reserva_aceptada), Toast.LENGTH_LONG).show();
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    )
                ) {
                    Text(stringResource(R.string.aceptar))
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = { onDecline()
                        Toast.makeText(context, context.getString(R.string.reserva_denegada), Toast.LENGTH_LONG).show();
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    )
                ) {
                    Text(stringResource(R.string.denegar))
                }
            }
        }
    }
}
