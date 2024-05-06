package com.example.locallockers.ui.theme.views.turista.main.views.maps

import UserViewModel
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locallockers.model.LockerModel
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.list.ShowReservationDialog
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
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
                val markerState = rememberMarkerState(position = LatLng(locker.latitude!!, locker.longitude!!))
                Marker(
                    state = markerState,
                    title = locker.name,
                    snippet = "Capacidad: ${locker.capacity}\nHorario: ${locker.openHours}",
                    onClick = {
                        if (locker.capacity > 0) {
                            selectedLocker = locker
                            showDialog = true
                        }
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
        ShowReservationDialog(
            locker = selectedLocker!!,
            onDismiss = { showDialog = false },
            onConfirm = { numberOfBags ->
                val startTime = Timestamp.now()  // Asumiendo que la reserva comienza ahora
                val endTime = Timestamp(Date(System.currentTimeMillis() + 86400000))  // Asumiendo reserva de un día
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