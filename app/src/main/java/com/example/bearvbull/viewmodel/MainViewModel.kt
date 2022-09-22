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
import java.time.LocalDateTime
import java.util.*
import kotlin.math.roundToInt
import kotlin.random.Random

class MainViewModel : ViewModel() {

    // NavBar
    private var _selectedNavItem = MutableStateFlow(NavBarItems.BET_SCREEN)
    val selectedNavItem : StateFlow<NavBarItems> = _selectedNavItem
    // End nav bar

    private var countDownTimer: CountDownTimer? = null

    private var _countDownTime = MutableStateFlow("")
    val countDownTime: StateFlow<String> = _countDownTime
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    // OrderBook
    private var orderBookHolder: OrderBook
    private var _orderBookMutableStateFlow = MutableStateFlow(
        OrderBook(
            orderBook = mutableListOf(
                OrderBookEntry(
                    userName = "User01",
                    amountWagered = 0.00,
                    betSide = "Bear",
                    time = Date(),
                    betPercent = 0.00
                )
            )
        )
    )
    val liveOrderBook: StateFlow<OrderBook> = _orderBookMutableStateFlow
    // End OrderBook


    // BetData
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
    val liveBetDataFlow: StateFlow<LiveBetData> = betData
    // End bet data


    init {
        orderBookHolder = OrderBook(
            mutableListOf(
                OrderBookEntry(
                    userName = "User01",
                    amountWagered = 0.00,
                    betSide = "Bear",
                    time = Date(),
                    betPercent = 0.00
                )
            )
        )
        startTimer()
        viewModelScope.launch {
            generateOrderBookEntries()
        }
        viewModelScope.launch {
            generateAndUpdateLiveBetDataData()
        }
    }

    private suspend fun generateOrderBookEntries() {
        while (true) {
            delay(Random.nextLong(300, 1000))
            val userName = (1..10)
                .map { Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
            val amountWagered = Random.nextDouble(from = 0.0, until = 99999999.99).roundToInt()
            println("AmountWagered: $amountWagered")
            var betSide = ""
            val randomNum = Random.nextInt(from = 1, until = 100)
            val betPercent: Double
            betSide = if (randomNum % 2 == 0) {
                betPercent = betData.value.getBearAndBullPercentages().first
                "Bear"
            } else {
                betPercent = betData.value.getBearAndBullPercentages().second
                "Bull"
            }
            val time = Date()
            orderBookHolder.orderBook.add(
                OrderBookEntry(
                    userName = userName,
                    amountWagered = amountWagered.toDouble(),
                    betSide = betSide,
                    betPercent = betPercent,
                    time = time
                )
            )
            _orderBookMutableStateFlow.value = orderBookHolder
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
            println(randomData)
            betData.value = randomData
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