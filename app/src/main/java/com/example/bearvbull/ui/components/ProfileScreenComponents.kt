package com.example.bearvbull.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.R
import com.example.bearvbull.data.BetInformation
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.BetGreen
import com.example.bearvbull.ui.theme.BetRed
import com.example.bearvbull.ui.theme.NotSoDeepPurple
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.util.Utility
import com.example.bearvbull.util.formatBigLong
import com.example.bearvbull.viewmodel.MainViewModel


@Composable
fun ProfileBetHistoryContainer(viewModel: MainViewModel) {
    Column() {
        Row(Modifier.padding(8.dp)) {
            Text(
                "Bet History",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(viewModel.fakeBetHistory) { bet ->
                ProfileBetHistoryRow(betInformation = bet)
            }
        }
    }
}

@Composable
fun ProfileBetHistoryRow(betInformation: BetInformation) {
    val imageResource =
        if (betInformation.betSide == "Bear") Utility.DOWN_ARROW else Utility.UP_ARROW
    val betColor = if (betInformation.betSide == "Bear") BetRed else BetGreen
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(NotSoDeepPurple)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "$${betInformation.tickerSymbol}",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${betInformation.initialBetAmount.formatBigLong()} @${betInformation.odds}",
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    color = betColor
                )
                Spacer(Modifier.width(4.dp))
                Image(
                    painter = painterResource(imageResource),
                    contentDescription = betInformation.betSide,
                    modifier = Modifier.size(10.dp),
                    colorFilter = ColorFilter.tint(betColor)
                )
            }
            Spacer(Modifier.weight(1f))
            if (betInformation.didWin) {
                CashAmountAndIcon(
                    color = BetGreen,
                    textSize = 14,
                    imageSize = 14,
                    cashAmount = betInformation.winnings
                )
            } else {
                Text(
                    text = "Bet lost",
                    fontFamily = poppinsFontFamily,
                    color = BetRed,
                    fontSize = 16.sp
                )
            }
        }
    }
}

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
                .size(92.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = userAccountInformation.userName,
            fontFamily = poppinsFontFamily,
            fontSize = 18.sp,
            color = Color.White
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.cash_icon),
                contentDescription = "Cash",
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = userAccountInformation.userBalance.formatBigLong(),
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
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Invisible icon to make for proper row spacing
        Image(
            painter = painterResource(id = R.drawable.settings_gear_icon),
            contentDescription = "",
            modifier = Modifier
                .size(24.dp)
                .focusable(false)
                .alpha(0f)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Profile",
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1f))
        TopBarIcon(icon = R.drawable.settings_gear_icon, contentDesc = "Settings")
    }
}