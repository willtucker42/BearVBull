package com.example.bearvbull.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bearvbull.BetWindow
import com.example.bearvbull.OrderBook
import com.example.bearvbull.TopBar
import com.example.bearvbull.ui.components.BetScreenStatusTitle
import com.example.bearvbull.ui.theme.DeepPurple
import com.example.bearvbull.viewmodel.MainViewModel

@Composable
fun BetScreen(
    viewModel: MainViewModel
) {
    val activeMarketData by viewModel.liveMarketDataFlow.collectAsState()
    val activeMarkets by viewModel.activeMarkets.collectAsState()
    val countDownTime by viewModel.countDownTime.collectAsState()
    val liveOrderBookData by viewModel.liveOrderBook.collectAsState()
    val liveParticipatingMarketsMap by viewModel.participatingInMarketsMapStateFlow.collectAsState()
    val transactionStatus by viewModel.betTxnStatus.collectAsState()

    Surface {
        Box(
            modifier = Modifier
                .background(DeepPurple)
        ) {
            Column {
                TopBar(txnStatus = transactionStatus, viewModel = viewModel)
                BetScreenStatusTitle(
                    activeMarketsList = activeMarkets,
                    activeMarketData = activeMarketData,
                    changeMarket = { marketId: String -> viewModel.onSelectedTickerChanged(marketId) }
                )
                BetWindow(
                    countDownTime = countDownTime,
                    activeMarketData = activeMarketData,
                    viewModel = viewModel,
                    liveParticipatingMarketsMap = liveParticipatingMarketsMap
                )
                Spacer(Modifier.height(12.dp))
                OrderBook(liveOrderBook = liveOrderBookData)
            }
        }
    }
}