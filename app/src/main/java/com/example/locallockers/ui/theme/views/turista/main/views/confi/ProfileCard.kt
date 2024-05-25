package com.example.locallockers.ui.theme.views.turista.main.views.confi

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.locallockers.R
import com.example.locallockers.model.UiConstants
import com.example.locallockers.model.UserModel


@Composable
fun ProfileCardUI(user: UserModel?) {
    // Estado para controlar la visibilidad del diálogo
    var showDialog by remember { mutableStateOf(false) }

    // Diálogo que muestra la información del usuario
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Esto se llama cuando el usuario toca fuera del diálogo para cerrarlo
                showDialog = false
            },
            title = {
                Text(text = "Información del Usuario")
            },
            text = {
                Column {
                    Text("Nombre: ${user?.userName ?: "No disponible"}")
                    Text("Email: ${user?.email ?: "No disponible"}")
                }
            },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    )
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.tu_perfil),
                    color = colorResource(id = R.color.primary),
                    fontSize = UiConstants.fontSizeL,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins))
                )
                Text(
                    text = user?.email ?: stringResource(R.string.email_no_disponible),
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    color = colorResource(id = R.color.secundary),
                    fontSize = UiConstants.fontSizeS,
                    fontWeight = FontWeight.SemiBold,
                )

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        showDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.primary),
                        contentColor = colorResource(id = R.color.white)
                    ),
                    contentPadding = PaddingValues(horizontal = 30.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "View",
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        fontSize = UiConstants.fontSizeM,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Image(
                painter = painterResource(id = R.drawable.ic_profile_card_image),
                contentDescription = "",
                modifier = Modifier
                    .height(240.dp)
                    .padding(start = 80.dp, top = 8.dp, end = 16.dp)
            )
        }
    }
}