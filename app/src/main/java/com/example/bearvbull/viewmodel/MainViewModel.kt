package com.example.bearvbull.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bearvbull.R
import com.example.bearvbull.data.BetInformation
import com.example.bearvbull.data.LivePredictionMarketData
import com.example.bearvbull.data.OrderBook
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.util.*
import com.example.bearvbull.util.Utility.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random

class MainViewModel : ViewModel() {

    // NavBar
    private var _selectedNavItem = MutableStateFlow(NavBarItems.RANKINGS_SCREEN)
    val selectedNavItem: StateFlow<NavBarItems> = _selectedNavItem
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
        LivePredictionMarketData(
            betId = "abc123",
            bearTotal = 89763.12,
            bullTotal = 189699.76,
            totalBears = 3783,
            totalBulls = 10075,
            biggestBearBet = 50000.00,
            biggestBullBet = 103098.00
        )
    )
    val liveBetDataFlow: StateFlow<LivePredictionMarketData> = betData
    // End bet data

    //    lateinit var fakeAccountInformation: RankingsUserList
    val fakeRankingsUserList = mutableListOf<UserAccountInformation>()
    val fakeUser = UserAccountInformation(
        userId = "123",
        userName = "willTucker42",
        70198817,
        R.drawable.green_wojak,
        1
    )
    val fakeBetHistory = mutableListOf<BetInformation>()

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
        generateRankingsUserList()
        generateBetHistory()
        startTimer()
        viewModelScope.launch {
            generateOrderBookEntries()
        }
        viewModelScope.launch {
            generateAndUpdateLiveBetDataData()
        }
    }

    private fun generateBetHistory() {
        val df = DecimalFormat("0.00")
        for (i in 0..200) {
            val initialBetAmount = Random.nextLong(0, 199999999)
            val betSide = if (Random.nextInt(0, 2) == 0) {
                "Bull"
            } else {
                "Bear"
            }
            val didWin = Random.nextInt(0, 2) == 0
            val odds = Random.nextDouble(0.00, 100.00).round()

            println("asdfasdf $initialBetAmount * $odds =  ${(initialBetAmount * odds).roundToLong()}")
            val winningsMultiplier = (100 / odds)
            val winnings = if (didWin) {
                (initialBetAmount * winningsMultiplier).roundToLong()
            } else {
                0
            }
            fakeBetHistory.add(
                BetInformation(
                    initialBetAmount = initialBetAmount,
                    betSide = betSide,
                    odds = odds,
                    winningsMultiplier = winningsMultiplier,
                    winnings = winnings,
                    didWin = didWin
                )
            )
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
//            println("AmountWagered: $amountWagered")
            var betSide: String
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

    private fun generateRankingsUserList() {
        for (i in 0..200) {
            fakeRankingsUserList.add(UserAccountInformation(
                userId = (1..10)
                    .map { Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString(""),
                userName = (1..10)
                    .map { Random.nextInt(0, charPool.size) }
                    .map(charPool::get)
                    .joinToString(""),
                rank = i,
                userBalance = Random.nextLong(from = 11111, until = 111111111),
                profileImage = R.drawable.green_wojak
            ))
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
            val randomData = LivePredictionMarketData(
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

//    val liveUserAccountInformation = UserAccountInformation(
//        userId = "123",
//        userBalance = 1111111
//    )

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

    fun navToDiffScreen(screen: NavBarItems) {
        _selectedNavItem.value = screen
    }

}