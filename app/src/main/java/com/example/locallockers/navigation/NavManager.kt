package com.example.locallockers.navigation

import UserViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.locallockers.ui.theme.views.admin.DeleteScreen
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

/**
 * Composable que gestiona la navegación en la aplicación utilizando NavController.
 * @param loginViewModel ViewModel para la gestión del login.
 * @param bookViewModel ViewModel para la gestión de reservas.
 * @param checkoutViewModel ViewModel para la gestión del proceso de pago.
 * @param mapViewModel ViewModel para la gestión de mapas.
 * @param lockerViewModel ViewModel para la gestión de taquillas.
 * @param userViewModel ViewModel para la gestión de usuarios.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavManager(
    loginViewModel: LoginViewModel,
    bookViewModel: BookViewModel,
    checkoutViewModel: CheckoutViewModel,
    mapViewModel: MapViewModel,
    lockerViewModel: LockerViewModel,
    userViewModel: UserViewModel,
) {
    /**
     * Inicializa NavController para gestionar la navegación.
     */
    val navController = rememberNavController()

    /**
     * Muestra un indicador de carga mientras el ViewModel de login está cargando.
     * Si no está cargando, configura NavHost para definir las rutas de navegación.
     */
    if (loginViewModel.isLoading) {
        CircularProgressIndicator()
    } else {
        NavHost(navController = navController, startDestination = "Blank") {
            composable("Blank") { BlankView(navController) }
            composable("TabsViews") { TabsViews(navController) }
            composable("Main") { MainScreen(navController, mapViewModel, lockerViewModel) }
            composable("Confi") { ConfiScreen(navController, mapViewModel) }
            composable("Listado") { ListadoScreen(navController, mapViewModel, lockerViewModel) }
            composable("Book") { BookScreen(navController, mapViewModel, bookViewModel, checkoutViewModel) }
            composable("Request") { RequestScreen(navController) }
            composable("Calendar") { CalendarScreen(navController, mapViewModel) }
            composable("Delete") { DeleteScreen(navController, userViewModel, mapViewModel, lockerViewModel) }
        }
    }
}