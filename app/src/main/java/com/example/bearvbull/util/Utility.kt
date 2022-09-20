package com.example.bearvbull.util

import com.example.bearvbull.R
import com.example.bearvbull.ui.theme.BearVBullTheme
import com.example.bearvbull.viewmodel.MainViewModel
import timber.log.Timber
import java.util.concurrent.TimeUnit

object Utility {

    //time to countdown - 1hr - 60secs
    const val TIME_COUNTDOWN: Long = 60000L
    private const val TIME_FORMAT = "%02d:%02d:%02d"


    //convert time to milli seconds
    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toMinutes(this),
        TimeUnit.MILLISECONDS.toSeconds(this) % 60,
        TimeUnit.MILLISECONDS.toMillis(this) % 10
    )

    fun getBetRatio(bearPercentage: Double, bullPercentage: Double, betSide: String): String {
        return buildString {
            when (betSide) {
                "BEAR" -> append((100 / bearPercentage)).append(COLON).append(ONE)
                "BULL" -> append((100 / bullPercentage)).append(COLON).append(ONE)
                else -> Timber.e("get_bet_ratio_error")
            }
        }
    }
}

enum class BetInfoType(icon: Int, createLabel: (viewModel: MainViewModel) -> String) {
    BET_TOTAL(R.drawable.coin_stack_icon, {
        it.getTotalWagered()
    }),
    RETURN_RATIO(R.drawable.trophy_icon, {
        "1:1"
    }),
    TOTAL_USERS(R.drawable.user_icon, {
        "1,112,900"
    }),
    BIGGEST_BET(R.drawable.money_bag_icon, {
        "87.68B"
    })
}


fun Int.formatBigNumber(): String {
    var i = 1
    var finalString = ""
    for (c in this.toString().reversed()) {
        i++
        if (i % 3 == 0) {
            finalString.plus(COMMA)
        }
        finalString.plus(c)
    }
    return finalString
}