package com.example.bearvbull.data.users

data class UserAccountInformation(
    val userId : String,
    val userName: String,
    val userBalance: Long,
    val profileImage: Int,
    val rank: Int
)
