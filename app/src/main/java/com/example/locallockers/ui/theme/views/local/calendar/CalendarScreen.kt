package com.example.locallockers.ui.theme.views.local.calendar

import UserViewModel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    mapViewModel: MapViewModel,
) {

    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    val calendarViewModel: CalendarViewModel = viewModel()
    val selectedDate by calendarViewModel.selectedDate.observeAsState(System.currentTimeMillis())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(
                text = "Calendario",
                modifier = Modifier.padding(16.dp),  // Ejemplo de especificar un modificador
                textAlign = TextAlign.Center,        // Ejemplo de especificar alineación del texto
                fontSize = 18.sp                     // Especificar el tamaño del texto
            )

            },
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
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CalendarApp()
        }
    }
}