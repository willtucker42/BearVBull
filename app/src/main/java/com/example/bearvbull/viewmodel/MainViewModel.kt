package com.example.bearvbull.viewmodel

import android.os.CountDownTimer
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bearvbull.R
import com.example.bearvbull.data.*
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.util.*
import com.example.bearvbull.util.Utility.formatTime
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.StructuredQuery.Order
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val ORDERBOOK_QUERY_LIMIT: Long = 1000

    // NavBar
    private val db: FirebaseFirestore = Firebase.firestore
    private var _selectedNavItem = MutableStateFlow(NavBarItems.BET_SCREEN)
    val selectedNavItem: StateFlow<NavBarItems> = _selectedNavItem
    // End nav bar

    // currently selected marketId
    private var _selectedMarketId = MutableStateFlow("SPY")
    val selectedMarketId: StateFlow<String> = _selectedMarketId
    val currentMarketId: String = "SPY"
    // end currently selected marketId

    private var countDownTimer: CountDownTimer? = null

    private var activeMarketsHolder: ActiveMarkets = ActiveMarkets()
    private var _activeMarkets = MutableStateFlow(ActiveMarkets())
    val activeMarkets: StateFlow<ActiveMarkets> = _activeMarkets

    private var _countDownTime = MutableStateFlow("")
    val countDownTime: StateFlow<String> = _countDownTime
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    //    private val todaysBetDocument: DocumentReference
    // OrderBook

//    private var newOrderBookHolder:


    private var orderBookHolder: MutableList<OrderBookEntry> = mutableListOf(OrderBookEntry())
    private var _orderBookMutableStateFlow = MutableStateFlow<List<OrderBookEntry>>(listOf())
    val liveOrderBook: MutableStateFlow<List<OrderBookEntry>> = _orderBookMutableStateFlow
    // End OrderBook


    // BetData
    private var activeMarketData = MutableStateFlow(ActiveMarket())
    val liveMarketDataFlow: StateFlow<ActiveMarket> = activeMarketData
    // End bet data

    // ActiveMarket

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
        viewModelScope.launch {
            startTimer()
        }

        generateRankingsUserList()
        generateBetHistory()

//        viewModelScope.launch {
//            launch { getActiveMarkets() }
//            launch { getMarketBookData() }
//            launch { generateOrderBookEntries() }
//            launch { generateAndUpdateLiveBetDataData() }
//        }
        viewModelScope.launch {
            getActiveMarkets()
        }
        getMarketBookData()

        viewModelScope.launch {
            generateOrderBookEntries()
        }
        viewModelScope.launch {
            generateAndUpdateLiveBetDataData()
        }
    }

    fun addUserBet(betInformation: BetInformation) {
        val userBet = hashMapOf(
            "bet_amount" to betInformation.initialBetAmount,
            "bet_side" to betInformation.betSide,
            "bet_status" to betInformation.betStatus,
            "market_id" to betInformation.marketId,
            "ticker_symbol" to betInformation.tickerSymbol,
            "timestamp" to betInformation.timestamp,
            "user_id" to betInformation.userId,
            "win_multiplier" to betInformation.winMultiplier,
            "odds" to betInformation.odds,
            "did_win" to betInformation.didWin
        )
        println(userBet)
        db.collection("all_user_bets")
            .add(userBet)
            .addOnSuccessListener { docRef ->
                Timber.i("addUserBet DocumentSnapshot added with ID: ${docRef.id}")
            }
            .addOnFailureListener { e ->
                Timber.e("Error adding document, $e")
            }
    }

    private fun getActiveMarkets() {
        Timber.i("in getActiveMarkets")
        db.collection("live_prediction_market_info")
            .whereEqualTo("bet_status", "live")
            .get()
            .addOnSuccessListener { result ->
                result.forEach { doc ->
                    println("thedoc ${doc.id} => ${doc.data}")
                    activeMarketsHolder.activeMarkets.add(doc.toObject(ActiveMarket::class.java))
                    _activeMarkets.value = activeMarketsHolder
                }
                _selectedMarketId.value = result.first().id
            }
            .addOnFailureListener { e ->
                Timber.e("Error getting documents. $e")
            }
    }

    private fun getMarketBookData() {
        Timber.i("in  getMarketBookData.")
        db.collection("all_user_bets")
            .whereEqualTo("bet_status", "active")
            .get()
            .addOnSuccessListener { result ->
                Timber.i("lmao?")
                result.forEach { doc ->
                    Timber.i("Trade doc: $doc")
                }
            }
            .addOnFailureListener { e ->
                Timber.e("Error getting trade history: $e")
            }
            .addOnCanceledListener {
                Timber.i("CANCELED?")
            }
            .addOnCompleteListener {
                Timber.i("COMPLETE?")
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

//            println("asdfasdf $initialBetAmount * $odds =  ${(initialBetAmount * odds).roundToLong()}")
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
                    winMultiplier = winningsMultiplier,
                    winnings = winnings,
                    didWin = didWin,
                    odds = odds
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
            val amountWagered = Random.nextLong(from = 0, until = 99999999)
//            println("AmountWagered: $amountWagered")
            var betSide: String
            val randomNum = Random.nextInt(from = 1, until = 100)
            val betPercent: Double
            betSide = if (randomNum % 2 == 0) {
                betPercent = activeMarketData.value.getBearAndBullPercentages().first
                "Bear"
            } else {
                betPercent = activeMarketData.value.getBearAndBullPercentages().second
                "Bull"
            }
            val time = Date()
//            val newArrayList: ArrayList<OrderBookEntry>
            orderBookHolder.add(
                OrderBookEntry(
                    userName = userName,
                    amountWagered = amountWagered,
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
            delay(10000L)
            val bearTotal = Random.nextLong(0, 9999999999)
            val bullTotal = Random.nextLong(from = 0, until = 9999999999)
            val totalBears = Random.nextInt(from = 0, until = 9999999)
            val totalBulls = Random.nextInt(from = 0, until = 9999999)
            val biggestBearBet = Random.nextLong(from = 0, until = bearTotal)
            val biggestBullBet = Random.nextLong(from = 0, until = bullTotal)
            val randomData = ActiveMarket(
                betId = "123",
                ticker = "SPY",
                bearTotal = bearTotal,
                bullTotal = bullTotal,
                bearHeadCount = totalBears,
                bullHeadCount = totalBulls,
                biggestBearBet = biggestBearBet,
                biggestBullBet = biggestBullBet,
                marketStatus = "active"
            )
            activeMarketData.value = randomData
        }
    }

    fun navToDiffScreen(screen: NavBarItems) {
        _selectedNavItem.value = screen
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Utility.TIME_COUNTDOWN, 1) {
            override fun onTick(millisRemaining: Long) {
                val progressValue = millisRemaining.toFloat() / Utility.TIME_COUNTDOWN
                _countDownTime.value = progressValue.toString()
            }

            override fun onFinish() {
                _countDownTime.value = "00:00:00"
            }
        }.start()
    }

}


//    val liveUserAccountInformation = UserAccountInformation(
//        userId = "123",
//        userBalance = 1111111
//    )


//
//}
//
//
//
//val countDownFlow = flow {
//    val startingValue = 10
//    var currentValue = startingValue
//    emit(startingValue)
//    while (currentValue > 0) {
//        delay(1000L)
//        currentValue--
//        emit(currentValue)
//    }
//}
