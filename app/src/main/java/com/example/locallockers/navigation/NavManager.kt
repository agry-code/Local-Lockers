package com.example.locallockers.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locallockers.ui.theme.views.login.ui.BlankView
import com.example.locallockers.ui.theme.views.tabs.TabsViews
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MainScreen
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapsView

@Composable
fun NavManager(mapViewModel: MapViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Blank" ){
        composable("Blank"){
            BlankView(navController)
        }
        composable("TabsViews"){
            TabsViews(navController)
        }
        composable("Main"){
            MainScreen(navController, mapViewModel)
        }
        composable("List"){

        }

    }
}