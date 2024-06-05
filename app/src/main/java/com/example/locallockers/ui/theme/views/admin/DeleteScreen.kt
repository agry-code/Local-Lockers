package com.example.locallockers.ui.theme.views.admin

import UserViewModel
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.locallockers.R
import com.example.locallockers.model.LockerModel
import com.example.locallockers.model.UserModel
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    mapViewModel: MapViewModel,
    lockerViewModel: LockerViewModel
) {
    val user by userViewModel.currentUser.observeAsState()
    val context = LocalContext.current
    val guests by userViewModel.guests.observeAsState(emptyList())
    var showDialog by remember { mutableStateOf(false) }
    var selectedGuest by remember { mutableStateOf<UserModel?>(null) }
    var selectedLocker by remember { mutableStateOf<LockerModel?>(null) }

    LaunchedEffect(Unit) {
        userViewModel.loadGuests()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Eliminar Huésped") },
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
                items(guests) { guest ->
                    val locker by lockerViewModel.getLockerByGuest(guest.userId).observeAsState()

                    GuestItem(
                        guest = guest,
                        locker = locker,
                        onDeleteClick = {
                            selectedGuest = guest
                            selectedLocker = locker
                            showDialog = true
                        }
                    )
                }
            }
        }

        if (showDialog && selectedGuest != null && selectedLocker != null) {
            ConfirmDeleteDialog(
                guestName = selectedGuest!!.userName,
                lockerName = selectedLocker?.name ?: "Sin asignar",
                onConfirm = {
                    userViewModel.deleteGuestAndLocker(selectedGuest!!.userId, selectedLocker?.id)
                    Toast.makeText(context, "Huésped ${selectedGuest!!.userName} eliminado", Toast.LENGTH_LONG).show()
                    showDialog = false
                },
                onCancel = {
                    showDialog = false
                    Toast.makeText(context, "Se ha cancelado la eliminación de ${selectedGuest!!.userName}", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}


@Composable
fun GuestItem(guest: UserModel, locker: LockerModel?, onDeleteClick: () -> Unit) {
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
            Text(text = "Nombre: ${guest.userName}")
            Text(text = "Locker: ${locker?.name ?: "Sin asignar"}")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    ) ) {
                    Text(text = "Eliminar")
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    guestName: String,
    lockerName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "Confirmar eliminación")
        },
        text = {
            Text("¿Está seguro de que desea eliminar al huésped $guestName, dueño del locker $lockerName?")
        },
        confirmButton = {
            Button(onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary),
                    contentColor = colorResource(id = R.color.white)
                )) {
                Text("Eliminar")
            }
        },
        dismissButton = {
            Button(onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary),
                    contentColor = colorResource(id = R.color.white)
                )) {
                Text("Cancelar")
            }
        }
    )
}