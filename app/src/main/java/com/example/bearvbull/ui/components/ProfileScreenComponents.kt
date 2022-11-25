package com.example.bearvbull.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.R
import com.example.bearvbull.data.BetInformation
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.util.ChangeUserNameValue
import com.example.bearvbull.util.ChangeUserNameValue.*
import com.example.bearvbull.util.Utility
import com.example.bearvbull.util.formatBigLong
import com.example.bearvbull.util.getEloRank
import com.example.bearvbull.viewmodel.MainViewModel


@Composable
fun ProfileBetHistoryContainer(viewModel: MainViewModel) {
    val userBetHistory by viewModel.userBetHistory.collectAsState()
    Column() {
        Row(Modifier.padding(8.dp)) {
            Text(
                "Bet History",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = White
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(userBetHistory) { bet ->
                ProfileBetHistoryRow(betInformation = bet)
            }
        }
    }
}

@Composable
fun ProfileBetHistoryRow(betInformation: BetInformation) {
    val imageResource =
        if (betInformation.betSide == "BEAR") Utility.DOWN_ARROW else Utility.UP_ARROW
    val betColor = if (betInformation.betSide == "BEAR") BetRed else BetGreen
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(NotSoDeepPurple)
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
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
                        color = White
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(Modifier.width(4.dp))
                        Image(
                            painter = painterResource(imageResource),
                            contentDescription = betInformation.betSide,
                            modifier = Modifier.size(10.dp),
                            colorFilter = ColorFilter.tint(betColor)
                        )
                    }
//                    Spacer(Modifier.weight(1f))
                }
                Row {
                    Text(
                        "Bet amount: ",
                        color = White,
                        fontSize = 14.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        betInformation.initialBetAmount.formatBigLong(),
                        color = White,
                        fontSize = 14.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
                if (betInformation.betStatus == "won") {
                    Text(
                        "Win Multiplier -> ${betInformation.winMultiplier}",
                        color = White,
                        fontSize = 14.sp,
                        fontFamily = poppinsFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Italic
                    )
                }
                Text(
                    "${betInformation.timestamp.toDate()}",
                    color = Gray,
                    fontSize = 10.sp,
                    fontFamily = poppinsFontFamily,
                    fontWeight = FontWeight.Normal
                )
            }
            Spacer(Modifier.weight(1f))
            when (betInformation.betStatus) {
                "active" -> {
                    Text(
                        text = "Pending",
                        fontFamily = poppinsFontFamily,
                        color = BetYellow,
                        fontSize = 16.sp
                    )
                }
                "won" -> {
                    CashAmountAndIcon(
                        color = BetGreen,
                        textSize = 14,
                        imageSize = 14,
                        cashAmount = betInformation.winnings
                    )
                }
                "lost" -> {
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
}

@Composable
fun ProfilePictureAndInfo(
    userAccountInformation: UserAccountInformation,
    editNameFunction: (String) -> Unit,
    changeUserNameValue: ChangeUserNameValue,
    badUserName: Boolean
) {
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
        UserNameWithEditButton(
            editNameFunction = editNameFunction,
            userAccountInformation = userAccountInformation,
            changeUserNameValue = changeUserNameValue,
            badUserName = badUserName
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EloWithRankImage(userAccountInformation.eloScore)
            Spacer(Modifier.height(8.dp))
            BalanceWithCashImage(userAccountInformation.userBalance)
        }
    }
}

@Composable
fun UserNameWithEditButton(
    editNameFunction: (String) -> Unit,
    userAccountInformation: UserAccountInformation,
    changeUserNameValue: ChangeUserNameValue,
    badUserName: Boolean

) {
    val originalUserNameText = userAccountInformation.userName
    var userNameText by rememberSaveable { mutableStateOf(userAccountInformation.userName) }
    var textFieldEnabled by remember { mutableStateOf(false) }
    val buttonColor: Color by animateColorAsState(targetValue = if (textFieldEnabled && changeUserNameValue != CHANGED) Color.Green else White)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.checkmark_icon),
            contentDescription = "Edit name",
            modifier = Modifier
                .size(18.dp)
                .padding(end = 8.dp)
                .alpha(0f)
        )
        if (textFieldEnabled && changeUserNameValue != CHANGED || changeUserNameValue == CHANGE_FAILED) {
            BasicTextField(
                value = userNameText,
                onValueChange = {
                    userNameText = it
                },
                modifier = Modifier.width(IntrinsicSize.Min),
                textStyle = LocalTextStyle.current.copy(color = White),
                cursorBrush = SolidColor(White)
            )
        } else {
            Text(
                text = userNameText,
                fontFamily = poppinsFontFamily,
                color = White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
        if (changeUserNameValue != CHANGE_IN_PROGRESS) {
            Image(
                painter = if (textFieldEnabled && changeUserNameValue != CHANGED || changeUserNameValue == CHANGE_FAILED) painterResource(
                    id = R.drawable.checkmark_icon
                ) else painterResource(
                    id = R.drawable.edit_icon
                ),
                contentDescription = "Edit name",
                colorFilter = ColorFilter.tint(color = buttonColor),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(14.dp)
                    .clickable {
                        if (textFieldEnabled) {
                            editNameFunction(userNameText)
                        }
                        textFieldEnabled = true
                    }
            )
        } else {
            LoadingSpinner(color = Color.Gray, size = 18.dp)
        }
        AnimatedVisibility(visible = (textFieldEnabled && changeUserNameValue != CHANGED)) {
            Image(
                painter = painterResource(id = R.drawable.cancel_icon),
                contentDescription = "cancel name edit",
                colorFilter = ColorFilter.tint(color = Color.Red),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
                    .clickable {
                        textFieldEnabled = false
                        userNameText = originalUserNameText
                    }
            )
        }
    }
}

@Composable
fun EloWithRankImage(userEloScore: Long) {
    val eloRank = getEloRank(userEloScore)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Column(
            modifier = Modifier.width(300.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Rank ${eloRank.ordinal}:",
                fontFamily = poppinsFontFamily,
                fontSize = 10.sp,
                color = White,
                fontStyle = FontStyle.Italic,
                textAlign = TextAlign.Center
            )
            Text(
                text = eloRank.title,
                fontFamily = poppinsFontFamily,
                fontSize = 14.sp,
                color = White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row {
            for (i in 0 until eloRank.amountOfStars) {
                Image(
                    painter = painterResource(id = eloRank.starIcon),
                    contentDescription = "star",
                    modifier = Modifier
                        .size(22.dp)
                        .padding(2.dp),
                    colorFilter = ColorFilter.tint(color = eloRank.color)
                )
            }
        }
    }
}

@Composable
fun BalanceWithCashImage(userBalance: Long) {
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
            text = userBalance.formatBigLong(),
            fontFamily = poppinsFontFamily,
            fontSize = 12.sp,
            color = White
        )
    }
}

@Composable
fun ProfileTopBar(
    viewModel: MainViewModel
) {
    val c = LocalContext.current
    Row(
        Modifier
            .wrapContentWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Invisible icon to make for proper row spacing
        TopBarIcon(
            icon = R.drawable.sign_out_icon,
            contentDesc = "Sign out button",
            signOutFunctionality = { viewModel.signOut(c) }
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Profile",
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = White
        )
        Spacer(modifier = Modifier.weight(1f))
        TopBarIcon(icon = R.drawable.settings_gear_icon, contentDesc = "Settings")
    }
}

