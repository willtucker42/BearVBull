package com.example.bearvbull.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bearvbull.R
import com.example.bearvbull.data.ActiveMarket
import com.example.bearvbull.data.ActiveMarkets
import com.example.bearvbull.data.BetInformation
import com.example.bearvbull.data.OrderBookEntry
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.util.*
import com.example.bearvbull.util.Utility.formatTime
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.roundToLong
import kotlin.random.Random

class MainViewModel : ViewModel() {
    private val ORDERBOOK_QUERY_LIMIT: Long = 1000

    var participatingInMarketsMapHolder = mutableMapOf<String, BetInformation>()
    var _participatingInMarketsMapMutableFlow =
        MutableStateFlow(mutableMapOf<String, BetInformation>())
    val participatingInMarketsMapStateFlow: StateFlow<MutableMap<String, BetInformation>> =
        _participatingInMarketsMapMutableFlow

    var _betTxnStatus = MutableStateFlow(BetTransactionStatus.NOT_SENDING_BET)
    val betTxnStatus: StateFlow<BetTransactionStatus> = _betTxnStatus

    var _signInStatus = MutableStateFlow(SignInStatus.NOT_SIGNED_IN)
    val signInStatus: StateFlow<SignInStatus> = _signInStatus

    var _activeUser = MutableStateFlow(UserAccountInformation())
    val activeUser: StateFlow<UserAccountInformation> = _activeUser

    var _userId = MutableStateFlow("")
    val userId: StateFlow<String> = _userId

    // NavBar
    private val db: FirebaseFirestore = Firebase.firestore
    private var _selectedNavItem = MutableStateFlow(NavBarItems.BET_SCREEN)
    val selectedNavItem: StateFlow<NavBarItems> = _selectedNavItem

    // End nav bar
    private var countDownTimer: CountDownTimer? = null

    private var activeMarketsHolder: ActiveMarkets = ActiveMarkets()
    private var _activeMarkets = MutableStateFlow(ActiveMarkets())
    val activeMarkets: StateFlow<ActiveMarkets> = _activeMarkets

    private var _countDownTime = MutableStateFlow("")
    val countDownTime: StateFlow<String> = _countDownTime
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    // OrderBook
    private var orderBookIdHashMap: MutableMap<String, Boolean> =
        mutableMapOf("" to true)

    private var orderBookHolder: MutableList<OrderBookEntry> = mutableListOf(OrderBookEntry())
    private var _orderBookMutableStateFlow = MutableStateFlow<List<OrderBookEntry>>(listOf())
    val liveOrderBook: StateFlow<List<OrderBookEntry>> = _orderBookMutableStateFlow

    private var _orderBookLoadingState = MutableStateFlow(State.LOADING)
    val orderBookLoadingState: StateFlow<State> = _orderBookLoadingState
    // End OrderBook


    // BetData
    private var activeMarketData = MutableStateFlow(ActiveMarket())
    val liveMarketDataFlow: StateFlow<ActiveMarket> = activeMarketData
    // End bet data

    // ActiveMarket

    val fakeRankingsUserList = mutableListOf<UserAccountInformation>()
    val fakeUser = UserAccountInformation(
        userId = "123",
        userName = "willTucker42",
        70198817,
        R.drawable.green_wojak,
        1
    )
    val fakeBetHistory = mutableListOf<BetInformation>()
    var changingTicker = false
    fun manualInit() {
        println("MainViewModel init")
        getActiveMarkets()
        startTimer()
        generateRankingsUserList()
        generateBetHistory()
        viewModelScope.launch {
            updateUserInfo()
        }
    }

