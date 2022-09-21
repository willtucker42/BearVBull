package com.example.bearvbull.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bearvbull.data.LiveBetData
import com.example.bearvbull.data.OrderBook
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.data.UserAccountInformation
import com.example.bearvbull.util.*
import com.example.bearvbull.util.Utility.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private var _countDownTime = MutableStateFlow("")
    val countDownTime: StateFlow<String> = _countDownTime

    var orderBookList = OrderBook()

    private var _orderBookMutableStateFlow = MutableStateFlow(
        OrderBook(
            orderBook = mutableListOf(
                OrderBookEntry(
                    userName = "User01",

                )
            )
        )
    )

    val liveOrderBook: StateFlow<OrderBook> = _orderBookMutableStateFlow

    private var betData = MutableStateFlow(
        LiveBetData(
            betId = "abc123",
            bearTotal = 89763.12,
            bullTotal = 189699.76,
            totalBears = 3783,
            totalBulls = 10075,
            biggestBearBet = 50000.00,
            biggestBullBet = 103098.00
        )
    )

    /**
     * MainActivity composables with consume this flow
     */
    val liveBetDataFlow: StateFlow<LiveBetData> = betData

    val liveBetDataTestData = LiveBetData(
        betId = "abc123",
        bearTotal = 89763.12,
        bullTotal = 189699.76,
        totalBears = 3783,
        totalBulls = 10075,
        biggestBearBet = 50000.00,
        biggestBullBet = 103098.00
    )

    init {
        startTimer()
        viewModelScope.launch {
            generateAndUpdateLiveBetDataData()
        }
    }

    private suspend fun generateAndUpdateLiveBetDataData() {
        while (true) {
            delay(1000L)
            val bearTotal = Random.nextDouble(from = 0.0, until = 9999999999.99)
            val bullTotal = Random.nextDouble(from = 0.0, until = 9999999999.99)
            val totalBears = Random.nextInt(from = 0, until = 9999999)
            val totalBulls = Random.nextInt(from = 0, until = 9999999)
            val biggestBearBet = Random.nextDouble(from = 0.0, until = bearTotal)
            val biggestBullBet = Random.nextDouble(from = 0.0, until = bullTotal)
            val randomData = LiveBetData(
                betId = "123",
                bearTotal = bearTotal,
                bullTotal = bullTotal,
                totalBears = totalBears,
                totalBulls = totalBulls,
                biggestBearBet = biggestBearBet,
                biggestBullBet = biggestBullBet,
            )
            betData.value = randomData
            orderBookList.orderBook.add(randomData)
            _orderBookMutableStateFlow.value = orderBookList
        }
    }

    val liveUserAccountInformation = UserAccountInformation(
        userId = "123",
        userBalance = 1092832.99
    )

    val countDownFlow = flow {
        val startingValue = 10
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }


    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Utility.TIME_COUNTDOWN, 1) {
            override fun onTick(millisRemaining: Long) {
                val progressValue = millisRemaining.toFloat() / Utility.TIME_COUNTDOWN
                _countDownTime.value = millisRemaining.formatTime()
            }

            override fun onFinish() {
                _countDownTime.value = "00:00:00"
            }
        }.start()
    }


}