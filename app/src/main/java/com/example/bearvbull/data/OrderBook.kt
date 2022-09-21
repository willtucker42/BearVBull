package com.example.bearvbull.data

data class OrderBook(
    val orderBook: MutableList<OrderBookEntry> = mutableListOf()
)
