package com.example.locallockers


import UserViewModel
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.locallockers.navigation.NavManager
import com.example.locallockers.ui.theme.LocalLockersTheme
import com.example.locallockers.ui.theme.views.admin.DeleteViewModel
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import com.google.android.gms.samples.pay.viewmodel.CheckoutViewModel
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData

//AIzaSyBTA_bquHKAeJfOOLKNX-RxaA4_Cr7iPao
class MainActivity : ComponentActivity() {
    private val googlePayRequestCode = 99942
    val loginViewModel: LoginViewModel by viewModels()
    val checkoutViewModel: CheckoutViewModel by viewModels()
    val bookViewModel: BookViewModel by viewModels()
    val mapViewModel: MapViewModel by viewModels()
    val lockerViewModel: LockerViewModel by viewModels()
    val userViewModel: UserViewModel by viewModels()
    val deleteViewModel: DeleteViewModel by viewModels()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            LocalLockersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavManager(loginViewModel,bookViewModel, checkoutViewModel, mapViewModel, lockerViewModel, userViewModel, deleteViewModel)
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
                                Toast.makeText(this, "Pago realizado con éxito", Toast.LENGTH_LONG).show()
                            } else {
                                Log.e("Pago", "El bookId es null después de la transacción")
                                Toast.makeText(this, "Error: bookId es null después de la transacción", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                RESULT_CANCELED -> {
                    Log.d("Pago", "Se ha cancelado la transacción")
                    Toast.makeText(this, "Se ha cancelado la transacción", Toast.LENGTH_LONG).show()
                }

                else -> {
                    val status = AutoResolveHelper.getStatusFromIntent(data)
                    status?.let {
                        Log.e("GooglePay", "Error processing payment: ${it.statusCode}")
                        Toast.makeText(this, "Error en el procesamiento del pago: ${it.statusCode}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}