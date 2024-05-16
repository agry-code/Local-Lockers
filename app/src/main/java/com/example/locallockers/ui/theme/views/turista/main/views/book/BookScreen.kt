package com.example.locallockers.ui.theme.views.turista.main.views.book

import BookModel
import UserViewModel
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import com.google.android.gms.samples.pay.util.PaymentsUtil
import com.google.android.gms.samples.pay.viewmodel.CheckoutViewModel
import com.google.android.gms.samples.pay.viewmodel.PaymentUiState
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.pay.button.ButtonTheme
import com.google.pay.button.ButtonType
import com.google.pay.button.PayButton
import kotlinx.coroutines.awaitAll


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookScreen(
    navController: NavController,
    mapViewModel: MapViewModel = viewModel(),
    bookViewModel: BookViewModel = viewModel(),
    checkoutViewModel: CheckoutViewModel,
) {
    val userViewModel: UserViewModel = viewModel()
    val user by userViewModel.currentUser.observeAsState()
    val books by bookViewModel.reservations.observeAsState(emptyList())
    val context = LocalContext.current
    val activity = context as? Activity


    LaunchedEffect(user?.userId, user?.role) {
        if (user?.userId != null && user?.role != null) {
            bookViewModel.loadReservations(user?.userId!!, user?.role!!)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Listado de Reservas") },
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
            BottomNav(navController, user?.role ?: "Turista")
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn {
                items(books) { book ->
                    BookItem(book, user?.role ?: "Turista", checkoutViewModel
                    ) {
                        activity?.let {
                            onGooglePayButtonClick(
                                it,
                                checkoutViewModel,
                                bookViewModel,
                                price = 1,
                                book.id
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookItem(
    book: BookModel, userRole: String, checkoutViewModel: CheckoutViewModel,
    onGooglePayButtonClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                checkoutViewModel.setBookId(book.id)  // Asegúrate de que esto se llama antes del proceso de pago
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (userRole == "Turista") {
                Text("Reserva en: ${book.lockerName}")
            }
            Text("Estado de la reserva: ${book.status}")
            Text("Inicio: ${book.startTime}")
            Text("Fin: ${book.endTime}")
            if (userRole == "Huesped") {
                Text(text = "Email:  ${book.userEmail}")
                Text(text = "Turista:  ${book.userName}")
            }
            if (userRole == "Turista" && book.status == "aceptada") {
                Spacer(modifier = Modifier.padding(8.dp))
                PayButton(
                    theme = ButtonTheme.Light,
                    type = ButtonType.Book,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("payButton"),
                    allowedPaymentMethods = PaymentsUtil.allowedPaymentMethods.toString(),
                    onClick = onGooglePayButtonClick
                )
            }
        }
    }
}

private val googlePayRequestCode = 99942
fun onGooglePayButtonClick(
    activity: Activity,
    checkoutViewModel: CheckoutViewModel,
    bookViewModel: BookViewModel,
    price: Long,
    bookId: String
) {
    Log.d("Pago", "Estableciendo bookId: $bookId")
    checkoutViewModel.setBookId(bookId)  // Asegúrate de que esto se llama antes del proceso de pago
    AutoResolveHelper.resolveTask(
        checkoutViewModel.getLoadPaymentDataTask(price),
        activity,
        googlePayRequestCode
    )
   // bookViewModel.updateReservationStatus(bookId, "Pagado")
}



