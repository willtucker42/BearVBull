package com.example.bearvbull.data

import java.util.Date

data class OrderBookEntry(
    val userName : String,
    val amountWagered: Double,
    val betSide: String,
    val time: Date,
    val betPercent: Double
)
