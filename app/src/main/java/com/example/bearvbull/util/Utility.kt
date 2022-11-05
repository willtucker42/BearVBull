package com.example.bearvbull.util

import android.annotation.SuppressLint
import androidx.compose.ui.graphics.Color
import com.example.bearvbull.R
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.*
import java.math.RoundingMode
import java.sql.Timestamp
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.math.pow

object Utility {

    // time to countdown - 1hr - 60secs
    const val TIME_COUNTDOWN: Long = 60000L
    private const val TIME_FORMAT = "%02d:%02d:%02d"
    private const val ORDER_BOOK_ENTRY_TIME_FORMAT = "HH:mm:ss.SS"

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(ORDER_BOOK_ENTRY_TIME_FORMAT)
    const val UP_ARROW = R.drawable.arrow_up
    const val DOWN_ARROW = R.drawable.arrow_down


    //convert time to milli seconds
    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )


}

val TIMEZONE_ET: ZoneId = ZoneId.of("America/New_York")

enum class BetSide(val bearOrBull: String) {
    BEAR(com.example.bearvbull.util.BEAR), BULL(com.example.bearvbull.util.BULL)
}

enum class BetTransactionStatus {
    NOT_SENDING_BET, SENDING_BET
}

enum class SignInStatus {
    NOT_SIGNED_IN, CHECKING_USER, SIGNED_IN, ADDING_NEW_USER
}

enum class BetInfoType(val icon: Int) {
    TOTAL_WAGERED(R.drawable.coin_stack_icon),
    RETURN_RATIO(R.drawable.bet_return_icon),
    TOTAL_USERS(R.drawable.user_group_icon),
    BIGGEST_BET(R.drawable.money_bag_icon)
}

enum class EloRank(val starIcon: Int, val color: Color, amountOfStars: Int, title: String) {
    CRAMER(R.drawable.low_rank_star, PodiumBronze,1, "Cramer"),
    SCRUB(R.drawable.low_rank_star, PodiumBronze,1, "Scrub"),
    AMATEUR(R.drawable.low_rank_star, PodiumBronze, 2, "Amateur"),
    STOCK_JUNKIE(R.drawable.low_rank_star, PodiumBronze, 2, "Stock Junkie"),
    TRADER(R.drawable.low_rank_star, PodiumBronze, 3, "Trader"),
    ANALYST(R.drawable.low_rank_star, PodiumSilver,1, "Analyst"),
    SENIOR_ANALYST(R.drawable.low_rank_star, PodiumSilver,2, "Sr. Analyst"),
    BROKER(R.drawable.low_rank_star, PodiumSilver,3, "Broker"),
    SENIOR_BROKER(R.drawable.low_rank_star, PodiumGold,1, "Sr. Broker"),
    INSIDER(R.drawable.low_rank_star, PodiumGold, 2,"Insider"),
    FINANCIER(R.drawable.low_rank_star, PodiumGold, 2,"Financier"),
    VENTURE_CAPITALIST(R.drawable.low_rank_star, PodiumGold, 3,"Venture Capitalist"),
    CEO(R.drawable.low_rank_star, PodiumGold, 3,"C.E.O"),
    PRESIDENT(R.drawable.low_rank_star, PodiumGold, 3,"Financier"),
    HEDGE_FUND_MANAGER(R.drawable.low_rank_star, GreenRank,1, "Hedge Fund Manager"),
    PROFITEER(R.drawable.low_rank_star, GreenRank, 2,"Profiteer"),
    DARK_MONEY_MANAGER(R.drawable.high_rank_star, GreenRank, 3, "Dark Money Manager"),
    LORD_PRESIDENT(R.drawable.high_rank_star, GrandMasterRank,4,"Lord President"),
    PROPHET(R.drawable.high_rank_star, GrandMasterRank, 1, "Prophet")
}

fun getEloRank(eloScore: Int): EloRank {
    return when (eloScore) {
        in 0..110 -> EloRank.BRONZE
        in 111..145 -> EloRank.SILVER
        in 146..190 -> EloRank.GOLD
        in 191..235 -> EloRank.PLATINUM
        in 236..280 -> EloRank.DIAMOND
        in 281..325 -> EloRank.MASTER
        else -> EloRank.GRAND_MASTER
    }
}


val betInfoTypeList = listOf(
    BetInfoType.TOTAL_WAGERED,
    BetInfoType.RETURN_RATIO,
    BetInfoType.TOTAL_USERS,
    BetInfoType.BIGGEST_BET,
)

enum class NavBarItems(val icon: Int, val title: String) {
    BET_SCREEN(R.drawable.bet_chips_icon, "Home"),
    RANKINGS_SCREEN(R.drawable.trophy_icon_2, "Rankings"),
    PROFILE_SCREEN(R.drawable.profile_icon, "Profile")
}

enum class PodiumRanks(val rank: String, val color: Color) {
    SECOND("Second", PodiumSilver),
    FIRST("First", PodiumGold),
    THIRD("Third", PodiumBronze)
}

val navBarItemList = listOf(
    NavBarItems.BET_SCREEN,
    NavBarItems.RANKINGS_SCREEN,
    NavBarItems.PROFILE_SCREEN
)

