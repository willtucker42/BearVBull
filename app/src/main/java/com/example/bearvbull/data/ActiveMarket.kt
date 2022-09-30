package com.example.bearvbull.data

data class ActiveMarket(
    val ticker: String = "SPY"
)

data class ActiveMarkets(
    val activeMarkets: MutableList<ActiveMarket> = mutableListOf()
)
