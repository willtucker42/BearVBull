package com.example.bearvbull.data

import com.example.bearvbull.util.round
import com.google.firebase.Timestamp
import com.example.bearvbull.util.*
import timber.log.Timber

data class ActiveMarket(
    val betId: String = "123",
    val bearTotal: Long = 1,
    val ticker: String = "SPY",
    val marketStatus: String = "active",
    val endTime: Timestamp = Timestamp(123412342134, 123421342),
    val bearHeadCount: Int = 0,
    val bullHeadCount: Int = 0,
    val marketId: String = "SPY-9_28_2022",
    val biggestBullBet: Long = 10,
    val biggestBearBet: Long = 10,
    val bullTotal: Long = 50000
) {
    fun getBearAndBullPercentages(): Pair<Double, Double> {
        val totalWagered = bearTotal + bullTotal
        val bearPercent = (bearTotal.toDouble() / totalWagered.toDouble()) * 100
        val bullPercent = (bullTotal.toDouble() / totalWagered.toDouble()) * 100
        return Pair(bearPercent.round(), bullPercent.round())
    }

    fun createBetInfoLabel(betInfoType: BetInfoType, betSide: BetSide): String {
        return when (betInfoType) {
            BetInfoType.TOTAL_WAGERED -> getTotalWageredForBetSide(betSide).formatBigLong()
            BetInfoType.RETURN_RATIO -> getReturnRatio(betSide)
            BetInfoType.TOTAL_USERS -> "%,d".format(getTotalUsersWageringOnBet(betSide))
            BetInfoType.BIGGEST_BET -> getBiggestBet(betSide).formatBigLong()
        }
    }

    private fun getTotalWageredForBetSide(betSide: BetSide): Long {
        return when (betSide) {
            BetSide.BEAR -> bearTotal
            BetSide.BULL -> bullTotal
        }
    }

    private fun getTotalUsersWageringOnBet(betSide: BetSide): Int {
        return when (betSide) {
            BetSide.BEAR -> bearHeadCount
            BetSide.BULL -> bullHeadCount
        }
    }

    private fun getBiggestBet(betSide: BetSide): Long {
        return when (betSide) {
            BetSide.BEAR -> biggestBearBet
            BetSide.BULL -> biggestBullBet
        }
    }

     fun getReturnRatio(betSide: BetSide): String {
        return buildString {
            val bearAndBullPercentages = getBearAndBullPercentages()
            when (betSide.bearOrBull) {
                BEAR -> append("$ONE$COLON").append((100 / bearAndBullPercentages.first).round())
                BULL -> append("$ONE$COLON").append((100 / bearAndBullPercentages.second).round())
                else -> Timber.e("get_bet_ratio_error")
            }
        }
    }
}

data class ActiveMarkets(
    val activeMarkets: MutableList<ActiveMarket> = mutableListOf(ActiveMarket())
)
