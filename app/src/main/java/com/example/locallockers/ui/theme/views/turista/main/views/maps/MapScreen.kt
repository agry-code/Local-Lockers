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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.navigation.BottomBarScreen
import com.example.locallockers.navigation.BottomNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, mapViewModel: MapViewModel) {
    val userViewModel : UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Turista") },
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
            Log.d("ProblemaRol","User Confi: ${user.toString()}")
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MapsView()
        }
    }
}