    private suspend fun getActiveMarketData() {
        while (true) {
            delay(1000)
            println("getting activeMarketData for ${activeMarketData.value.marketId}")
            if (activeMarketData.value.marketId != "") {
                db.collection("live_prediction_market_info")
                    .document(activeMarketData.value.marketId)
                    .get()
                    .addOnSuccessListener { marketDoc ->
                        if (!changingTicker) {
                            activeMarketData.value = ActiveMarket(
                                marketId = activeMarketData.value.marketId,
                                bearHeadCount = marketDoc.get("bear_headcount") as Long,
                                bearTotal = marketDoc.get("bear_total") as Long,
                                marketStatus = marketDoc.get("bet_status") as String,
                                biggestBearBet = marketDoc.get("biggest_bear_bet") as Long,
                                biggestBullBet = marketDoc.get("biggest_bull_bet") as Long,
                                bullHeadCount = marketDoc.get("bull_headcount") as Long,
                                bullTotal = marketDoc.get("bull_total") as Long,
                                ticker = marketDoc.get("ticker") as String
                            )
                        }
                    }
            }
        }
    }

    fun onSelectedTickerChanged(marketId: String) {
        if (changingTicker) {
            return
        }
        changingTicker = true
        println("onSelectedTickerChanged market id: $marketId")
        orderBookHolder.clear()
        orderBookIdHashMap.clear()
        _orderBookMutableStateFlow.value = orderBookHolder
        db.collection("live_prediction_market_info")
            .document(marketId)
            .get()
            .addOnSuccessListener { marketDoc ->
                activeMarketData.value = ActiveMarket(
                    marketId = marketId,
                    bearHeadCount = marketDoc.get("bear_headcount") as Long,
                    bearTotal = marketDoc.get("bear_total") as Long,
                    marketStatus = marketDoc.get("bet_status") as String,
                    biggestBearBet = marketDoc.get("biggest_bear_bet") as Long,
                    biggestBullBet = marketDoc.get("biggest_bull_bet") as Long,
                    bullHeadCount = marketDoc.get("bull_headcount") as Long,
                    bullTotal = marketDoc.get("bull_total") as Long,
                    ticker = marketDoc.get("ticker") as String
                )
                changingTicker = false
            }
            .addOnFailureListener { e ->
                println("Error in onSelectedTickerChanged $e")
                changingTicker = false
            }
        checkIfAlreadyParticipatingInMarket(marketId)
    }


    fun beginUserBetFlow(betInformation: BetInformation, contextForToast: Context) {
        if (betTxnStatus.value == BetTransactionStatus.SENDING_BET) {
            println("Bet transaction already taking place")
            return
        }
        _betTxnStatus.value = BetTransactionStatus.SENDING_BET
        println("Beginning User bet flow...")
        // first get the current user from database and check the balance
        /** User Bet flow starts... **/
        checkIfAlreadyParticipatingInMarket(betInformation, contextForToast)
    }

