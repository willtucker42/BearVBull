package com.example.bearvbull.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.components.BearVBullTitle
import com.example.bearvbull.ui.theme.DeepPurple
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.viewmodel.MainViewModel

@Composable
fun SignInScreen(mainViewModel: MainViewModel) {
    Surface {
        Box(
            modifier = Modifier.background(DeepPurple)
        ) {
            Column() {
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    BearVBullTitle()
                    Spacer(modifier = Modifier.weight(1.0F))
                }
                Row(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column() {
                        Text(
                            text = "The Stock Market Prediction App",
                            fontFamily = poppinsFontFamily,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 34.sp
                        )
                        Text(
                            text = "Elo rankings, leaderboards, and something else",
                            fontFamily = poppinsFontFamily,
                            color = Color.White,
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp
                        )
                    }
                    Spacer(modifier = Modifier.weight(1.0F))
                }

//                Button(onClick = {
//                    mainViewModel._activeUser.value = UserAccountInformation(userId = "willTucker42")
//                }) {
//                    Text("go to main screen")
//                }
            }
        }
    }
}