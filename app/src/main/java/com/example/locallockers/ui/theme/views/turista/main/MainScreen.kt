package com.example.locallockers.ui.theme.views.turista.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: NavController, mainViewModel: MainViewModel) {
    Scaffold (
        topBar = {
            TopAppBar(title = { Text(text = "Turista")},
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                          mainViewModel.signOut()
                        navController.popBackStack()
                    }
                })
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