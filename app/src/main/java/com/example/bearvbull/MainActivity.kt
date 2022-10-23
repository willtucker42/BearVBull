package com.example.bearvbull

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bearvbull.data.ActiveMarket
import com.example.bearvbull.data.BetInformation
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.ui.components.*
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.ui.views.BetScreen
import com.example.bearvbull.ui.views.ProfileScreen
import com.example.bearvbull.ui.views.RankingsScreen
import com.example.bearvbull.ui.views.SignInScreen
import com.example.bearvbull.util.*
import com.example.bearvbull.viewmodel.MainViewModel
import com.google.firebase.Timestamp
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BearVBullTheme {
                val mainViewModel = viewModel<MainViewModel>()
//                val mainViewModel = MainViewModel()
                mainViewModel.manualInit()
                val selectedScreen by mainViewModel.selectedNavItem.collectAsState()
                val userId by mainViewModel.activeUser.collectAsState()
                Box(
                    modifier = Modifier
                        .background(DeepPurple)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    ) {
                        if (userId.userId != "") {
                            when (selectedScreen) {
                                NavBarItems.BET_SCREEN ->
                                    BetScreen(
                                        viewModel = mainViewModel
                                    )
                                NavBarItems.RANKINGS_SCREEN -> RankingsScreen(mainViewModel)
                                NavBarItems.PROFILE_SCREEN -> ProfileScreen(viewModel = mainViewModel)
                            }
                        } else {
                            SignInScreen(mainViewModel = mainViewModel)
                        }
                    }
                    if (userId.userId != "") {
                        BottomNavBar(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            viewModel = mainViewModel,
                            selectedScreen = selectedScreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavBar(modifier: Modifier, viewModel: MainViewModel, selectedScreen: NavBarItems) {
    Row(
        modifier = modifier
            .height(64.dp)
            .background(ButtonOutline)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (navItem in navBarItemList) {
            BottomNavBarItem(
                navItem,
                viewModel = viewModel,
                modifier = Modifier.weight(1f),
                selectedScreen = selectedScreen
            )
        }
    }
}

@Composable
fun BetWindow(
    countDownTime: String,
    activeMarketData: ActiveMarket,
    viewModel: MainViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainBetPromptTitle(countDownTime_ = countDownTime, activeMarket = activeMarketData)
        Spacer(modifier = Modifier.height(16.dp))
        BetButtonRow(activeMarketData = activeMarketData, viewModel = viewModel)
    }
}

@Composable
fun OrderBook(liveOrderBook: List<OrderBookEntry>) {
    Column(
        modifier = Modifier
            .height((LocalConfiguration.current.screenHeightDp / 2.5).dp)
            .padding(horizontal = 12.dp)
    ) {
        Row {
            Text(
                text = "Order Book",
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1.0F))
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            OrderBookLabelRow()
            LazyColumn(
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(items = liveOrderBook.reversed()) { order ->
                    OrderBookEntryRow(
                        orderBookEntry = order
                    )
                }
            }
        }
    }
}

@Composable
fun BetButtonRow(activeMarketData: ActiveMarket, viewModel: MainViewModel) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BetButtonContainer(
            percentage = activeMarketData.getBearAndBullPercentages().first,
            icon = R.drawable.arrow_down,
            backgroundColor = BetRed,
            description = "Down arrow",
            betSide = BetSide.BEAR,
            activeMarketData = activeMarketData,
            viewModel = viewModel
        )
        Spacer(modifier = Modifier.width(16.dp))
        BetButtonContainer(
            percentage = activeMarketData.getBearAndBullPercentages().second,
            icon = R.drawable.arrow_up,
            backgroundColor = BetGreen,
            description = "Up arrow",
            betSide = BetSide.BULL,
            activeMarketData = activeMarketData,
            viewModel = viewModel
        )
    }
}

