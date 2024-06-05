package com.example.locallockers.navigation

import UserViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locallockers.ui.theme.views.admin.DeleteScreen
import com.example.locallockers.ui.theme.views.admin.DeleteViewModel
import com.example.locallockers.ui.theme.views.local.calendar.CalendarScreen
import com.example.locallockers.ui.theme.views.local.request.RequestScreen
import com.example.locallockers.ui.theme.views.login.ui.BlankView
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.tabs.TabsViews
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookScreen
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.list.ListadoScreen
import com.example.locallockers.ui.theme.views.turista.main.views.confi.ConfiScreen
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MainScreen
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import com.google.android.gms.samples.pay.viewmodel.CheckoutViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(
    loginViewModel: LoginViewModel,
    bookViewModel: BookViewModel,
    checkoutViewModel: CheckoutViewModel,
    mapViewModel: MapViewModel,
    lockerViewModel: LockerViewModel,
    userViewModel: UserViewModel,
    deleteViewModel: DeleteViewModel
) {
    val navController = rememberNavController()

    if (loginViewModel.isLoading) {
        CircularProgressIndicator()
    } else {
        NavHost(navController = navController, startDestination = "Blank") {
            composable("Blank") { BlankView(navController) }
            composable("TabsViews") { TabsViews(navController) }
            composable("Main") {MainScreen(navController, mapViewModel,lockerViewModel)}
            composable("Confi") { ConfiScreen(navController, mapViewModel) }
            composable("Listado") { ListadoScreen(navController, mapViewModel, lockerViewModel) }
            composable("Book") {BookScreen(navController, mapViewModel, bookViewModel, checkoutViewModel)}
            composable("Request") { RequestScreen(navController) }
            composable("Calendar") {CalendarScreen(navController,mapViewModel)}
            composable("Delete"){ DeleteScreen(navController, userViewModel, mapViewModel,lockerViewModel)}
        }
    }
}

