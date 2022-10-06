package com.example.bearvbull.data

import com.example.bearvbull.util.*
import timber.log.Timber

data class LivePredictionMarketData(
    val betId: String,
    val bearTotal: Double,
    val bullTotal: Double,
    val biggestBearBet: Double,
    val biggestBullBet: Double,
    val totalBears: Int,
    val totalBulls: Int,
    val betStatus: String = "active", // live, closed, waiting
    val grandTotal: Double = bearTotal + bullTotal
) {
    private fun getBearAndBullPercentages(): Pair<Double, Double> {
        val totalWagered = grandTotal
        val bearPercent = (bearTotal / totalWagered) * 100
        val bullPercent = (bullTotal / totalWagered) * 100
        return Pair(bearPercent.round(), bullPercent.round())
    }

    private fun getReturnRatio(betSide: BetSide): String {
        return buildString {
            val bearAndBullPercentages = getBearAndBullPercentages()
            when (betSide.bearOrBull) {
                BEAR -> append("$ONE$COLON").append((100 / bearAndBullPercentages.first).round())
                BULL -> append("$ONE$COLON").append((100 / bearAndBullPercentages.second).round())
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
