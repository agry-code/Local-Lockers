package com.example.locallockers.navigation

import RegisterScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locallockers.ui.theme.Register.ui.RegisterViewModel
import com.example.locallockers.ui.theme.login.ui.LoginScreen
import com.example.locallockers.ui.theme.login.ui.LoginViewModel

@Composable
fun NavManager(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "Login" ){
        composable("Login"){
            LoginScreen(LoginViewModel(),navController)
        }
        composable("Register"){
            RegisterScreen(RegisterViewModel(),navController)
        }
    }
}