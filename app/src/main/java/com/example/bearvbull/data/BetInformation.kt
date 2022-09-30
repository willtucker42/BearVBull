package com.example.bearvbull.data

data class BetInformation(
    val tickerSymbol: String = "SPY",
    val initialBetAmount: Long = 500000,
    val betSide: String = "Bear",
    val odds: Double = 50.00,
    val winningsMultiplier: Double = 2.0,
    val winnings: Long = 1000000,
    val userId: String = "123",
    val betStatus: String = "active", // Status can be either active, won, lost
    val didWin: Boolean = true
)

data class UserBetHistory(
    val betHistoryList: List<BetInformation>
)