@Composable
fun BetButtonContainer(
    percentage: Double,
    icon: Int = R.drawable.arrow_up,
    backgroundColor: Color = BetGreen,
    betSide: BetSide,
    description: String = "blah",
    activeMarketData: ActiveMarket,
    viewModel: MainViewModel
) {
    Surface(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, ButtonOutline),
        elevation = 8.dp
    ) {
        var showTextField by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .background(DeepPurple),
        ) {
            BetButton(
                percentage = percentage,
                icon = icon,
                backgroundColor = backgroundColor,
                description = description,
                onClickMethod = {
                    showTextField = true
                }
            )
            var text by rememberSaveable { mutableStateOf("") }
            var inputError by remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current
            val context = LocalContext.current
            if (showTextField) {
                Row (verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = text,
                        onValueChange = { value ->
                            text = value.filter {
                                it.isDigit() || it == '.'
                            }
                            inputError = false
                        },
                        label = { Text("Bet amount") },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = DeepPurple,
                            textColor = Color.White,
                            focusedLabelColor = Color.White,
                            placeholderColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                        modifier = Modifier.width(120.dp),
                        isError = inputError
                    )
                    Spacer(Modifier.width(2.dp))
                    Image(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        Modifier
                            .size(24.dp)
                            .clickable {
                                if (text.isNotEmpty() && text.toLong() > 0) {
                                    viewModel.addUserBet(
                                        betInformation = BetInformation(
                                            tickerSymbol = activeMarketData.ticker,
                                            initialBetAmount = text.toLong(),
                                            betSide = betSide.name,
                                            winMultiplier = activeMarketData
                                                .getReturnRatio(betSide)
                                                .substringAfter(':')
                                                .toDouble(),
                                            userId = UUID
                                                .randomUUID()
                                                .toString(),
                                            betStatus = "active",
                                            marketId = activeMarketData.marketId,
                                            timestamp = Timestamp(Calendar.getInstance().time),
                                            odds = percentage
                                        )
                                    )
                                    focusManager.clearFocus()
                                    Toast
                                        .makeText(context, "Bet placed", Toast.LENGTH_LONG)
                                        .show()
                                } else {
                                    inputError = true
                                }
                                text = ""
                            },
                        colorFilter = ColorFilter.tint(color = Color.White)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .background(DeepPurple)
                    .padding(4.dp)
            ) {
                for (infoType in betInfoTypeList) {
                    BetInformationRow(
                        infoType = infoType,
                        betSide = betSide,
                        activeMarketData = activeMarketData
                    )
                }
            }
        }
    }
}

@Composable
fun BetInformationRow(
    infoType: BetInfoType,
    betSide: BetSide,
    activeMarketData: ActiveMarket
) {
    Row {
        BetInfoImage(infoType = infoType)
        Spacer(modifier = Modifier.weight(1.0f))
        BetInfoLabel(infoType = infoType, betSide = betSide, activeMarketData = activeMarketData)
    }
}

@Composable
fun BetButton(
    percentage: Double,
    icon: Int = R.drawable.arrow_up,
    backgroundColor: Color = BetGreen,
    description: String = "blah",
    onClickMethod: () -> Unit,
) {
    Button(
        onClick = { onClickMethod() },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor.copy(alpha = 1f),
            contentColor = DeepPurple
        )
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = description,
                modifier = Modifier
                    .padding(4.dp)
                    .size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                fontFamily = poppinsFontFamily,
                fontSize = 16.sp,
                text = "$percentage$PERCENT_SIGN",
                textAlign = TextAlign.Center,
                color = DeepPurple
            )
        }
    }
}

@Composable
fun UserTotalBalance(balance: Double = 1123.44, viewModel: MainViewModel) {
    val shadowStyle = MaterialTheme.typography.button.copy(
        shadow = Shadow(
            color = Color.White,
            offset = Offset(4f, 4f),
            blurRadius = 4f
        )
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
//        Image(
//            painter = painterResource(id = R.drawable.bucks_iconv2),
//            contentDescription = "Bucks icon",
//            modifier = Modifier
//                .height(13.dp)
//                .padding(end = 2.5.dp),
//            colorFilter = ColorFilter.tint(color = Color.White)
//        )
        Text(
            text = balance.toString(),
            fontFamily = poppinsFontFamily,
            color = Color.White,
            style = shadowStyle
        )
    }
}

@Preview
@Composable
fun TopBar(title: String = "BearVBull", viewModel: MainViewModel = MainViewModel()) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopBarIcon(icon = R.drawable.profile_icon)
        Spacer(modifier = Modifier.weight(1.0f))
        BearVBullTitle()
        Spacer(modifier = Modifier.weight(1.0f))
        TopBarIcon(icon = R.drawable.money_bag_icon)
    }
}

//@Preview
@Composable
fun MainBetPromptTitle(
    activeMarket: ActiveMarket,
    countDownTime_: String = ""
) {
    val betPrompt = "Will $${activeMarket.ticker} open red or green tomorrow?"
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .background(Color.LightGray.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .wrapContentWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = betPrompt,
                fontFamily = poppinsFontFamily,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(1.0f))
                Text(
                    text = "Bets close in ",
                    textAlign = TextAlign.Center,
                    fontFamily = poppinsFontFamily,
                    color = Color.LightGray
                )
                Text(
                    text = countDownTime_,
                    textAlign = TextAlign.Center,
                    fontFamily = poppinsFontFamily,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.weight(1.0f))
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    BearVBullTheme {
//        Greeting("Android")
//    }
//}