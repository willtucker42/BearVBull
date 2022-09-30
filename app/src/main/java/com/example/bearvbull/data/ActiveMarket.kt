package com.example.bearvbull.data

import com.google.firebase.Timestamp

data class ActiveMarket(
    val betId: String = "123",
    val bearTotal: Int = 0,
    val ticker: String = "SPY",
    val betStatus: String = "live",
    val endTime: Timestamp = Timestamp(123412342134, 123421342),
    val bearHeadCount: Int = 0,
    val marketId: String = "SPY-9_28_2022",
    val biggestBullBet: Int = 10,
    val biggestBearBet: Int = 10,
    val bullTotal: Int = 50000
)

data class ActiveMarkets(
    val activeMarkets: MutableList<ActiveMarket> = mutableListOf()
)
