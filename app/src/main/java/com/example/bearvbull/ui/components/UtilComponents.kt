package com.example.bearvbull.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.data.LiveBetData
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.ui.theme.BetGreen
import com.example.bearvbull.ui.theme.BetRed
import com.example.bearvbull.ui.theme.interFontFamily
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.util.BetInfoType
import com.example.bearvbull.util.BetSide
import com.example.bearvbull.util.PERCENT_SIGN
import com.example.bearvbull.util.Utility.DOWN_ARROW
import com.example.bearvbull.util.Utility.UP_ARROW
import com.example.bearvbull.util.Utility.simpleDateFormat
import com.example.bearvbull.util.round
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
    Image(
        painter = painterResource(id = icon),
        contentDescription = "Profile",
        modifier = Modifier
            .size(24.dp)
            .clickable { },
        colorFilter = ColorFilter.tint(color = Color.White)
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
            text = "Side", modifier = Modifier
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
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
}

@Composable
fun OrderBookEntryPercentText(betside: String, percent: String, modifier: Modifier) {
    val imageResource = if (betside == "Bear") DOWN_ARROW else UP_ARROW
    val betColor = if (betside == "Bear") BetRed else BetGreen
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(imageResource),
            contentDescription = betside,
            modifier = Modifier.size(12.dp),
            colorFilter = ColorFilter.tint(color = betColor)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = percent,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal,
            color = betColor,
            fontSize = 12.sp
        )
    }
}

@Composable
fun OrderBookEntryRow(orderBookEntry: OrderBookEntry, modifier: Modifier) {
    Row(modifier = modifier) {
        OrderBookEntryText(
            orderBookEntry.userName, modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
        OrderBookEntryPercentText(
            betside = orderBookEntry.betSide,
            percent = orderBookEntry.betPercent.toString(),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
        OrderBookEntryText(
            orderBookEntry.amountWagered.toString(), modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
        OrderBookEntryText(
            simpleDateFormat.format(orderBookEntry.time), modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
    }
}


@Composable
fun BottomNavBarItem(icon: Int, title: String) {
    Column() {
        Image(
            painter = painterResource(id = icon),
            contentDescription = title,
            colorFilter = ColorFilter.tint(
                Color.White
            )
        )
        Text(title, fontSize = 12.sp)
    }
}