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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.R
import com.example.locallockers.model.LockerModel
import com.example.locallockers.navigation.BottomBarScreen
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel

// Indica que este código utiliza características experimentales de Material3
@OptIn(ExperimentalMaterial3Api::class)
// Función Composable que define la pantalla principal
@Composable
fun MainScreen(navController: NavController,
               mapViewModel: MapViewModel,
               lockerViewModel: LockerViewModel) {
    // Obtiene una instancia del ViewModel del usuario
    val userViewModel : UserViewModel = viewModel()
    // Observa los cambios en el usuario actual y los almacena en 'user'
    val user by userViewModel.currentUser.observeAsState()
    // Observa los cambios en la lista de lockers y los almacena en 'lockers'
    val lockers by mapViewModel.lockers.observeAsState(initial = emptyList())

    // Asegúrate de cargar los datos cuando sea necesario, puedes llamar a loadLockers en un evento de inicio adecuado
    LaunchedEffect(key1 = true) {
        mapViewModel.loadLockers()
    }

    // Diseño de la pantalla con Scaffold
    Scaffold(
        // Barra superior con título y botón de cierre de sesión
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.mapa)) },
                navigationIcon = {
                    IconButton(onClick = {
                        mapViewModel.signOut()
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
                    }
                },
                // Configuración de colores de la barra superior
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorResource(id = R.color.white),
                    titleContentColor = colorResource(id = R.color.primary),
                    navigationIconContentColor = colorResource(id = R.color.primary)
                )
            )
        },
        // Barra inferior de navegación
        bottomBar = {
            BottomNav(navController, user?.role ?: "Turista")
        }
    ) { pad ->
        // Contenido principal de la pantalla, columna con el mapa y un registro en la consola de la lista de lockers
        Column(
            modifier = Modifier.padding(pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MapsView(lockers, lockerViewModel)
            Log.d("lockers","lista de lockers${lockers}")
        }
    }
}