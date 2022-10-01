package com.example.bearvbull.util

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Color
import com.example.bearvbull.R
import com.example.bearvbull.ui.theme.PodiumBronze
import com.example.bearvbull.ui.theme.PodiumGold
import com.example.bearvbull.ui.theme.PodiumSilver
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import kotlin.math.ln
import kotlin.math.pow

object Utility {

    //time to countdown - 1hr - 60secs
    const val TIME_COUNTDOWN: Long = 60000L
    private const val TIME_FORMAT = "%02d:%02d"
    private const val ORDER_BOOK_ENTRY_TIME_FORMAT = "HH:mm:ss.SS"

    @SuppressLint("SimpleDateFormat")
    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat(ORDER_BOOK_ENTRY_TIME_FORMAT)
    val UP_ARROW = R.drawable.arrow_up
    val DOWN_ARROW = R.drawable.arrow_down


    //convert time to milli seconds
    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) % 60
    )


}

enum class BetSide(val bearOrBull: String) {
    BEAR(com.example.bearvbull.util.BEAR), BULL(com.example.bearvbull.util.BULL)
}

enum class BetInfoType(val icon: Int) {
    TOTAL_WAGERED(R.drawable.coin_stack_icon),
    RETURN_RATIO(R.drawable.trophy_icon),
    TOTAL_USERS(R.drawable.user_group_icon),
    BIGGEST_BET(R.drawable.money_bag_icon)
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
    df.roundingMode = RoundingMode.DOWN

    return df.format(this).toDouble()
}

//val glowingAnimationKeyFrames = KeyframesSpec.KeyframesSpecConfig(keyframes<> {  })