package com.example.locallockers

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locallockers.navigation.NavManager
import com.example.locallockers.ui.theme.LocalLockersTheme
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import com.google.android.gms.samples.pay.util.PaymentsUtil
import com.google.android.gms.samples.pay.viewmodel.CheckoutViewModel
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentDataRequest

//AIzaSyBTA_bquHKAeJfOOLKNX-RxaA4_Cr7iPao
class MainActivity : ComponentActivity() {
    private val googlePayRequestCode = 99942
    val checkoutViewModel: CheckoutViewModel by viewModels()
    val bookViewModel: BookViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginViewModel: LoginViewModel by viewModels()

        setContent {
            LocalLockersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavManager(loginViewModel,bookViewModel, checkoutViewModel)
                }
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API...")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == googlePayRequestCode) {
            Log.d("Pago", "onActivityResult llamado con requestCode: $requestCode y resultCode: $resultCode")
            when (resultCode) {
                RESULT_OK -> {
                    data?.let {
                        val paymentData = PaymentData.getFromIntent(it)
                        paymentData?.let {
                            // Procesa los datos del pago
                            checkoutViewModel.setPaymentData(paymentData)

                            // Obtén el bookId desde CheckoutViewModel
                            val bookId = checkoutViewModel.bookId
                            Log.d("Pago", "BookId obtenido en onActivityResult: $bookId")
                            if (bookId != null) {
                                // Actualiza el estado de la reserva
                                bookViewModel.updateReservationStatus(bookId, "Pagado")
                                Log.d("Pago", "Reserva $bookId actualizada a 'Pagado'")
                            } else {
                                Log.e("Pago", "El bookId es null después de la transacción")
                            }
                        }
                    }
                }

                RESULT_CANCELED -> {
                    Log.d("Pago", "Se ha cancelado la transacción")
                }

                else -> {
                    val status = AutoResolveHelper.getStatusFromIntent(data)
                    status?.let {
                        Log.e("GooglePay", "Error processing payment: ${it.statusCode}")
                    }
                }
            }
        }
    }
}