val testProfilePics = listOf(
    R.drawable.chadjack,
    R.drawable.green_wojak,
    R.drawable.ben_shapiro
)

fun Double.getFormattedNumber(): String {
    if (this < 1000) return "" + this
    val exp = (ln(this) / ln(1000.0)).toInt()
    return String.format("%.1f%c", this / 1000.0.pow(exp.toDouble()), "kMGTPE"[exp - 1])
}

fun getTimeUntilPredictionMarketClose(): Long {
    val cal = GregorianCalendar.getInstance()
    val ldt = LocalDateTime.of(
        cal.get(GregorianCalendar.YEAR),
        cal.get(GregorianCalendar.MONTH) + 1,
        cal.get(GregorianCalendar.DAY_OF_MONTH),
        23,
        59,
        0
    )
    val marketEndTime = Timestamp.from(
        ldt.toInstant(
            TIMEZONE_ET.rules.getOffset(Instant.now())
        )
    )
    val curTime = Calendar.getInstance().time

    return marketEndTime.time - curTime.time

//    println("future time $marketEndTime")
//    println("New Timestamp ${marketEndTime.time.milliseconds.inWholeMilliseconds}")
//    println("Now Timestamp ${System.currentTimeMillis()}")
//    println("Time millis diff ${marketEndTime.time.milliseconds.inWholeMilliseconds - System.currentTimeMillis()} \n\n")
//    println("future time $marketEndTime ")
//    println("The current time is $curTime")
    //    val seconds = dif / 1000
//    val minutes = seconds / 60
//    val hours = minutes / 60
//    val days = hours / 24
//
//    println("formatted: ${dif.formatTime()}")
//    val closeTimestamp = Timestamp.valueOf(marketCloseDateTime.toString())
//    println("closeTimestamp $closeTimestamp")
//    println("The timestamp ${closeTimestamp - System.currentTimeMillis()}")
//    return closeTimestamp - System.currentTimeMillis()
}

fun Double.formatBigNumberWithCommas(): String {
//    Timber.i()
    val theInt = toString().substringBefore(PERIOD)
    val remainderAfterDecimal = toString().substringAfter(PERIOD)
    val postDecimalDigits = EMPTY_STRING
    if (remainderAfterDecimal.isNotEmpty())
        postDecimalDigits.plus(remainderAfterDecimal)

    return theInt.reversed()
        .chunked(3)
        .joinToString(COMMA)
        .reversed()
        .plus(postDecimalDigits)
}

fun Long.formatBigLong(): String {
    return this.toString().reversed()
        .chunked(3)
        .joinToString(COMMA)
        .reversed()
}

fun Double.round(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.UP
    val formattedVal: Double = try {
        df.format(this).toDouble()
    } catch (e: Exception) {
        0.0
    }
    return formattedVal
}

enum class State {
    LOADING, LOADED
}


//private suspend fun generateAndUpdateLiveBetDataData() {
//    while (true) {
//        delay(10000L)
//        val bearTotal = Random.nextLong(0, 9999999999)
//        val bullTotal = Random.nextLong(from = 0, until = 9999999999)
//        val totalBears = Random.nextLong(from = 0, until = 9999999)
//        val totalBulls = Random.nextLong(from = 0, until = 9999999)
//        val biggestBearBet = Random.nextLong(from = 0, until = bearTotal)
//        val biggestBullBet = Random.nextLong(from = 0, until = bullTotal)
//        val randomData = ActiveMarket(
//            marketId = "123",
//            ticker = "SPY",
//            bearTotal = bearTotal,
//            bullTotal = bullTotal,
//            bearHeadCount = totalBears,
//            bullHeadCount = totalBulls,
//            biggestBearBet = biggestBearBet,
//            biggestBullBet = biggestBullBet,
//            marketStatus = "active"
//        )
//        activeMarketData.value = randomData
//    }
//}
//
//private suspend fun generateOrderBookEntries() {
//    while (true) {
//        delay(Random.nextLong(300, 1000))
//        val userName = (1..10)
//            .map { Random.nextInt(0, charPool.size) }
//            .map(charPool::get)
//            .joinToString("")
//        val amountWagered = Random.nextLong(from = 0, until = 99999999)
////            println("AmountWagered: $amountWagered")
//        var betSide: String
//        val randomNum = Random.nextInt(from = 1, until = 100)
//        val betPercent: Double
//        betSide = if (randomNum % 2 == 0) {
//            betPercent = activeMarketData.value.getBearAndBullPercentages().first
//            "Bear"
//        } else {
//            betPercent = activeMarketData.value.getBearAndBullPercentages().second
//            "Bull"
//        }
//        val time = Date()
////            val newArrayList: ArrayList<OrderBookEntry>
//        orderBookHolder.add(
//            OrderBookEntry(
//                userName = userName,
//                amountWagered = amountWagered,
//                betSide = betSide,
//                betPercent = betPercent,
//                time = time
//            )
//        )
//        _orderBookMutableStateFlow.value = orderBookHolder
//    }
//}

//val glowingAnimationKeyFrames = KeyframesSpec.KeyframesSpecConfig(keyframes<> {  })