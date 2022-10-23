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
                Row(modifier = Modifier.padding(8.dp)) {
                    BearVBullTitle()
                    Spacer(modifier = Modifier.weight(1.0F))
                }
                Button(onClick = {
                    mainViewModel.activeUserId = mainViewModel.fakeUser.userId
                }) {
                    Text("go to main screen")
                }
            }
        }
    }
}