package com.example.bearvbull.util

import android.annotation.SuppressLint
import com.example.bearvbull.R
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
    TOTAL_USERS(R.drawable.user_icon),
    BIGGEST_BET(R.drawable.money_bag_icon)
}

val betInfoTypeList = listOf(
    BetInfoType.TOTAL_WAGERED,
    BetInfoType.RETURN_RATIO,
    BetInfoType.TOTAL_USERS,
    BetInfoType.BIGGEST_BET,
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



fun Double.round(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.DOWN

    return df.format(this).toDouble()
}