package com.example.bearvbull.data

import java.util.*

data class OrderBookEntry(
    val userName : String = "123",
    val amountWagered: Long = 100,
    val betSide: String = "bear",
    val time: Date = Calendar.getInstance().time,
    val betPercent: Double = 50.0
)
