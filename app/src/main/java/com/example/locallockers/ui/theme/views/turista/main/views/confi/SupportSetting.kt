package com.example.locallockers.ui.theme.views.turista.main.views.confi

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.locallockers.R
import com.example.locallockers.model.UiConstants

@Composable
fun SupportConfiUI() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var dialogContent by remember { mutableStateOf<Pair<String, String>?>(null) }

    if (showDialog && dialogContent != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
            },
            title = {
                Text(text = dialogContent!!.first)
            },
            text = {
                Text(text = dialogContent!!.second)
            },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    if (dialogContent!!.first == "Feedback") {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=local+lockers"))
                        context.startActivity(intent)
                    }
                }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier.padding(horizontal = 14.dp)
    ) {
        Text(
            text = "Support",
            fontFamily = FontFamily(Font(R.font.poppins)),
            fontSize = UiConstants.fontSizeM,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        SupportItem(
            icon = R.drawable.contact_confi,
            mainText = "Contact",
            onClick = {
                dialogContent = "Contact Information" to "You can contact us via email at support@locallockers.com."
                showDialog = true
            }
        )
        SupportItem(
            icon = R.drawable.feedback_confi,
            mainText = "Feedback",
            onClick = {
                dialogContent = "Feedback" to "Click Okey and leave us a review on Google!"
                showDialog = true
            }
        )
        SupportItem(
            icon = R.drawable.privacy_confi,
            mainText = "Privacy Policy",
            onClick = {
                dialogContent = "Privacy Policy" to "Our privacy policy ensures that your data is securely managed. For more details, visit our website."
                showDialog = true
            }
        )
        SupportItem(
            icon = R.drawable.about_confi,
            mainText = "About",
            onClick = {
                dialogContent = "About LocalLockers" to "LocalLockers is committed to supporting the local community in Málaga by providing secure storage solutions."
                showDialog = true
            }
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportItem(icon: Int, mainText: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
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
            Box(modifier = Modifier.size(UiConstants.boxSize)) {
                Icon(painter = painterResource(id = icon), contentDescription = "", tint = Color.Unspecified, modifier = Modifier.size(UiConstants.IconSize))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(mainText, fontFamily = FontFamily(Font(R.font.poppins)), fontSize = UiConstants.fontSizeM, fontWeight = FontWeight.Bold)
            Icon(painter = painterResource(id = R.drawable.flecha_derecha), contentDescription = "", modifier = Modifier.size(UiConstants.ArrowIconSize))
        }
    }
}