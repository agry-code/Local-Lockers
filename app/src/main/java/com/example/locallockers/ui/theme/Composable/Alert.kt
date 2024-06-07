package com.example.locallockers.ui.theme.Composable

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import com.example.locallockers.R

/**
 * Composable que muestra una alerta con un título, un mensaje y botones de confirmación y cancelación.
 * @param title Título de la alerta.
 * @param msg Mensaje de la alerta.
 * @param confirmText Texto del botón de confirmación.
 * @param onConfirmClick Acción a realizar al hacer clic en el botón de confirmación.
 * @param onDismissClick Acción a realizar al hacer clic fuera de la alerta o en el botón de cancelar.
 */
@Composable
fun Alert(
    title: String,
    msg: String,
    confirmText: String,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
) {
    val scroll = rememberScrollState(0) // Estado de desplazamiento para el texto de la alerta

    AlertDialog(
        onDismissRequest = { onDismissClick() }, // Acción a realizar al hacer clic fuera de la alerta
        title = { Text(text = title) }, // Título de la alerta
        text = {
            Text(
                text = msg,
                textAlign = TextAlign.Justify, // Alineación del texto del mensaje
                modifier = Modifier.verticalScroll(scroll) // Habilita el desplazamiento vertical si el mensaje es largo
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirmClick() }, // Acción a realizar al hacer clic en el botón de confirmación
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary), // Color de fondo del botón
                    contentColor = colorResource(id = R.color.white) // Color del texto del botón
                )
            ) {
                Text(text = confirmText) // Texto del botón de confirmación
            }
        }
    )
}