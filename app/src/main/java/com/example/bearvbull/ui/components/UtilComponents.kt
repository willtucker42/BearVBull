package com.example.bearvbull.ui.components


import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.bearvbull.R
import com.example.bearvbull.data.ActiveMarket
import com.example.bearvbull.data.ActiveMarkets
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.util.*
import com.example.bearvbull.util.NavBarItems.PROFILE_SCREEN
import com.example.bearvbull.util.Utility.DOWN_ARROW
import com.example.bearvbull.util.Utility.UP_ARROW
import com.example.bearvbull.util.Utility.simpleDateFormat
import com.example.bearvbull.viewmodel.MainViewModel


@Composable
fun BetScreenStatusTitle(
    marketStatus: String = "live",
    activeMarketsList: ActiveMarkets,
    activeMarketData: ActiveMarket,
    changeMarket: (String) -> Unit
) {

    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 2000
                0.05f at 100
                0.1f at 200
                0.15f at 300
                0.2f at 400
                0.25f at 500
                0.3f at 600
                0.35f at 700
                0.40f at 800
                0.45f at 900
                0.5f at 1000
                0.55f at 1100
                0.60f at 1200
                0.65f at 1300
                0.7f at 1400
                0.75f at 1500
                0.8f at 1600
                0.85f at 1700
                0.9f at 1800
                0.95f at 1900
                1f at 2000
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    val color = if (marketStatus == "live") BetGreen else BetRed
    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Bet markets are ",
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = White
        )
        Text(
            text = marketStatus,
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = color
        )
        Spacer(modifier = Modifier.width(8.dp))
        Canvas(modifier = Modifier
            .size(12.dp)
            .alpha(alpha), onDraw = {
            drawCircle(color = color)
        })
        Spacer(modifier = Modifier.weight(1f))
        var expanded by remember { mutableStateOf(false) }
        val icon = if (expanded)
            Icons.Filled.KeyboardArrowUp
        else
            Icons.Filled.KeyboardArrowDown
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, ButtonOutline),
            elevation = 8.dp
        ) {
            Row(modifier = Modifier
                .clickable {
                    expanded = !expanded
                }
                .background(DeepPurple)
                .padding(8.dp)
            ) {
                Text(
                    text = if (activeMarketData.ticker == "") "..." else activeMarketData.ticker,
                    color = White
                )
                Icon(imageVector = icon, contentDescription = null)
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    activeMarketsList.activeMarkets.forEach { market ->
                        DropdownMenuItem(onClick = {
                            changeMarket(market.marketId)
                            expanded = false
                        }) {
                            Text(text = "$${market.ticker}")
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BetInfoImage(infoType: BetInfoType) {
    Image(
        painter = painterResource(id = infoType.icon),
        contentDescription = infoType.name,
        modifier = Modifier
            .padding(4.dp)
            .size(15.dp),
        colorFilter = ColorFilter.tint(color = White)
    )
}

@Composable
fun BetInfoLabel(infoType: BetInfoType, betSide: BetSide, activeMarketData: ActiveMarket) {
    Text(
        text = activeMarketData.createBetInfoLabel(infoType, betSide),
        fontFamily = interFontFamily,
        fontSize = 12.sp,
        color = White
    )
}

@Composable
fun BearVBullTitle() {
    Text(
        text = BEAR_V_BULL_TITLE,
        fontFamily = poppinsFontFamily,
        color = White,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    )
}

@Composable
fun TopBarProfileIcon(viewModel: MainViewModel) {
    Image(
        painter = painterResource(id = R.drawable.profile_icon),
        contentDescription = "Go to profile screen",
        modifier = Modifier
            .size(24.dp)
            .clickable { viewModel.navToDiffScreen(PROFILE_SCREEN) },
        colorFilter = ColorFilter.tint(color = White)
    )
}

@Composable
fun TopBarMoneyBagIcon(activeUser: UserAccountInformation) {
    val openComposable = remember { mutableStateOf(false) }
    Image(
        painter = painterResource(id = R.drawable.money_bag_icon),
        contentDescription = "See available balance",
        modifier = Modifier
            .size(24.dp)
            .clickable { openComposable.value = !openComposable.value },
        colorFilter = ColorFilter.tint(color = White)
    )
    if (openComposable.value) {
        Dialog(onDismissRequest = { openComposable.value = false }) {
            Text(
                "Available balance: ${activeUser.userBalance}",
                color = White, fontFamily = poppinsFontFamily, fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun TopBarIcon(
    icon: Int,
    contentDesc: String = "Profile",
    signOutFunctionality: () -> Unit = {},
    visible: Boolean = true
) {
    val openComposable = remember { mutableStateOf(false) }
    Image(
        painter = painterResource(id = icon),
        contentDescription = contentDesc,
        modifier = Modifier
            .size(24.dp)
            .clickable { openComposable.value = !openComposable.value }
            .alpha((if (visible) 1.0 else 0.0).toFloat()),
        colorFilter = ColorFilter.tint(color = White),
    )
    if (openComposable.value) {
        AlertDialog(
            onDismissRequest = { openComposable.value = false },
            title = { Text("Sign out?", fontFamily = poppinsFontFamily) },
            buttons = {
                Row(
                    modifier = Modifier.padding(all = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { openComposable.value = false },
                        colors = ButtonDefaults.buttonColors(backgroundColor = BetGreen),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text("Don't sign out", fontFamily = poppinsFontFamily, color = White)
                    }
                    Button(
                        onClick = {
                            openComposable.value = false
                            signOutFunctionality()
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = BetRed),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text("Sign out", color = White, fontFamily = poppinsFontFamily)
                    }
                }
            }
        )
    }
}

@Composable
fun OrderBookLabel(text: String, modifier: Modifier) {
    Text(
        text = text,
        fontFamily = poppinsFontFamily,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier,
        color = White
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
            text = "Stock", modifier = Modifier
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
        fontSize = 12.sp,
        color = White
    )
}

@Composable
fun OrderBookEntryPercentText(betside: String, percent: String, modifier: Modifier) {
    val imageResource = if (betside.lowercase() == "bear") DOWN_ARROW else UP_ARROW
    val betColor = if (betside.lowercase() == "bear") BetRed else BetGreen
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
fun OrderBookEntryRow(orderBookEntry: OrderBookEntry) {
    Row() {
        OrderBookEntryText(
            orderBookEntry.userName, modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
        OrderBookEntryPercentText(
            betside = orderBookEntry.betSide,
            percent = orderBookEntry.ticker,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F)
        )
//        OrderBookEntryText(
//            orderBookEntry.ticker, modifier = Modifier
//                .fillMaxWidth()
//                .weight(1F)
//        )
        OrderBookEntryText(
            orderBookEntry.amountWagered.formatBigLong(), modifier = Modifier
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


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun BottomNavBarItem(
    navBarItem: NavBarItems,
    viewModel: MainViewModel,
    modifier: Modifier,
    selectedScreen: NavBarItems
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .clickable {
                viewModel.navToDiffScreen(navBarItem)
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = navBarItem.icon),
            contentDescription = navBarItem.title,
            colorFilter = ColorFilter.tint(
                if (selectedScreen == navBarItem) White else Color.Gray
            ),
            modifier = Modifier.size(if (selectedScreen == navBarItem) 24.dp else 18.dp)
        )
        Text(
            navBarItem.title,
            fontFamily = poppinsFontFamily,
            fontSize = 12.sp,
            color = if (selectedScreen == navBarItem) White else Color.Gray
        )
    }
}

@Composable
fun LoadingSpinner(color: Color, size: Dp) {
    CircularProgressIndicator(
        modifier = Modifier.size(size),
        color = color
    )
}

@Composable
fun CashAmountAndIcon(
    color: Color,
    textSize: Int,
    imageSize: Int,
    cashAmount: Long
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.cash_icon),
            contentDescription = "Cash",
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(imageSize.dp)
        )
        Text(
            text = cashAmount.formatBigLong(),
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = textSize.sp,
            color = color
        )
    }
}
