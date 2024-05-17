import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.locallockers.ui.theme.Composable.*
import com.example.locallockers.ui.theme.views.Register.ui.RegisterViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.SearchViewModel
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun RegisterScreen(
    searchViewModel: SearchViewModel,
    registerModel: RegisterViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val email: String by registerModel.email.observeAsState(initial = "")
    val name: String by registerModel.name.observeAsState(initial = "")
    val password: String by registerModel.password.observeAsState(initial = "")
    val confirmPassword: String by registerModel.confirmPassword.observeAsState(initial = "")
    val location by searchViewModel.location.observeAsState(initial = "")
    val openHours by registerModel.openHours.observeAsState(initial = "")
    val localName by registerModel.localName.observeAsState("")

    val lat by searchViewModel.lat.observeAsState()
    val long by searchViewModel.long.observeAsState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val roles = listOf("Turista", "Huésped")
        var selectedRole by remember { mutableStateOf("Turista") }
        var expanded by remember { mutableStateOf(false) }

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
            NameField(
                name = name,
                "Nombre",
                onTextFieldChanged = { registerModel.onNameChanged(it) })
            Spacer(modifier = Modifier.padding(8.dp))
            PasswordField(
                password = password,
                onPasswordChanged = { registerModel.onPasswordChanged(it) })
            Spacer(modifier = Modifier.padding(8.dp))
            ConfirmPasswordField(
                confirmPassword = confirmPassword,
                onConfirmPasswordChanged = { registerModel.onConfirmPasswordChanged(it) }
            )
            Spacer(modifier = Modifier.padding(8.dp))

        }

        // Mostrar los campos adicionales si el usuario es "Huésped"
        if (registerModel.userType == "Huésped") {
            NameField(
                name = localName,
                text = "Nombre del local",
                onTextFieldChanged = registerModel::onLocalNameChanged
            )
            Spacer(modifier = Modifier.padding(8.dp))
            LocationField(
                location = location,
                onLocationChanged = searchViewModel::onLocationChanged
            )
            Spacer(modifier = Modifier.padding(8.dp))
            OpenHoursField(
                openHours = openHours,
                onOpenHoursChanged = registerModel::onOpenHoursChanged
            )
        }
        Spacer(modifier = Modifier.padding(8.dp))
        if (registerModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text("Registrando...", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        Button(onClick = {
            // Resetea cualquier alerta previa
            registerModel.closeAlert()

            // Validación de campos vacíos
            if (email.isBlank() || password.isBlank() || name.isBlank() || (selectedRole == "Huésped" && location.isBlank())) {
                registerModel.showAlert = true
                registerModel.showError("Todos los campos son obligatorios")
                Log.d("RegisterScreen", "Validación Fallida: Campos vacíos")
            }else if(!isValidEmail(email)){
                registerModel.showAlert = true
                registerModel.showError("Correo electrónico no válido")
                Log.d("RegisterScreen", "Validación Fallida: Correo electrónico no válido")
            }
            else if (password != confirmPassword) {
                registerModel.showAlert = true
                registerModel.showError("Las contraseñas no coinciden")
                Log.d("RegisterScreen", "Validación Fallida: Las contraseñas no coinciden")
            } else {
                // Procesa el registro dependiendo del rol seleccionado
                if (selectedRole == "Turista") {
                    registerModel.showLoading(true)
                    registerModel.createUser(email, password, name, 0.0, 0.0) {
                        registerModel.showLoading(false)
                        navController.navigate("Main")
                        Log.d("RegisterScreen", "Usuario registrado como Turista")
                    }
                } else if (selectedRole == "Huésped") {
                    searchViewModel.getLocation(location) // Sólo si location no es vacío
                    if (lat == null || long == null) {
                        registerModel.showAlert = true
                        registerModel.showError("Es necesario completar la información de ubicación para 'Huésped'")
                        Log.d("RegisterScreen", "Validación Fallida: Coordenadas de ubicación faltantes")
                    } else {
                        registerModel.showLoading(true)
                        registerModel.createUser(email, password, name, lat!!, long!!) {
                            registerModel.showLoading(false)
                            navController.navigate("Request")
                            Log.d("RegisterScreen", "Usuario registrado como Huésped con lat: $lat, long: $long")
                        }
                    }
                }
            }
        }, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Text(text = "Registrarse")
        }
    }
    if (registerModel.showAlert) {
        Alert(
            title = "Alerta",
            msg = registerModel.alertMessage,
            confirmText = "Aceptar",
            onConfirmClick = { registerModel.closeAlert() }) {
        }
    }
}
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}