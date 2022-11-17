package com.example.bearvbull.data

import com.google.firebase.Timestamp

data class BetInformation(
    val tickerSymbol: String = "SPY",
    val initialBetAmount: Long = 500000,
    val betSide: String = "Bear",
    val winMultiplier: Any = 2.0,
    val userId: String = "123",
    val betStatus: String = "active", // Status can be either active, won, lost
    val winnings: Long = 123123123,
    val didWin: Boolean = true,
    val odds : Double = 1.2,
    val marketId: String = "SPY-10_2_2022",
    val timestamp: Timestamp = Timestamp(123123, 123123)
)