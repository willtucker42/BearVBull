package com.example.bearvbull.data

import com.example.bearvbull.util.*
import timber.log.Timber

data class LiveBetInformation(
    val betId: String,
    val bearTotal: Double,
    val bullTotal: Double
) {
    fun getTotalWagered(): Double {
        return bearTotal + bullTotal
    }

    private fun getBearAndBullPercentages(): Pair<Double, Double> {
        val totalWagered = getTotalWagered()
        val bearPercent = bearTotal / totalWagered
        val bullPercent = bullTotal / totalWagered
        return Pair(bearPercent, bullPercent)
    }

    fun getReturnRatio(betSide: BetSide): String {
        return buildString {
            val bearAndBullPercentages = getBearAndBullPercentages()
            when (betSide.bearOrBull) {
                BEAR -> append((100 / bearAndBullPercentages.first).round(2)).append(COLON).append(ONE)
                BULL -> append((100 / bearAndBullPercentages.second).round(2)).append(COLON).append(ONE)
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
