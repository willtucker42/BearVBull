package com.example.bearvbull.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.R
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.poppinsFontFamily


@Composable
fun ProfilePictureAndInfo(userAccountInformation: UserAccountInformation) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.green_wojak),
            contentDescription = "User profile picture",
            Modifier
                .clip(CircleShape)
                .size(80.dp)
        )
        Text(
            text = userAccountInformation.userName,
            fontFamily = poppinsFontFamily,
            fontSize = 14.sp,
            color = Color.White
        )
        Row() {
            Image(
                painter = painterResource(id = R.drawable.cash_icon),
                contentDescription = "Cash",
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = userAccountInformation.userBalance.toString(),
                fontFamily = poppinsFontFamily,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileTopBar() {
    Row(
        Modifier
            .wrapContentWidth()
            .padding(12.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        TopBarIcon(icon = R.drawable.settings_gear_icon, contentDesc = "Settings")
    }
}