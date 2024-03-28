import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.locallockers.ui.theme.Composable.Alert
import com.example.locallockers.ui.theme.Composable.ConfirmPasswordField
import com.example.locallockers.ui.theme.Composable.EmailField
import com.example.locallockers.ui.theme.Composable.MainIconButton
import com.example.locallockers.ui.theme.Composable.NameField
import com.example.locallockers.ui.theme.Composable.PasswordField
import com.example.locallockers.ui.theme.Composable.TitleBar
import com.example.locallockers.ui.theme.views.Register.ui.RegisterViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    registerModel: RegisterViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Register(modifier = modifier, registerModel = registerModel, navController = navController)
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
        Spacer(modifier = Modifier.padding(40.dp))
        EmailField(email = email, onTextFieldChanged = { registerModel.onEmailChanged(it) })
        Spacer(modifier = Modifier.padding(8.dp))
        NameField(name = name, onTextFieldChanged = { registerModel.onNameChanged(it) })
        Spacer(modifier = Modifier.padding(8.dp))
        PasswordField(
            password = password,
            onPasswordChanged = { registerModel.onPasswordChanged(it) })
        Spacer(modifier = Modifier.padding(8.dp))
        ConfirmPasswordField(
            confirmPassword = confirmPassword,
            onConfirmPasswordChanged = { registerModel.onConfirmPasswordChanged(it) })
        Button(
            onClick = {
                      registerModel.createUser(email,password,name){
                          navController.navigate("Main")
                      }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Registrarse")
        }
        if(registerModel.showAlert){
            Alert(title = "Alerta", msg = "Usuario no creado", confirmText = "Aceptar", onConfirmClick = { registerModel.closeAlert() }) {
                
            }
        }
    }
}
