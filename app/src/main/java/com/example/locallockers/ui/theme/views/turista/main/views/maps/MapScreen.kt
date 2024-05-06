package com.example.locallockers.ui.theme.views.turista.main.views.maps

import UserViewModel
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.model.LockerModel
import com.example.locallockers.navigation.BottomBarScreen
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController,
               mapViewModel: MapViewModel,
               lockerViewModel: LockerViewModel) {
    val userViewModel : UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    val lockers by mapViewModel.lockers.observeAsState(initial = emptyList())

    // Asegúrate de cargar los datos cuando sea necesario, puedes llamar a loadLockers en un evento de inicio adecuado
    LaunchedEffect(key1 = true) {
        mapViewModel.loadLockers()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Turista") },
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
            Log.d("ProblemaRol","User Confi: ${user.toString()}")
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MapsView(lockers, lockerViewModel)
            Log.d("lockers","lista de lockers${lockers}")
        }
    }
}