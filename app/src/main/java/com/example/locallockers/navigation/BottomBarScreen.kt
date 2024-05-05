package com.example.locallockers.navigation

import com.example.locallockers.R

sealed class BottomBarScreen (
    val route: String,
    val title: String,
    val icon: Int,
    val icon_focused: Int
) {
    //for mainScreen(mapa)
    object Map: BottomBarScreen(
        route = "Main",
        title = "Mapa",
        icon = R.drawable.map,
        icon_focused = R.drawable.map_focus
    )

    //for listScreen
    object Listado: BottomBarScreen(
        route = "Listado",
        title = "Listado",
        icon = R.drawable.list,
        icon_focused = R.drawable.list_focus
    )

    //for bookScreen
    object Book: BottomBarScreen(
        route = "Book",
        title = "Book",
        icon = R.drawable.book,
        icon_focused = R.drawable.book_focus
    )

    //for confiScreen
    object Confi: BottomBarScreen(
        route = "Confi",
        title = "Confi",
        icon = R.drawable.confi,
        icon_focused = R.drawable.confi_focus
    )

    //for RequesScreen
    object Request: BottomBarScreen(
        "Request",
        "Request",
        icon = R.drawable.request,
        icon_focused = R.drawable.request_focus
    )

}