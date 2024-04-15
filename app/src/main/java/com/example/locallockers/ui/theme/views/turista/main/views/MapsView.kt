package com.example.locallockers.ui.theme.views.turista.main.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapsView() {
    val malaga = LatLng(36.71407355134514, -4.424203447255011)
    val markerState = rememberMarkerState(position = malaga)
    val cameraPosition = CameraPosition.fromLatLngZoom(malaga, 10f)
    val cameraState = rememberCameraPositionState { position = cameraPosition }
    var mapLoading by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraState,
            onMapLoaded = {
                mapLoading = false
            }
        ) {
            Marker(
                state = markerState,
                title = "Malagueto"
            )
        }
        if (mapLoading){
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
}