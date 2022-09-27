@file:OptIn(ExperimentalFoundationApi::class)

package com.example.bearvbull

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bearvbull.data.LiveBetData
import com.example.bearvbull.data.OrderBook
import com.example.bearvbull.ui.components.*
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.ui.views.BetScreen
import com.example.bearvbull.ui.views.ProfileScreen
import com.example.bearvbull.ui.views.RankingsScreen
import com.example.bearvbull.util.*
import com.example.bearvbull.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BearVBullTheme {
                val mainViewModel = viewModel<MainViewModel>()
                val countDownTime by mainViewModel.countDownTime.collectAsState()
                val liveBetData by mainViewModel.liveBetDataFlow.collectAsState()
                val liveOrderBookData by mainViewModel.liveOrderBook.collectAsState()
                val selectedScreen by mainViewModel.selectedNavItem.collectAsState()

                var betScreenBool by remember {
                    mutableStateOf(false)
                }
                Box(
                    modifier = Modifier
                        .background(DeepPurple)
                ) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.TopCenter)
                    ) {
                        when (selectedScreen) {
                            NavBarItems.BET_SCREEN ->
                                BetScreen(
                                    countDownTime = countDownTime,
                                    liveBetData = liveBetData,
                                    liveOrderBookData = liveOrderBookData
                                )
                            NavBarItems.RANKINGS_SCREEN -> RankingsScreen(mainViewModel)
                            NavBarItems.PROFILE_SCREEN -> ProfileScreen(viewModel = mainViewModel)
                        }
                    }
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
    liveBetData: LiveBetData
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainBetPromptTitle(countDownTime_ = countDownTime)
        Spacer(modifier = Modifier.height(16.dp))
        BetButtonRow(liveBetData = liveBetData)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderBook(liveOrderBook: OrderBook) {
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
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.weight(1.0F))
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            OrderBookLabelRow()
            LazyColumn(
                contentPadding = PaddingValues(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                items(items = liveOrderBook.orderBook.reversed()) { order ->
                    OrderBookEntryRow(
                        orderBookEntry = order, modifier = Modifier.animateItemPlacement(
                            animationSpec = tween(
                                durationMillis = 200
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun BetButtonRow(liveBetData: LiveBetData) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BetButtonContainer(
            percentage = liveBetData.getBearAndBullPercentages().first,
            icon = R.drawable.arrow_down,
            backgroundColor = BetRed,
            description = "Down arrow",
            betSide = BetSide.BEAR,
            liveBetData = liveBetData
        )
        Spacer(modifier = Modifier.width(16.dp))
        BetButtonContainer(
            percentage = liveBetData.getBearAndBullPercentages().second,
            icon = R.drawable.arrow_up,
            backgroundColor = BetGreen,
            description = "Up arrow",
            betSide = BetSide.BULL,
            liveBetData = liveBetData
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
    liveBetData: LiveBetData
) {
    Surface(
        modifier = Modifier.width(150.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.5.dp, ButtonOutline),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(DeepPurple),
        ) {
            BetButton(
                percentage = percentage,
                icon = icon,
                backgroundColor = backgroundColor,
                description = description
            )
            Column(
                modifier = Modifier
                    .background(DeepPurple)
                    .padding(4.dp)
            ) {
                for (infoType in betInfoTypeList) {
                    BetInformationRow(
                        infoType = infoType,
                        betSide = betSide,
                        liveBetData = liveBetData
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
    liveBetData: LiveBetData
) {
    Row {
        BetInfoImage(infoType = infoType)
        Spacer(modifier = Modifier.weight(1.0f))
        BetInfoLabel(infoType = infoType, betSide = betSide, liveBetData = liveBetData)
    }
}

@Composable
fun BetButton(
    percentage: Double,
    icon: Int = R.drawable.arrow_up,
    backgroundColor: Color = BetGreen,
    description: String = "blah"
) {
    Button(
        onClick = { },
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
        Text(
            text = title,
            fontFamily = poppinsFontFamily,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(modifier = Modifier.weight(1.0f))
        TopBarIcon(icon = R.drawable.money_bag_icon)
    }
}

//@Preview
@Composable
fun MainBetPromptTitle(
    ticker: String = "SPY",
    countDownTime_: String = ""
) {
    val betPrompt = "Will $$ticker open red or green tomorrow?"
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