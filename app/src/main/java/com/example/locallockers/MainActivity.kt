package com.example.locallockers

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.example.locallockers.navigation.NavManager
import com.example.locallockers.ui.theme.LocalLockersTheme
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel

//AIzaSyBTA_bquHKAeJfOOLKNX-RxaA4_Cr7iPao
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginViewModel : LoginViewModel by viewModels()

        setContent {
            LocalLockersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavManager(loginViewModel)
                }
            }
        }
    }
}