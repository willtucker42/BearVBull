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
import com.example.bearvbull.data.ActiveMarket
import com.example.bearvbull.data.ActiveMarkets
import com.example.bearvbull.data.LivePredictionMarketData
import com.example.bearvbull.data.OrderBook
import com.example.bearvbull.ui.components.BetScreenStatusTitle
import com.example.bearvbull.ui.theme.DeepPurple
import com.example.bearvbull.viewmodel.MainViewModel

@Composable
fun BetScreen(
    countDownTime: String,
    activeMarketData: ActiveMarket,
    liveOrderBookData: OrderBook,
    activeMarkets: ActiveMarkets,
    viewModel: MainViewModel
) {
    Surface {
        Box(
            modifier = Modifier
                .background(DeepPurple)
        ) {
            Column(
//                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                TopBar()
                BetScreenStatusTitle(activeMarketsList = activeMarkets)
                BetWindow(
                    countDownTime = countDownTime,
                    activeMarketData = activeMarketData,
                    viewModel = viewModel
                )
                Spacer(Modifier.height(12.dp))
                OrderBook(liveOrderBook = liveOrderBookData)
            }
        }
    }
}