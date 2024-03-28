package com.example.locallockers.navigation

import RegisterScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locallockers.ui.theme.views.Register.ui.RegisterViewModel
import com.example.locallockers.ui.theme.views.login.ui.LoginScreen
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.tabs.TabsViews
import com.example.locallockers.ui.theme.views.turista.main.MainScreen
import com.example.locallockers.ui.theme.views.turista.main.MainViewModel

@Composable
fun NavManager(mainViewModel: MainViewModel){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "TabsViews" ){
        composable("TabsViews"){
            TabsViews(navController)
        }
        composable("Main"){
            MainScreen(navController,mainViewModel)
        }
    }
}