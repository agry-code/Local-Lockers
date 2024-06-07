package com.example.locallockers.ui.theme.Composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.locallockers.R

/**
 * Composable que muestra un campo de texto para el email.
 * @param email El email actual.
 * @param onTextFieldChanged Acción a realizar al cambiar el texto del email.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailField(email: String, onTextFieldChanged: (String) -> Unit) {
    TextField(
        value = email,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = stringResource(R.string.email)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = Color(0xFF636262)),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = colorResource(id = R.color.transparent),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

/**
 * Composable que muestra un campo de texto para el nombre.
 * @param name El nombre actual.
 * @param text Texto del placeholder.
 * @param onTextFieldChanged Acción a realizar al cambiar el texto del nombre.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameField(name: String, text: String, onTextFieldChanged: (String) -> Unit) {
    TextField(value = name,
        onValueChange = { onTextFieldChanged(it) },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = text) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = Color(0xFF636262)),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = colorResource(id = R.color.transparent),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
        )
}

/**
 * Composable que muestra un campo de texto para la contraseña.
 * @param password La contraseña actual.
 * @param onPasswordChanged Acción a realizar al cambiar el texto de la contraseña.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordField(password: String, onPasswordChanged: (String) -> Unit) {
    TextField(
        value = password,
        onValueChange = onPasswordChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(text = stringResource(R.string.contrase_a)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = colorResource(id = R.color.transparent),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}


/**
 * Composable que muestra un campo de texto para confirmar la contraseña.
 * @param confirmPassword La contraseña de confirmación actual.
 * @param onConfirmPasswordChanged Acción a realizar al cambiar el texto de la contraseña de confirmación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmPasswordField(confirmPassword: String, onConfirmPasswordChanged: (String) -> Unit) {
    TextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChanged,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = stringResource(R.string.confirmar_contrase_a)) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = colorResource(id = R.color.transparent),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}

/**
 * Composable que muestra un campo de texto para la ubicación.
 * @param location La ubicación actual.
 * @param onLocationChanged Acción a realizar al cambiar el texto de la ubicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationField(location: String, onLocationChanged: (String) -> Unit) {
    TextField(
        value = location,
        onValueChange = onLocationChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.ubicaci_n)) },
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            containerColor = colorResource(id = R.color.transparent),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}
/**
 * Composable que muestra un campo de texto para el horario de apertura.
 * @param openHours El horario de apertura actual.
 * @param onOpenHoursChanged Acción a realizar al cambiar el texto del horario de apertura.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpenHoursField(openHours: String, onOpenHoursChanged: (String) -> Unit) {
    TextField(
        value = openHours,
        onValueChange = onOpenHoursChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(stringResource(R.string.horario_de_apertura)) },
        singleLine = true,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = colorResource(id = R.color.transparent),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}
