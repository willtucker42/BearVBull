package com.example.bearvbull.interfaces

interface RandomInterface {

    companion object {
        const val something = "something"
    }

    fun printSomething() {
        println(something)
    }
}