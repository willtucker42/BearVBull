package com.example.bearvbull.data.users

data class UserAccountInformation(
    val userId : String = "",
    val userName: String = "",
    val userBalance: Long = 0,
    val profileImage: Int = 0,
    val rank: Int = 0,
    val email: String = "",
    val eloScore: Int = 100
)
