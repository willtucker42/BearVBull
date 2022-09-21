package com.example.bearvbull

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bearvbull.data.LiveBetData
import com.example.bearvbull.ui.components.BetInfoImage
import com.example.bearvbull.ui.components.BetInfoLabel
import com.example.bearvbull.ui.components.TopBarIcon
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.util.BetInfoType
import com.example.bearvbull.util.BetSide
import com.example.bearvbull.util.PERCENT_SIGN
import com.example.bearvbull.util.betInfoTypeList
import com.example.bearvbull.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BearVBullTheme {
                val mainViewModel = viewModel<MainViewModel>()
                val countDownTime by mainViewModel.countDownTime.collectAsState()
                val liveBetData by mainViewModel.liveBetDataFlow.collectAsState()
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepPurple),
                    ) {
                        TopBar()
                        BetWindow(
                            modifier = Modifier.align(Alignment.Center),
                            viewModel = mainViewModel,
                            countDownTime = countDownTime,
                            liveBetData = liveBetData
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BetWindow(
    modifier: Modifier,
    viewModel: MainViewModel,
    countDownTime: String,
    liveBetData: LiveBetData
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainBetPromptTitle(countDownTime_ = countDownTime)
        Spacer(modifier = Modifier.height(16.dp))
        BetButtonRow(liveBetData = liveBetData)
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
            fontSize = 22.sp,
            modifier = Modifier.padding(1.dp)
        )
        Spacer(modifier = Modifier.weight(1.0f))
        TopBarIcon(icon = R.drawable.resume_icon)
    }
}

//@Preview
@Composable
fun MainBetPromptTitle(
    title: String = "Betting is ",
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