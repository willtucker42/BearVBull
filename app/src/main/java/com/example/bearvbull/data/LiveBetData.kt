package com.example.bearvbull.data

import android.util.Log
import com.example.bearvbull.util.*
import timber.log.Timber

data class LiveBetData(
    val betId: String,
    val bearTotal: Double,
    val bullTotal: Double,
    val biggestBearBet: Double,
    val biggestBullBet: Double,
    val totalBears: Int,
    val totalBulls: Int,
    val grandTotal: Double = bearTotal + bullTotal
) {
    fun getBearAndBullPercentages(): Pair<Double, Double> {
        val totalWagered = grandTotal
        val bearPercent = (bearTotal / totalWagered) * 100
        val bullPercent = (bullTotal / totalWagered) * 100
        Log.i("asdasdsda", "bearPercent: $bearPercent, bullPercent: $bullPercent")
        return Pair(bearPercent.round(2), bullPercent.round(2))
    }

    fun getReturnRatio(betSide: BetSide): String {
        return buildString {
            val bearAndBullPercentages = getBearAndBullPercentages()
            when (betSide.bearOrBull) {
                BEAR -> append((100 / bearAndBullPercentages.first).round(2)).append(COLON)
                    .append(ONE)
                BULL -> append((100 / bearAndBullPercentages.second).round(2)).append(COLON)
                    .append(ONE)
                else -> Timber.e("get_bet_ratio_error")
            }
        }
    }
    fun createBetInfoLabel(betInfoType: BetInfoType, betSide: BetSide): String {
        return when (betInfoType) {
            BetInfoType.TOTAL_WAGERED -> getTotalWageredForBetSide(betSide).getFormattedNumber()
            BetInfoType.RETURN_RATIO -> getReturnRatio(betSide)
            BetInfoType.TOTAL_USERS -> "%,d".format(getTotalUsersWageringOnBet(betSide))
            BetInfoType.BIGGEST_BET -> getBiggestBet(betSide).getFormattedNumber()
        }
    }
    private fun getTotalWageredForBetSide(betSide: BetSide): Double {
        return when (betSide) {
            BetSide.BEAR -> bearTotal
            BetSide.BULL -> bullTotal
        }
    }

    private fun getTotalUsersWageringOnBet(betSide: BetSide): Int {
        return when (betSide) {
            BetSide.BEAR -> totalBears
            BetSide.BULL -> totalBulls
        }
    }

    private fun getBiggestBet(betSide: BetSide): Double {
        return when (betSide) {
            BetSide.BEAR -> biggestBearBet
            BetSide.BULL -> biggestBullBet
        }
    }


//    fun getReturnRatio(betSide: BetSide): String {
//        return {
//            val bearAndBullPercentages = getBearAndBullPercentages()
//            getReturnRatio(
//                bearPercentage = bearAndBullPercentages.first,
//                bullPercentage = bearAndBullPercentages.second
//            )
//        }
//    }
}
