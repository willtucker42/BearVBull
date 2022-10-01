package com.example.bearvbull.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bearvbull.BetWindow
import com.example.bearvbull.OrderBook
import com.example.bearvbull.TopBar
import com.example.bearvbull.data.LivePredictionMarketData
import com.example.bearvbull.data.OrderBook
import com.example.bearvbull.ui.components.BetScreenStatusTitle
import com.example.bearvbull.ui.theme.DeepPurple

@Composable
fun BetScreen(countDownTime: String, liveBetData: LivePredictionMarketData, liveOrderBookData: OrderBook) {
    Surface{
        Box(
            modifier = Modifier
                .background(DeepPurple)
        ) {
            Column(
//                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                TopBar()
                BetScreenStatusTitle()
                BetWindow(
                    countDownTime = countDownTime,
                    liveBetData = liveBetData
                )
                Spacer(Modifier.height(12.dp))
                OrderBook(liveOrderBook = liveOrderBookData)
            }
        }
    }
}