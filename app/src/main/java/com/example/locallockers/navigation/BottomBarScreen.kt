package com.example.locallockers.navigation

import android.icu.text.CaseMap.Title
import androidx.annotation.StringRes
import com.example.locallockers.R

// Clase sellada que define las diferentes pantallas del BottomBar.
sealed class BottomBarScreen (
    val route: String,  // Ruta de navegación de la pantalla.
    @StringRes val title: Int,  // ID del recurso de cadena del título de la pantalla.
    val icon: Int,  // Icono de la pantalla.
    val icon_focused: Int  // Icono de la pantalla cuando está enfocada.
) {
    //for mainScreen(mapa)
    object Map: BottomBarScreen(
        route = "Main",
        title = R.string.title_map,
        icon = R.drawable.map,
        icon_focused = R.drawable.map_focus
    )

    //for listScreen
    object Listado: BottomBarScreen(
        route = "Listado",
        title = R.string.title_listado,
        icon = R.drawable.list,
        icon_focused = R.drawable.list_focus
    )

    //for bookScreen
    object Book: BottomBarScreen(
        route = "Book",
        title = R.string.title_book,
        icon = R.drawable.book,
        icon_focused = R.drawable.book_focus
    )

    //for confiScreen
    object Confi: BottomBarScreen(
        route = "Confi",
        title = R.string.title_confi,
        icon = R.drawable.confi,
        icon_focused = R.drawable.confi_focus
    )

    //for RequesScreen
    object Request: BottomBarScreen(
        "Request",
        R.string.title_request,
        icon = R.drawable.request,
        icon_focused = R.drawable.request_focus
    )
    //for CalendarScreen
    object Calendar: BottomBarScreen(
        "Calendar",
        R.string.title_calendar,
        icon = R.drawable.calendar,
        icon_focused = R.drawable.calendar_focus
    )
    //for DeleteScreen
    object Delete: BottomBarScreen(
        "Delete",
        R.string.title_delete,
        icon = R.drawable.about_confi,
        icon_focused = R.drawable.about_confi
    )

}