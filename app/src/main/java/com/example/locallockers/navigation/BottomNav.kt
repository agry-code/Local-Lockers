package com.example.locallockers.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.locallockers.R

/**
 * Composable que define el Bottom Navigation Bar según el rol del usuario.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param userRole Rol del usuario que determina las pantallas visibles en el BottomBar.
 */
@Composable
fun BottomNav(navController: NavController, userRole: String) {
    /** Determina las pantallas a mostrar según el rol del usuario. */
    val screens = when (userRole) {
        "Turista" -> listOf(BottomBarScreen.Map, BottomBarScreen.Listado, BottomBarScreen.Book, BottomBarScreen.Confi)
        "Huesped" -> listOf(BottomBarScreen.Book, BottomBarScreen.Request, BottomBarScreen.Calendar, BottomBarScreen.Confi)
        "Admin" -> listOf(BottomBarScreen.Delete, BottomBarScreen.Confi)
        else -> emptyList()  // Caso por defecto si el rol no es reconocido.
    }
    /** Llama al Composable BottomBar pasándole las pantallas a mostrar. */
    BottomBar(navController = navController, screens = screens)
}

/**
 * Composable que define la barra de navegación inferior.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 * @param screens Lista de pantallas a mostrar en el BottomBar.
 */
@Composable
fun BottomBar(navController: NavController, screens: List<BottomBarScreen>) {
    /** Obtiene el estado actual de la entrada en la pila de navegación. */
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    /**
     * Crea una fila (Row) para contener los elementos del BottomBar.
     * Se define el diseño de la fila con paddings y fondo blanco.
     */
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
            .background(Color.White)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        /** Itera sobre las pantallas y añade cada una como un ítem en la fila. */
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

/**
 * Composable que añade un ítem a la fila del BottomBar.
 * @param screen Pantalla que se va a añadir al BottomBar.
 * @param currentDestination Destino actual en la navegación.
 * @param navController Controlador de navegación para gestionar la navegación entre pantallas.
 */
@Composable
fun RowScope.AddItem(
    screen: BottomBarScreen,
    currentDestination: NavDestination?,
    navController: NavController
) {
    /** Determina si la pantalla actual está seleccionada. */
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    /** Define el color de fondo según si la pantalla está seleccionada o no. */
    val background = if (selected) colorResource(id = R.color.primary) else Color.Transparent
    /** Define el color del contenido (icono y texto) según si la pantalla está seleccionada o no. */
    val contentColor = if (selected) Color.White else Color.Black

    /**
     * Crea una caja (Box) que actúa como contenedor del ítem.
     * Se define el diseño de la caja con altura, recorte circular, color de fondo y acción de clic.
     */
    Box(
        modifier = Modifier
            .height(50.dp)
            .clip(CircleShape)
            .background(background)
            .clickable(onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            })
    ) {
        /**
         * Crea una fila (Row) para contener el icono y el texto.
         * Se define el diseño de la fila con paddings, alineación vertical y distribución horizontal.
         */
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            /** Añade el icono a la fila. */
            Icon(
                painter = painterResource(id = if (selected) screen.icon_focused else screen.icon),
                contentDescription = "icon",
                tint = contentColor,
                modifier = Modifier.size(30.dp)
            )
            /**
             * Añade el texto a la fila, solo si la pantalla está seleccionada.
             * Utiliza AnimatedVisibility para animar la visibilidad del texto.
             */
            AnimatedVisibility(visible = selected) {
                Text(text = stringResource(id = screen.title), color = contentColor)
            }
        }
    }
}