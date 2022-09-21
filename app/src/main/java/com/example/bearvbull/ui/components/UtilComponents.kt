package com.example.bearvbull.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.data.LiveBetData
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.ui.theme.interFontFamily
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.util.BetInfoType
import com.example.bearvbull.util.BetSide
import com.example.bearvbull.util.PERCENT_SIGN
import com.example.bearvbull.viewmodel.MainViewModel

@Composable
fun BetInfoImage(infoType: BetInfoType) {
    Image(
        painter = painterResource(id = infoType.icon),
        contentDescription = infoType.name,
        modifier = Modifier
            .padding(4.dp)
            .size(15.dp),
        colorFilter = ColorFilter.tint(color = Color.White)
    )
}

@Composable
fun BetInfoLabel(infoType: BetInfoType, betSide: BetSide, liveBetData: LiveBetData) {
    Text(
        text = liveBetData.createBetInfoLabel(infoType, betSide),
        fontFamily = interFontFamily,
        fontSize = 12.sp
    )
}

@Composable
fun TopBarIcon(icon: Int) {
    // 0 = Left icon 1 = Right icon
    Image(
        painter = painterResource(id = icon),
        contentDescription = "Profile",
        modifier = Modifier
            .size(24.dp)
            .clickable { }
    )
}

@Composable
fun OrderBookLabel(text: String, modifier: Modifier) {
    Text(
        text = text,
        fontFamily = poppinsFontFamily,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
fun OrderBookLabelRow() {
    Row() {
        OrderBookLabel(
            text = "User", modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
        OrderBookLabel(
            text = PERCENT_SIGN, modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        ) // Betside and percent are combined here
        OrderBookLabel(
            text = "Amount", modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
        OrderBookLabel(
            text = "Time", modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
    }
}

@Composable
fun OrderBookEntryText(orderBookEntryText: String, modifier: Modifier) {
    Text(
        text = orderBookEntryText,
        modifier = modifier,
        fontFamily = poppinsFontFamily,
        fontWeight = FontWeight.Normal
    )
}

@Composable
fun OrderBookEntryRow(orderBookEntry: OrderBookEntry) {
    Row() {
        OrderBookEntryText(
            orderBookEntry.userName, modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
    }
}