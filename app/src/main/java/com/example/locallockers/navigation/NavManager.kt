package com.example.locallockers.navigation

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locallockers.ui.theme.views.login.ui.BlankView
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.tabs.TabsViews
import com.example.locallockers.ui.theme.views.turista.main.views.confi.ConfiScreen
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MainScreen
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
@Composable
fun NavManager(loginViewModel: LoginViewModel = viewModel(), mapViewModel: MapViewModel = viewModel()) {
    val navController = rememberNavController()
    val userModel = loginViewModel.currentUser.collectAsState().value

    LaunchedEffect(userModel) {
        Log.d("ProblemaRol", "NM LaunchedEffect userModel: $userModel")
    }

    if (loginViewModel.isLoading) {
        CircularProgressIndicator()
        Log.d("ProblemaRol", "NM isLoading: userModel is $userModel")
    } else {
        Log.d("ProblemaRol", "NM Before NavHost: userModel is $userModel")
        NavHost(navController = navController, startDestination = "Blank") {
            composable("Blank") { BlankView(navController) }
            composable("TabsViews") { TabsViews(navController) }
            composable("Main") {
                Log.d("ProblemaRol", "NM In Main: userModel is $userModel")
                MainScreen(navController, mapViewModel, userModel?.role ?: "Turista")
            }
            composable("Confi") { ConfiScreen(navController, mapViewModel, userModel?.role ?: "Turista") }
        }
    }
}
