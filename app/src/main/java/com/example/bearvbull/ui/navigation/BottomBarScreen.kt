package com.example.bearvbull.ui.navigation

import androidx.annotation.DrawableRes
import com.example.bearvbull.R

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int
) {
    object Home : BottomBarScreen(
        route = "home",
        title = "Home",
        icon = R.drawable.bet_chips_icon
    )
    object Rankings : BottomBarScreen(
        route = "rankings",
        title = "Rankings",
        icon = R.drawable.trophy_icon_2
    )
    object Profile : BottomBarScreen(
        route = "profile",
        title = "Profile",
        icon = R.drawable.user_icon_filled
    )
}
