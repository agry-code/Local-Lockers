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

@Composable
fun Alert(
    title:String,
    msg:String,
    confirmText:String,
    onConfirmClick:() -> Unit,
    onDismissClick:() -> Unit,
){
    val scroll = rememberScrollState(0)

    AlertDialog(onDismissRequest = {onDismissClick},
        title = { Text(text = title)},
        text = { Text(text = msg,
            textAlign = TextAlign.Justify,
        modifier = Modifier.verticalScroll(scroll)
        )}
        , confirmButton = {
            Button(
                onClick = { onConfirmClick() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.primary),
                    contentColor = colorResource(id = R.color.white)
                )
            )  {
                Text(text = confirmText)
            }
        })
}