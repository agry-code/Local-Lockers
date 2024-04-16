package com.example.locallockers.ui.theme.views.turista.main.views.confi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.locallockers.navigation.BottomNav
import com.example.locallockers.ui.theme.views.turista.main.views.maps.MapViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.locallockers.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiScreen(navController: NavController, mapViewModel: MapViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Configuración") },
                navigationIcon = {
                    IconButton(onClick = {
                        mapViewModel.signOut()
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "")
                    }
                })
        },
        bottomBar = {
            BottomNav(navController)
        }
    ) { pad ->
        Column(
            modifier = Modifier.padding(pad),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileCardUI()
            GeneralConfiUI()
            SupportConfiUI()
        }
    }
}

@Composable
fun SupportConfiUI() {
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = "Support",
            fontFamily = FontFamily(Font(R.font.poppins)),
            //color = SecondaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        SupportItem(
            icon = R.drawable.contact_confi,
            mainText = "Contact",
            onClick = {}
        )
        SupportItem(
            icon = R.drawable.feedback_confi,
            mainText = "Feedback",
            onClick = {}
        )
        SupportItem(
            icon = R.drawable.privacy_confi,
            mainText = "Privacy Policy",
            onClick = {}
        )
        SupportItem(
            icon = R.drawable.about_confi,
            mainText = "About",
            onClick = {}
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportItem(icon: Int, mainText: String, onClick: () -> Unit) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
//                        .clip(shape = Shapes.medium)
//                        .background(LightPrimaryColor)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = mainText,
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    //color = SecondaryColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.icon_google),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )

        }
    }
}

@Composable
fun GeneralConfiUI() {
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = "General",
            fontFamily = FontFamily(Font(R.font.poppins)),
            //color = SecondaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        GeneralSettingItem(
            icon = R.drawable.notification_confi,
            mainText = "Notifications",
            subText = "Customize notifications",
            onClick = {}
        )
        GeneralSettingItem(
            icon = R.drawable.more_customization_confi,
            mainText = "More customization",
            subText = "Customize it more to fit your usage",
            onClick = {}
        )
//        GeneralSettingItem()
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
                        .size(34.dp)
                        //.clip(shape = Shapes.medium)
                        //.background(LightPrimaryColor)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(8.dp)
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = subText,
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        color = Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.offset(y = (-4).dp)
                    )
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.icon_google),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )

        }
    }
}

@Composable
fun ProfileCardUI() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column() {
                Text(
                    text = "Check Your Profile",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily(Font(R.font.poppins))
                )

                Text(
                    text = "UI.Stack.YT@gmail.com",
                    fontFamily = FontFamily(Font(R.font.poppins)),
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                )

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 30.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )

                    ) {
                    Text(
                        text = "View",
                        fontFamily = FontFamily(Font(R.font.poppins)),
                        //color = SecondaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
           // Spacer(modifier = Modifier.width(82.dp)) // Ajusta el ancho según sea necesario
            Image(
                painter = painterResource(id = R.drawable.ic_profile_card_image),
                contentDescription = "",
                modifier = Modifier.height(240.dp).padding(start = 80.dp, top = 8.dp, end = 16.dp)
            )
        }
    }
}
