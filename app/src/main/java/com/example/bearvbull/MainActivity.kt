package com.example.bearvbull

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bearvbull.data.ActiveMarket
import com.example.bearvbull.data.BetInformation
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.components.*
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.ui.views.*
import com.example.bearvbull.util.*
import com.example.bearvbull.util.NavBarItems.*
import com.example.bearvbull.util.SignInStatus.*
import com.example.bearvbull.viewmodel.MainViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Timestamp
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fun googleLogin(): GoogleSignInClient {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(resources.getString(R.string.SERVER_CLIENT_ID))
                .requestId()
                .requestProfile()
                .build()
            return GoogleSignIn.getClient(this, gso)
        }

        val sp: SharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
        setContent {
            BearVBullTheme {
                val viewModel = viewModel<MainViewModel>()
                val selectedScreen by viewModel.selectedNavItem.collectAsState()
                val signInStatus by viewModel.signInStatus.collectAsState()

                Box(
                    modifier = Modifier
                        .background(DeepPurple)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    ) {
                        when (signInStatus) {
                            SIGNED_IN -> {
                                if (!viewModel.manualInitComplete) {
                                    viewModel.manualInit()
                                }
                                when (selectedScreen) {
                                    BET_SCREEN -> BetScreen(viewModel)
                                    RANKINGS_SCREEN -> RankingsScreen(viewModel)
                                    PROFILE_SCREEN -> ProfileScreen(viewModel)
                                    else -> {}
                                }
                            }
                            NOT_SIGNED_IN -> SignInScreen(viewModel, googleLogin(), sp)
                            CHECKING_SHARED_PREFS -> SplashScreen()
                            else -> {}
                        }
                    }
                    if (signInStatus == SIGNED_IN) {
                        BottomNavBar(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            viewModel = viewModel,
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
    viewModel: MainViewModel,
    liveParticipatingMarketsMap: Map<String, BetInformation>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainBetPromptTitle(
            countDownTime_ = countDownTime,
            activeMarket = activeMarketData,
            liveParticipatingMarketsMap = liveParticipatingMarketsMap
        )
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
                    showTextField = !showTextField
                }
            )
            var text by rememberSaveable { mutableStateOf("") }
            var inputError by remember { mutableStateOf(false) }
            val focusManager = LocalFocusManager.current
            val context = LocalContext.current
            if (showTextField) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    viewModel.beginUserBetFlow(
                                        betInformation = BetInformation(
                                            tickerSymbol = activeMarketData.ticker,
                                            initialBetAmount = text.toLong(),
                                            betSide = betSide.name,
                                            winMultiplier = activeMarketData
                                                .getReturnRatio(betSide)
                                                .substringAfter(':')
                                                .toDouble(),
                                            userId = viewModel.activeUser.value.userId,
                                            betStatus = "active",
                                            marketId = activeMarketData.marketId,
                                            timestamp = Timestamp(Calendar.getInstance().time),
                                            odds = percentage
                                        ), context
                                    )
                                    focusManager.clearFocus()
                                    text = ""
                                } else {
                                    inputError = true
                                }
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


@Composable
fun TopBar(
    txnStatus: BetTransactionStatus,
    viewModel: MainViewModel,
    activeUser: UserAccountInformation
) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TopBarProfileIcon(viewModel = viewModel)
        Spacer(modifier = Modifier.weight(1.0f))
        BearVBullTitle()
        Spacer(modifier = Modifier.weight(1.0f))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (txnStatus == BetTransactionStatus.SENDING_BET) {
                LoadingSpinner(color = Color.LightGray, size = 15.dp)
            }
            TopBarMoneyBagIcon(activeUser = activeUser)
        }
    }
}

//@Preview
@Composable
fun MainBetPromptTitle(
    activeMarket: ActiveMarket,
    countDownTime_: String = "",
    liveParticipatingMarketsMap: Map<String, BetInformation>
) {
    val betPrompt = "Will  $${activeMarket.ticker}  open red or green tomorrow?"
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