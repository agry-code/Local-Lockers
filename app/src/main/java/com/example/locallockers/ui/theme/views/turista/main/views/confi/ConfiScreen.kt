package com.example.locallockers.ui.theme.views.turista.main.views.confi

import UserViewModel
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiScreen(navController: NavController, mapViewModel: MapViewModel, userRol: String) {
    val userViewModel : UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Configuración") },
                navigationIcon = {
                    IconButton(onClick = {
                        mapViewModel.signOut()
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
                    }
                })
        },
        bottomBar = {
            BottomNav(navController,userRol)
            Log.d("ProblemaRol","User Confi: ${user.toString()}")
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileCardUI(user)
            GeneralConfiUI(userViewModel)
            SupportConfiUI()
        }
    }
}

