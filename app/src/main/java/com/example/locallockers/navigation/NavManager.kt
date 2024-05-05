package com.example.locallockers.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locallockers.ui.theme.views.local.request.RequestScreen
import com.example.locallockers.ui.theme.views.login.ui.BlankView
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.tabs.TabsViews
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookScreen
import com.example.locallockers.ui.theme.views.turista.main.views.list.ListadoScreen
import com.example.locallockers.ui.theme.views.turista.main.views.confi.ConfiScreen
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MainScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(loginViewModel: LoginViewModel) {
    val navController = rememberNavController()

    if (loginViewModel.isLoading) {
        CircularProgressIndicator()
    } else {
        NavHost(navController = navController, startDestination = "Blank") {
            composable("Blank") { BlankView(navController) }
            composable("TabsViews") { TabsViews(navController) }
            composable("Main") {
                MainScreen(
                    navController,
                    viewModel()
                )
            } // Usando viewModel aquí directamente
            composable("Confi") { ConfiScreen(navController, viewModel()) } // Igualmente aquí
            composable("Listado") { ListadoScreen(navController, viewModel(), viewModel()) }
            composable("Book") {
                BookScreen(navController, viewModel(), viewModel())
            }
            composable("Request"){ RequestScreen(navController = navController)}
        }
    }
}

