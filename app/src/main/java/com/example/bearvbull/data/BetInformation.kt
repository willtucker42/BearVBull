package com.example.bearvbull.data

import com.example.bearvbull.util.BetSide

data class BetInformation(
    val tickerSymbol: String = "SPY",
    val initialBet: Long = 500000,
    val betSide: String = "Bear",
    val odds: Double = 50.00,
    val winnings: Long = 1000000
)