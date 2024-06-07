import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.locallockers.R
import com.example.locallockers.ui.theme.Composable.*
import com.example.locallockers.ui.theme.views.Register.ui.RegisterViewModel
import com.example.locallockers.ui.theme.views.turista.main.views.maps.SearchViewModel

/**
 * Campos en la pantalla BlankView para registrar al usuario.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedMutableState")
@Composable
fun RegisterScreen(
    searchViewModel: SearchViewModel,
    registerModel: RegisterViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    /* Observamos los estados de las variables del ViewModel */
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
        /* Lista de roles disponibles para el registro */
        val roles = listOf("Turista", "Huesped")
        var selectedRole by remember { mutableStateOf("Turista") }
        var expanded by remember { mutableStateOf(false) }

        // Un botón que cuando se presiona, cambia el estado de 'expanded' para mostrar u ocultar el menú
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = colorResource(id = R.color.primary),
                    disabledContainerColor = colorResource(id = R.color.secundary),
                )
                ) {
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
        /* Campos de entrada para el registro */
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

        /* Mostrar campos adicionales si el usuario es "Huesped" */

        if (registerModel.userType == "Huesped") {
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

        /* Mostrar un indicador de carga si el registro está en proceso */
        if (registerModel.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(stringResource(R.string.registrando), modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        /* Botón para enviar el formulario de registro */
        Button(onClick = {

            /* Resetea cualquier alerta previa */
            registerModel.closeAlert()
            /* Validación de campos vacíos */
            if (email.isBlank() || password.isBlank() || name.isBlank() || (selectedRole == "Huesped" && (location.isBlank() || localName.isBlank() || openHours.isBlank()))) {
                registerModel.showAlert = true
                registerModel.showError("Todos los campos son obligatorios")
                Log.d("RegisterScreen", "Validación Fallida: Campos vacíos")
            }else if(!isValidEmail(email)){
                registerModel.showAlert = true
                registerModel.showError("Correo electrónico no válido")
                Log.d("RegisterScreen", "Validación Fallida: Correo electrónico no válido")
            }
            else if (password.length < 6) {
                registerModel.showAlert = true
                registerModel.showError("La contraseña debe tener al menos 6 caracteres")
                Log.d("RegisterScreen", "Validación Fallida: Contraseña no cumple longitud mínima")
            }
            else if (password != confirmPassword) {
                registerModel.showAlert = true
                registerModel.showError("Las contraseñas no coinciden")
                Log.d("RegisterScreen", "Validación Fallida: Las contraseñas no coinciden")
            } else {
                /* Procesa el registro dependiendo del rol seleccionado */

                if (selectedRole == "Turista") {
                    registerModel.showLoading(true)
                    registerModel.createUser(email, password, name, 0.0, 0.0) {
                        registerModel.showLoading(false)
                        navController.navigate("Main")
                        Log.d("RegisterScreen", "Usuario registrado como Turista")
                    }
                } else if (selectedRole == "Huesped") {
                    searchViewModel.getLocation(location) // Sólo si location no es vacío
                    if (lat == null || long == null) {
                        registerModel.showAlert = true
                        registerModel.showError("Es necesario completar la información de ubicación para 'Huesped'")
                        Log.d("RegisterScreen", "Validación Fallida: Coordenadas de ubicación faltantes")
                    } else {
                        registerModel.showLoading(true)
                        registerModel.createUser(email, password, name, lat!!, long!!) {
                            registerModel.showLoading(false)
                            navController.navigate("Request")
                            Log.d("RegisterScreen", "Usuario registrado como Huesped con lat: $lat, long: $long")
                        }
                    }
                }
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.primary),
                contentColor = colorResource(id = R.color.white)
            )
        ) {
            Text(text = stringResource(R.string.registrarse))
        }
    }
    /* Mostrar alerta si hay un error en el registro */
    if (registerModel.showAlert) {
        Alert(
            title = stringResource(id = R.string.alerta),
            msg = registerModel.alertMessage,
            confirmText = stringResource(id = R.string.aceptar),
            onConfirmClick = { registerModel.closeAlert() }) {
        }
    }
}
/* Función para validar si un correo electrónico es válido */
fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}