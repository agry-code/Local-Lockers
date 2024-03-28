@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.locallockers.ui.theme.views.turista.main

import android.graphics.drawable.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController, mainViewModel: MainViewModel ) {
    Scaffold (
        topBar = {
            TopAppBar(title = { Text(text = "Turista")},
                navigationIcon = {
                    IconButton(onClick = {
                        mainViewModel.signOut()
                        navController.popBackStack()
                        }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "MainView!")
        }
    }
}