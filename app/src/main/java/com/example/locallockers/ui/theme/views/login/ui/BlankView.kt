package com.example.locallockers.ui.theme.views.login.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
/**
 * Función composable que verifica si el usuario actual está autenticado y redirige a la pantalla correspondiente.
 * Si el usuario está autenticado, navega a la pantalla de configuración ("Confi").
 * Si el usuario no está autenticado, navega a la pantalla de vistas de pestañas ("TabsViews").
 *
 * @param navController el controlador de navegación utilizado para navegar entre pantallas
 */
@Composable
fun BlankView(navController: NavController){
    LaunchedEffect(Unit){
        if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            navController.navigate("Confi")
        }else{
            navController.navigate("TabsViews")
        }
    }
}