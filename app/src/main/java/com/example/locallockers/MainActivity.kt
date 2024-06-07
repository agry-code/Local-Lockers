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
import com.example.locallockers.ui.theme.views.login.ui.LoginViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.book.BookViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.list.LockerViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import com.google.android.gms.samples.pay.viewmodel.CheckoutViewModel
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData

/**
 * MainActivity es la actividad principal de la aplicación "Local Lockers".
 * Gestiona la configuración inicial y la navegación de la aplicación.
 */
class MainActivity : ComponentActivity() {

    // Código de solicitud para Google Pay
    private val googlePayRequestCode = 99942

    // ViewModels utilizados en la actividad
    val loginViewModel: LoginViewModel by viewModels()
    val checkoutViewModel: CheckoutViewModel by viewModels()
    val bookViewModel: BookViewModel by viewModels()
    val mapViewModel: MapViewModel by viewModels()
    val lockerViewModel: LockerViewModel by viewModels()
    val userViewModel: UserViewModel by viewModels()
    /**
     * onCreate es llamado cuando la actividad es creada por primera vez.
     * Configura el contenido de la pantalla utilizando Jetpack Compose.
     *
     * @param savedInstanceState Estado previamente guardado de la instancia.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            LocalLockersTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Inicializa el gestor de navegación con los ViewModels necesarios
                    NavManager(loginViewModel,bookViewModel, checkoutViewModel, mapViewModel, lockerViewModel, userViewModel)
                }
            }
        }
    }

    /**
     * onActivityResult es llamado cuando una actividad lanzada con startActivityForResult finaliza.
     * Maneja el resultado de la transacción de Google Pay.
     *
     * @param requestCode Código de solicitud con el que se inició la actividad.
     * @param resultCode Código de resultado devuelto por la actividad.
     * @param data Intent que contiene los datos resultantes.
     */
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
                                Toast.makeText(this,
                                    getString(R.string.pago_realizado_con_xito), Toast.LENGTH_LONG).show()
                            } else {
                                Log.e("Pago", "El bookId es null después de la transacción")
                                Toast.makeText(this,
                                    getString(R.string.error_bookid_es_null_despu_s_de_la_transacci_n), Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

                RESULT_CANCELED -> {
                    Log.d("Pago", "Se ha cancelado la transacción")
                    Toast.makeText(this,
                        getString(R.string.se_ha_cancelado_la_transacci_n), Toast.LENGTH_LONG).show()
                }

                else -> {
                    val status = AutoResolveHelper.getStatusFromIntent(data)
                    status?.let {
                        Log.e("GooglePay", "Error processing payment: ${it.statusCode}")
                        Toast.makeText(this,
                            getString(R.string.error_en_el_procesamiento_del_pago + it.statusCode), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}