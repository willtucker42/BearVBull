package com.example.bearvbull

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.TextClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.util.PERCENT_SIGN

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BearVBullTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepPurple),
                    ) {
                        TopBar()
                        Row(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            BetButton(
                                percentage = 54,
                                icon = R.drawable.arrow_down,
                                backgroundColor = BetRed,
                                description = "Down arrow"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            BetButton(
                                percentage = 46,
                                icon = R.drawable.arrow_up,
                                backgroundColor = BetGreen,
                                description = "Up arrow"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BetButton(percentage: Int, icon: Int, backgroundColor: Color, description: String) {
    Button(
        onClick = { },
        modifier = Modifier
            .clip(shape = RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors
            (
            backgroundColor = backgroundColor.copy(alpha = 0.9f),
            contentColor = DeepPurple
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = description,
                modifier = Modifier
                    .padding(8.dp)
                    .size(50.dp)
            )
            Text(
                fontFamily = interFontFamily,
                fontSize = 32.sp,
                text = "$percentage$PERCENT_SIGN",
                textAlign = TextAlign.Center,
                color = DeepPurple
            )
        }
    }
}

@Composable
fun UserTotalBalance(balance: Double = 1123.44) {
    val shadowStyle = MaterialTheme.typography.button.copy(
        shadow = Shadow(
            color = Color.White,
            offset = Offset(4f,4f),
            blurRadius = 6f
        )
    )
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.bucks_iconv2),
            contentDescription = "Bucks icon",
            modifier = Modifier
                .height(13.dp)
                .padding(end = 2.5.dp),
            colorFilter = ColorFilter.tint(color = Color.White)
        )
        Text(
            text = balance.toString(),
            fontFamily = interFontFamily,
            color = Color.White,
            style = shadowStyle
        )
    }
}

@Preview
@Composable
fun TopBar(title: String = "BearVBull") {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
    ) {
        Text(
            text = title,
            fontFamily = interFontFamily,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1.0f))
        UserTotalBalance()
    }
}

@Preview
@Composable
fun MainBetPromptTitle(title: String = "Betting is ",ticker: String = "SPY") {
    val betPrompt = "Will $$ticker open red or green tomorrow?"
    Box (
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        Column (
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = betPrompt,
                fontFamily = interFontFamily,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = ""
            )
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