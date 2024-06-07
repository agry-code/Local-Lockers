package com.example.locallockers.ui.theme.views.tabs

import RegisterScreen
import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavController
import com.example.locallockers.R
import com.example.locallockers.ui.theme.views.Register.ui.RegisterViewModel
import com.example.locallockers.ui.theme.views.login.ui.LoginScreen
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.SearchViewModel
/**
 * Función composable que muestra una vista con pestañas para la navegación entre la pantalla de inicio de sesión y la pantalla de registro.
 *
 * @param navController el controlador de navegación utilizado para navegar entre pantallas
 */
@Composable
fun TabsViews(navController: NavController) {
    /* Variable para almacenar la pestaña seleccionada actualmente */
    var selectedTab by remember { mutableStateOf(0) }
    /* Lista de títulos de las pestañas */
    val tabs = listOf("Iniciar Sesion", "Registrarse")

    Column {
        /* TabRow para mostrar las pestañas en la parte superior */
        TabRow(selectedTabIndex = selectedTab,
            contentColor = colorResource(id = R.color.primary),
            containerColor = colorResource(id = R.color.transparent),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = colorResource(id = R.color.primary)
                )
            }) {
            /* Crear una pestaña para cada título en la lista de pestañas */
            tabs.forEachIndexed() { index, title ->
                Tab(selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(text = title) })
            }
        }
        /* Mostrar el contenido correspondiente a la pestaña seleccionada */
        when(selectedTab){
            0 -> LoginScreen(viewModel = LoginViewModel(), navController = navController)
            1 -> RegisterScreen(searchViewModel = SearchViewModel(), registerModel = RegisterViewModel(), navController = navController)
        }
    }
}