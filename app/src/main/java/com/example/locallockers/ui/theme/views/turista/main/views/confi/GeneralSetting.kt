package com.example.locallockers.ui.theme.views.turista.main.views.confi

import UserViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.locallockers.R
import com.example.locallockers.model.UiConstants


@Composable
fun GeneralConfiUI(userViewModel: UserViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = "General",
            fontFamily = FontFamily(Font(R.font.poppins)),
            //color = SecondaryColor,
            fontSize = UiConstants.fontSizeM,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        GeneralSettingItem(
            icon = R.drawable.notification_confi,
            mainText = "Notifications",
            subText = "Customize notifications",
            onClick = {

            }

        )
        GeneralSettingItem(
            icon = R.drawable.more_customization_confi,
            mainText = "More customization",
            subText = "Customize it more to fit your usage",
            onClick = {
                userViewModel.showCustomizationDialog = true
            }
        )
    }
    if (userViewModel.showCustomizationDialog) {
        CustomizationDialog(userViewModel)
    }
    SnackbarHost(hostState = snackbarHostState)
    LaunchedEffect(userViewModel.showSnackbar) {
        if (userViewModel.showSnackbar) {
            snackbarHostState.showSnackbar(
                message = userViewModel.snackbarMessage,
                duration = SnackbarDuration.Short
            )
            userViewModel.showSnackbar = false  // Reset the state
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingItem(icon: Int, mainText: String, subText: String, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        //backgroundColor = Color.White,
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(UiConstants.boxSize)
                    //.clip(shape = Shapes.medium)
                    //.background(LightPrimaryColor)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(UiConstants.IconSize)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))
                Column(
                    modifier = Modifier.offset(y = (2).dp)
                ) {
                    Text(
                        text = mainText,
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        // color = SecondaryColor,
                        fontSize = UiConstants.fontSizeM,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = subText,
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        color = Color.Gray,
                        fontSize = UiConstants.fontSizeS,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.offset(y = (-4).dp)
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.flecha_derecha),
                contentDescription = "",
                modifier = Modifier.size(UiConstants.ArrowIconSize)
            )

        }
    }
}

@Composable
fun CustomizationDialog(userViewModel: UserViewModel) {
    var newName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { userViewModel.showCustomizationDialog = false },
        title = { Text("Change Your Name") },
        text = {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("New Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    userViewModel.updateUserName(newName)
                    userViewModel.showCustomizationDialog = false
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(
                onClick = { userViewModel.showCustomizationDialog = false }
            ) {
                Text("Cancel")
            }
        }
    )
}