    private fun checkIfAlreadyParticipatingInMarket(betInformation: BetInformation, c: Context) {
        println("Checking if user is already participating in market...")
        db.collection("all_user_bets")
            .whereEqualTo("market_id", betInformation.marketId)
            .whereEqualTo("user_id", activeUser.value.userId)
            .get()
            .addOnSuccessListener { marketsDoc ->
                if (marketsDoc.size() > 0) {
                    marketsDoc.forEach { doc ->
                        addMarketToMap(betInformation.marketId, doc)
                    }
                    // already participating
                    Toast.makeText(c, "You have already bet in this market", Toast.LENGTH_LONG)
                        .show()
                    println("Already participating in market ${betInformation.marketId}")
//                    checkIfSufficientFunds(betInformation, c) // REMOVE THIS LATER
                    _betTxnStatus.value = BetTransactionStatus.NOT_SENDING_BET
                    // ADD ABOVE LINE LATER
                } else {
                    /** User Bet flow continues... **/
                    checkIfSufficientFunds(betInformation, c)
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel.kt", "Error checking if in market $e")
                _betTxnStatus.value = BetTransactionStatus.NOT_SENDING_BET
            }
    }

    private fun checkIfAlreadyParticipatingInMarket(marketId: String) {
        db.collection("all_user_bets")
            .whereEqualTo("market_id", marketId)
            .whereEqualTo("user_id", activeUser.value.userId)
            .get()
            .addOnSuccessListener { docs ->
                if (docs.size() > 0) {
                    docs.forEach { doc ->
                        addMarketToMap(marketId, doc)
                    }
                    println("Already participating in the market $marketId")
                } else {
                    println("Not participating in market $marketId")
                }

            }
            .addOnFailureListener { e ->
                Log.e("checkIfAlreadyParticipatingInMarket(marketId: String)", "Error $e")
            }
    }

    private fun addMarketToMap(marketId: String, doc: DocumentSnapshot) {
        participatingInMarketsMapHolder[marketId] = BetInformation(
            tickerSymbol = doc.get("ticker_symbol") as String,
            initialBetAmount = doc.get("bet_amount") as Long,
            betSide = doc.get("bet_side") as String,
            winMultiplier = doc.get("win_multiplier") as Double,
            userId = doc.get("user_id") as String,
            betStatus = doc.get("bet_status") as String,
            didWin = doc.get("did_win") as Boolean,
            odds = doc.get("odds") as Double,
            marketId = doc.get("market_id") as String,
            timestamp = doc.get("timestamp") as Timestamp
        )
        _participatingInMarketsMapMutableFlow.value = participatingInMarketsMapHolder
        println("Participating in markets keys: ${participatingInMarketsMapStateFlow.value.keys}")
    }

    private fun checkIfSufficientFunds(betInformation: BetInformation, c: Context) {
//        println("ATTEMPTING TO CHECK SUFFICIENT FUNDS... on user ${_activeUser.value.email}")
        db.collection("users")
            .document(_activeUser.value.email)
            .get()
            .addOnSuccessListener { doc ->
                println(
                    "Checking if user has sufficient funds... ${activeUser.value.email} balance: " +
                            "${doc.get("balance_available") as Long}"
                )
                println(doc)
                if (doc.get("balance_available") as Long >= betInformation.initialBetAmount) {
                    /** User Bet flow continues... **/
                    addUserBet(betInformation, c)
                } else {
                    Toast.makeText(c, "Insufficient funds", Toast.LENGTH_LONG)
                        .show()
                    println("Insufficient funds")
                    _betTxnStatus.value = BetTransactionStatus.NOT_SENDING_BET
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel.kt", "Error getting funds info $e")
                _betTxnStatus.value = BetTransactionStatus.NOT_SENDING_BET
            }
    }

    private fun addUserBet(betInformation: BetInformation, c: Context) {
        println("Adding user bet... $betInformation")
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
            "did_win" to betInformation.didWin,
            "bet_id" to UUID.randomUUID()
        )

        /** User Bet flow ends... **/
        db.collection("all_user_bets")
            .add(userBet)
            .addOnSuccessListener { docRef ->

                Log.i("MainViewModel.kt", "addUserBet DocumentSnapshot added with ID: ${docRef.id}")
                Toast.makeText(c, "Bet sent. Good luck!", Toast.LENGTH_LONG).show()
                updateUserBalance(-betInformation.initialBetAmount)
            }
            .addOnFailureListener { e ->
                Log.i("MainViewModel.kt", "Error adding document, $e")
                Toast.makeText(c, "Bet failed to send. Funds are safe.", Toast.LENGTH_LONG)
                    .show()
                _betTxnStatus.value = BetTransactionStatus.NOT_SENDING_BET
            }
    }

    /**
     * Updates the balance in the database and locally
     * @param amount can be negative or positive
     */
    private fun updateUserBalance(amount: Long) {
        println("Updating user balance... $amount")
        db.collection("users")
            .document(activeUser.value.email)
            .update("balance_available", FieldValue.increment(amount))
            .addOnSuccessListener {
                println("updateUserBalance success. ")
                _activeUser.value = UserAccountInformation(
                    userId = _activeUser.value.userId,
                    userName = _activeUser.value.userName,
                    userBalance = _activeUser.value.userBalance.plus(amount),
                    profileImage = _activeUser.value.profileImage,
                    rank = _activeUser.value.rank,
                    email = _activeUser.value.email
                )
                _betTxnStatus.value = BetTransactionStatus.NOT_SENDING_BET
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel", "updateUserBalance() failed with error: $e")
                _betTxnStatus.value = BetTransactionStatus.NOT_SENDING_BET
            }
    }

    private fun getActiveMarkets() {
//        println("in getActiveMarkets")
        db.collection("live_prediction_market_info")
            .whereEqualTo("bet_status", "waiting")
            .get()
            .addOnSuccessListener { mDoc ->
                /** This for loop adds the markets to the list of markets for the dropdown */
                mDoc.forEach { doc ->
//                    println("thedoc ${doc.id} => ${doc.data}")
                    activeMarketsHolder.activeMarkets.add(
                        ActiveMarket(
                            marketId = doc.id,
                            bearHeadCount = doc.get("bear_headcount") as Long,
                            bearTotal = doc.get("bear_total") as Long,
                            marketStatus = doc.get("bet_status") as String,
                            biggestBearBet = doc.get("biggest_bear_bet") as Long,
                            biggestBullBet = doc.get("biggest_bull_bet") as Long,
                            bullHeadCount = doc.get("bull_headcount") as Long,
                            bullTotal = doc.get("bull_total") as Long,
                            ticker = doc.get("ticker") as String
                        )
                    )
                }
                _activeMarkets.value = activeMarketsHolder
                if (!mDoc.isEmpty) {
                    mDoc.last().let {
                        activeMarketData.value = ActiveMarket(
                            marketId = it.id,
                            bearHeadCount = it.get("bear_headcount") as Long,
                            bearTotal = it.get("bear_total") as Long,
                            marketStatus = it.get("bet_status") as String,
                            biggestBearBet = it.get("biggest_bear_bet") as Long,
                            biggestBullBet = it.get("biggest_bull_bet") as Long,
                            bullHeadCount = it.get("bull_headcount") as Long,
                            bullTotal = it.get("bull_total") as Long,
                            ticker = it.get("ticker") as String
                        )
                    }
                }
                println("Launching")
                viewModelScope.launch {
                    launch { getActiveMarketData() }
                    launch { getMarketBookData() }
                }
            }
            .addOnFailureListener { e ->
                println("Error getting documents. $e")
            }

    }

    @SuppressLint("LogNotTimber")
    private suspend fun getMarketBookData() {
        while (!changingTicker) {
//            println("getMarketBookData1, ticker: ${liveMarketDataFlow.value.ticker}")
            println("getting marketbook data")
            delay(1000)
            db.collection("all_user_bets")
                .whereEqualTo("bet_status", "active")
//                .whereEqualTo("market_id", liveMarketDataFlow.value.marketId)
                .limit(ORDERBOOK_QUERY_LIMIT)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    if (!changingTicker) {
//                        println("order book result size is ${result.size()} for ticker: ${liveMarketDataFlow.value.ticker}")
                        result.forEach { tradeDoc ->
                            // Only add the bet if it's not already in the bet list
                            if (!orderBookIdHashMap.containsKey(
                                    tradeDoc.get("bet_id").toString()
                                )
                            ) {
                                orderBookIdHashMap[tradeDoc.get("bet_id").toString()] = true
                                orderBookHolder.add(
                                    OrderBookEntry(
                                        userName = "willTucker42",
                                        amountWagered = tradeDoc.get("bet_amount") as Long,
                                        betSide = tradeDoc.get("bet_side") as String,
                                        time = (tradeDoc.get("timestamp") as Timestamp).toDate(),
                                        betPercent = tradeDoc.get("odds") as Double,
                                        ticker = tradeDoc.get("ticker_symbol") as String
                                    )
                                )
                            }
                        }
                        _orderBookMutableStateFlow.value = orderBookHolder
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MainViewModel.kt", "Error getting trade history: $e")
                }
        }
    }

    private fun generateBetHistory() {
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

    private fun generateRankingsUserList() {
        println("generateRankingsUSerList")
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


    fun navToDiffScreen(screen: NavBarItems) {
        _selectedNavItem.value = screen
    }

    fun checkIfUserExistsInDb(account: GoogleSignInAccount) {
        updateSignInStatus(SignInStatus.CHECKING_USER)
        db.collection("users")
            .whereEqualTo("user_id", account.id.toString())
            .get()
            .addOnSuccessListener { doc ->
                println("the user doc.size ${doc.size()}")
                if (doc.size() > 0) {
                    println("User found. Signing in... ${account.email.toString()}")
                    updateUserFromDbDoc(doc.first())
                    updateSignInStatus(SignInStatus.SIGNED_IN)
//                    addNewUserToDb(account) // remove this later
                } else {
                    addNewUserToDb(account)
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel.kt", "Error getting User: $e")
            }
    }

    private fun addNewUserToDb(account: GoogleSignInAccount) {
        _signInStatus.value = SignInStatus.ADDING_NEW_USER
        println("User not found... adding new user... ${account.email}")
        val user = hashMapOf(
            "balance_available" to 1000000,
            "elo_score" to 100,
            "email" to account.email,
            "user_id" to account.id.toString(),
            "username" to account.displayName,

            )
        db.collection("users")
            .document(account.email.toString())
            .set(user)
            .addOnSuccessListener { docRef ->
                Log.i("MainViewModel.kt", "Added user id $docRef")
                _activeUser.value = UserAccountInformation(
                    userId = account.id.toString(),
                    userName = account.displayName.toString(),
                    userBalance = 1000000,
                    profileImage = _activeUser.value.profileImage,
                    rank = _activeUser.value.rank,
                    email = account.email.toString(),
                    eloScore = 100
                )
                _signInStatus.value = SignInStatus.SIGNED_IN
            }
            .addOnFailureListener { e ->
                _signInStatus.value = SignInStatus.NOT_SIGNED_IN
                Log.e("MainViewModel.kt", "Error adding user, $e")
            }
    }

    private fun updateSignInStatus(value: SignInStatus) {
        _signInStatus.value = value
    }

    private fun updateUserId(id: String) {
        _userId.value = id
    }

    private suspend fun updateUserInfo() {
        while (signInStatus.value == SignInStatus.SIGNED_IN) {
            delay(5000)
            println("Updating user info...")
            db.collection("users")
                .whereEqualTo("user_id", activeUser.value.userId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.size() > 0) {
                        updateUserFromDbDoc(doc.first())
                        println(doc.first())
                    } else {
                        Log.e("MainViewModel", "Really unexpected error")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("MainViewModel.kt", "Error updating user info: $e")
                }
        }
    }

    private fun updateUserFromDbDoc(doc: DocumentSnapshot) {
        _activeUser.value = UserAccountInformation(
            userId = doc.get("user_id") as String,
            userName = doc.get("username") as String,
            userBalance = doc.get("balance_available") as Long,
            profileImage = _activeUser.value.profileImage,
            rank = _activeUser.value.rank,
            email = doc.get("email") as String,
            eloScore = (doc.get("elo_score") as Long).toInt()
        )
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(getTimeUntilPredictionMarketClose(), 1) {
            override fun onTick(millisRemaining: Long) {
//                val progressValue = millisRemaining.toFloat() / Utility.TIME_COUNTDOWN
                _countDownTime.value = millisRemaining.formatTime()
            }

            override fun onFinish() {
                _countDownTime.value = "00:00:00"
            }
        }.start()
    }

//    fun onTapSignInWithGoogle() = flow {
//        try {
//            emit()
//        } catch (e: Exception) {
//
//        }
//    }

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
