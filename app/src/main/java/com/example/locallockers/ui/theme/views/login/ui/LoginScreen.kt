package com.example.locallockers.ui.theme.views.login.ui

import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.locallockers.R
import com.example.locallockers.ui.theme.Composable.Alert
import com.example.locallockers.ui.theme.Composable.EmailField
import com.example.locallockers.ui.theme.Composable.PasswordField
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
/**
 * Composable que representa la pantalla de inicio de sesión.
 * Muestra un indicador de carga si isLoading es true, de lo contrario muestra el formulario de inicio de sesión.
 */
@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    val isLoading = viewModel.isLoading
    if (isLoading) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        Box(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Login(Modifier.align(Alignment.Center), viewModel, navController)
        }
    }
}
/**
 * Composable que muestra el formulario de inicio de sesión.
 * Incluye campos para email y contraseña, y botones para iniciar sesión.
 */
@Composable
fun Login(modifier: Modifier, viewModel: LoginViewModel, navController: NavController) {
    val email: String by viewModel.email.observeAsState(initial = "")
    val password: String by viewModel.password.observeAsState(initial = "")
    val loginEnable: Boolean by viewModel.loginEnable.observeAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                viewModel.signInWithGoogleCredential(credential) {
                    //función para navegar a un home screen
                    Log.d("LocakLocker", "Se ha logeado correctamente con SignIn")
                    navController.navigate("Confi")
                }
            } catch (ex: Exception) {
                Log.d("LocalLocker", "Google SignInFallo")
            }
        }

    Column(modifier = modifier) {
        HeaderImage(Modifier.align(Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.padding(16.dp))
        EmailField(email, { viewModel.onLoginChanged(it, password)})
        Spacer(modifier = Modifier.padding(4.dp))
        PasswordField(password, { viewModel.onLoginChanged(email, it) })
        Spacer(modifier = Modifier.padding(8.dp))
        //ForgotPassword(Modifier.align(Alignment.End)) FUTURA IMPLEMENTACIÓN
        Spacer(modifier = Modifier.padding(16.dp))
        LogginButton(loginEnable) {
            coroutineScope.launch {
                viewModel.login(email, password) {
                    navController.navigate("Confi")
                }
            }
        }
       // GoogleSignInButton(launcher) Futura implementación para tomar los datos del cliente
        if (viewModel.showAlert) {
            Alert(title = stringResource(R.string.alerta),
                msg = stringResource(R.string.usuario_o_contrase_a_erronea),
                confirmText = stringResource(id = R.string.aceptar),
                onConfirmClick = { viewModel.closeAlert() }) {
            }
        }
    }
}

/**
 * Botón para iniciar sesión con Google.
 * Configura e inicia la intención de inicio de sesión con Google.
 */
@Composable
fun GoogleSignInButton(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    val context = LocalContext.current
    Button(
        onClick = {
            // Aquí configuras y lanzas la intención de inicio de sesión con Google
            val signInClient = GoogleSignIn.getClient(
                context,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.token))
                    .requestEmail()
                    .build()
            )
            val signInIntent = signInClient.signInIntent
            launcher.launch(signInIntent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_google),
            contentDescription = "Cont descripcion",
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
        )
        Text(text = "Iniciar sesión con Google", color = Color.White)
    }
}
/**
 * Botón de inicio de sesión.
 * Desactivado si el loginEnable es false.
 */
@Composable
fun LogginButton(loginEnable: Boolean, login: () -> Unit) {
    Button(
        onClick = {
            login()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = colorResource(id = R.color.primary),
            disabledContainerColor = colorResource(id = R.color.secundary),
            ), enabled = loginEnable
    ) {
        Text(text = stringResource(R.string.iniciar_sesion))
    }

}

/**
 * Composable para la opción de "¿Olvidaste la contraseña?".
 * Actualmente sin funcionalidad, solo texto clicable.
 */
@Composable
fun ForgotPassword(align: Modifier) {
    Text(
        text = "¿Olvidaste la contraseña?",
        modifier = align.clickable { },
        fontWeight = FontWeight.Bold,
        color = Color(0xFFFB9600)
    )
}

/**
 * Imagen de encabezado para la pantalla de inicio de sesión.
 */
@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.localocker_sinfondo),
        contentDescription = "Header",
        modifier = modifier
    )
}
