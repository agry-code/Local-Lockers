import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.locallockers.ui.theme.Composable.ConfirmPasswordField
import com.example.locallockers.ui.theme.Composable.EmailField
import com.example.locallockers.ui.theme.Composable.NameField
import com.example.locallockers.ui.theme.Composable.PasswordField
import com.example.locallockers.ui.theme.Register.ui.RegisterViewModel

@Composable
fun RegisterScreen(registerModel: RegisterViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally, // Esto centrará todos los composables de la Columna horizontalmente
    ) {
        Header() // Esto se centrará horizontalmente en la Columna
        Spacer(Modifier.height(8.dp)) // Proporciona espacio entre el encabezado y el formulario de registro
        // Utiliza un Box para contener y centrar el formulario de registro en la pantalla
        Box(
            contentAlignment = Alignment.Center, // Centra el contenido (el formulario de registro) dentro del Box
            modifier = Modifier.padding(10.dp), // El Box ocupa todo el espacio disponible
        ) {
            Register(Modifier.align(Alignment.Center), registerModel, navController)
        }
    }
}

@Composable
fun Header() {
    Text(
        text = "Registro",
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
        ),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Register(modifier: Modifier, registerModel: RegisterViewModel, navController: NavController) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val email: String by registerModel.email.observeAsState(initial = "")
    val name: String by registerModel.name.observeAsState(initial = "")
    val password: String by registerModel.password.observeAsState(initial = "")
    val confirmPassword: String by registerModel.confirmPassword.observeAsState(initial = "")

    Column(modifier = modifier) {

        EmailField(email = email, onTextFieldChanged = { registerModel.onEmailChanged(it) })
        Spacer(modifier = Modifier.padding(8.dp))
        NameField(name = name,onTextFieldChanged = {registerModel.onNameChanged(it)})
        Spacer(modifier = Modifier.padding(8.dp))
        PasswordField(password = password, onPasswordChanged = {registerModel.onPasswordChanged(it)})
        Spacer(modifier = Modifier.padding(8.dp))
        ConfirmPasswordField(confirmPassword = confirmPassword, onConfirmPasswordChanged = {registerModel.onConfirmPasswordChanged(it)})
        Button(onClick = { /*TODO*/ },modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Text(text = "Registrarse")
        }
    }
}
