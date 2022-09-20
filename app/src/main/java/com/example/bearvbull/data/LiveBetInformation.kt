package com.example.bearvbull.data

import android.util.Log
import com.example.bearvbull.util.*
import timber.log.Timber

data class LiveBetInformation(
    val betId: String,
    val bearTotal: Double,
    val bullTotal: Double,
    val biggestBearBet: Double,
    val biggestBullBet: Double,
    val totalBears: Int,
    val totalBulls: Int
) {
    private fun getTotalWagered(): Double {
        return bearTotal + bullTotal
    }

    fun getBearAndBullPercentages(): Pair<Double, Double> {
        val totalWagered = getTotalWagered()
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
