import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.locallockers.ui.theme.Composable.Alert
import com.example.locallockers.ui.theme.Composable.ConfirmPasswordField
import com.example.locallockers.ui.theme.Composable.EmailField
import com.example.locallockers.ui.theme.Composable.LocationField
import com.example.locallockers.ui.theme.Composable.NameField
import com.example.locallockers.ui.theme.Composable.OpenHoursField
import com.example.locallockers.ui.theme.Composable.PasswordField
import com.example.locallockers.ui.theme.views.Register.ui.RegisterViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterScreen(
    registerModel: RegisterViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val email: String by registerModel.email.observeAsState(initial = "")
    val name: String by registerModel.name.observeAsState(initial = "")
    val password: String by registerModel.password.observeAsState(initial = "")
    val confirmPassword: String by registerModel.confirmPassword.observeAsState(initial = "")
    val location by registerModel.location.observeAsState(initial = "")
    val openHours by registerModel.openHours.observeAsState(initial = "")
    val localName by registerModel.localName.observeAsState("")
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val roles = listOf("Turista", "Huesped")
        var selectedRole by remember {
            mutableStateOf("Turista")

        }
        var expanded by remember { mutableStateOf(false) }  // Inicializa el estado 'expanded'
        // Un botón que cuando se presiona, cambia el estado de 'expanded' para mostrar u ocultar el menú
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { expanded = true }) {
                Text("Seleccione su rol: $selectedRole")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                roles.forEach { role ->
                    DropdownMenuItem(text = { Text(role) }, onClick = {
                        selectedRole = role
                        registerModel.onRoleChanged(role)
                        expanded = false
                    })
                }
            }
        }
        Column(modifier = modifier) {
            Spacer(modifier = Modifier.padding(40.dp))
            EmailField(email = email, onTextFieldChanged = { registerModel.onEmailChanged(it) })
            Spacer(modifier = Modifier.padding(8.dp))
            NameField(name = name, "Nombre",onTextFieldChanged = { registerModel.onNameChanged(it) })
            Spacer(modifier = Modifier.padding(8.dp))
            PasswordField(
                password = password,
                onPasswordChanged = { registerModel.onPasswordChanged(it) })
            Spacer(modifier = Modifier.padding(8.dp))
            ConfirmPasswordField(
                confirmPassword = confirmPassword,
                onConfirmPasswordChanged = { registerModel.onConfirmPasswordChanged(it) })
            Spacer(modifier = Modifier.padding(8.dp))


            if (registerModel.showAlert) {
                Alert(
                    title = "Alerta",
                    msg = "Usuario no creado",
                    confirmText = "Aceptar",
                    onConfirmClick = { registerModel.closeAlert() }) {
                }
            }
        }
        // Mostrar los campos adicionales si el usuario es "Huesped/Local"
        if (registerModel.userType == "Huesped") {
            NameField(name = localName, text = "Nombre del local", onTextFieldChanged = registerModel::onLocalNameChanged )
            Spacer(modifier = Modifier.padding(8.dp))
            LocationField(location = location, onLocationChanged = registerModel::onLocationChanged)
            Spacer(modifier = Modifier.padding(8.dp))
            OpenHoursField(openHours = openHours, onOpenHoursChanged = registerModel::onOpenHoursChanged)
        }
        // Botón de registro
        Button(
            onClick = {
                registerModel.createUser(email, password, name) {
                    navController.navigate("Main")
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Registrarse")
        }
        // Alerta de errores
        if (registerModel.showAlert) {
            Alert(
                title = "Alerta",
                msg = "Usuario no creado",
                confirmText = "Aceptar",
                onConfirmClick = { registerModel.closeAlert() }) {
            }        }
    }
}


@Composable
fun Register(modifier: Modifier, registerModel: RegisterViewModel, navController: NavController) {
    val email: String by registerModel.email.observeAsState(initial = "")
    val name: String by registerModel.name.observeAsState(initial = "")
    val password: String by registerModel.password.observeAsState(initial = "")
    val confirmPassword: String by registerModel.confirmPassword.observeAsState(initial = "")

    Column(modifier = modifier) {
        Spacer(modifier = Modifier.padding(40.dp))
        EmailField(email = email, onTextFieldChanged = { registerModel.onEmailChanged(it) })
        Spacer(modifier = Modifier.padding(8.dp))
        NameField(name = name,"Nombre" ,onTextFieldChanged = { registerModel.onNameChanged(it) })
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
                registerModel.createUser(email, password, name) {
                    navController.navigate("Main")
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Registrarse")
        }
        if (registerModel.showAlert) {
            Alert(
                title = "Alerta",
                msg = "Usuario no creado",
                confirmText = "Aceptar",
                onConfirmClick = { registerModel.closeAlert() }) {
            }
        }
    }
}
