package com.example.locallockers.navigation

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
fun NavManager(loginViewModel: LoginViewModel, mapViewModel: MapViewModel) {
    val navController = rememberNavController()

    if (loginViewModel.isLoading) {
        CircularProgressIndicator()
    } else {
        NavHost(navController = navController, startDestination = "Blank") {
            composable("Blank") { BlankView(navController) }
            composable("TabsViews") { TabsViews(navController) }
            composable("Main") { MainScreen(navController, mapViewModel) }
            composable("Confi") { ConfiScreen(navController, mapViewModel) }
        }
    }
}
