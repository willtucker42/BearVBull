package com.example.bearvbull

import android.graphics.drawable.Drawable
import android.os.Bundle
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.ui.theme.BearVBullTheme
import com.example.bearvbull.ui.theme.BetGreen
import com.example.bearvbull.ui.theme.BetRed
import com.example.bearvbull.util.PERCENT_SIGN

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BearVBullTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
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
fun Greeting(name: String) {
    Text(text = "Hello $name!", color = Color.White)
}

@Composable
fun BetButton(percentage: Int, icon: Int, backgroundColor: Color, description: String) {
    Button(
        onClick = {  },
        modifier = Modifier
            .clip(shape = RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors
            (backgroundColor = backgroundColor.copy(alpha = 0.9f),
            contentColor = Color.White)
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
                fontStyle = FontStyle.Italic,
                fontSize = 32.sp,
                text = "$percentage$PERCENT_SIGN",
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BearVBullTheme {
        Greeting("Android")
    }
}