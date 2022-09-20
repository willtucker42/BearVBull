package com.example.bearvbull.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bearvbull.data.LiveBetInformation
import com.example.bearvbull.util.*
import com.example.bearvbull.util.Utility.formatTime
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private var _countDownTime = MutableStateFlow("")
    val countDownTime: StateFlow<String> = _countDownTime


    val liveBetInformationTestData = LiveBetInformation(
        betId = "abc123",
        bearTotal = 89763.12,
        bullTotal = 189699.76,
        totalBears = 3783,
        totalBulls = 10075,
        biggestBearBet = 50000.00,
        biggestBullBet = 103098.00
    )

    val countDownFlow = flow {
        val startingValue = 10
        var currentValue = startingValue
        emit(startingValue)
        while (currentValue > 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    init {
        startTimer()
//        collectTimerFlow()
    }

//    private fun collectTimerFlow() {
//        viewModelScope.launch {
//            countDownTime.collectLatest {
////                println(it)
//            }
//        }
//    }

//    fun handleCountDownTimer() {
//        if (isPlaying.value == true) {
//            _celebrate.postValue(false)
//        } else {
//            startTimer()
//        }
//    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Utility.TIME_COUNTDOWN, 1) {
            override fun onTick(millisRemaining: Long) {
                val progressValue = millisRemaining.toFloat() / Utility.TIME_COUNTDOWN
                _countDownTime.value = millisRemaining.formatTime()
            }

            override fun onFinish() {
                _countDownTime.value = 0.toString()
            }
        }.start()
    }

    private fun getTotalWageredForBetSide(betSide: BetSide): Double {
        return when (betSide) {
            BetSide.BEAR -> liveBetInformationTestData.bearTotal
            BetSide.BULL -> liveBetInformationTestData.bullTotal
        }
    }

    private fun getTotalUsersWageringOnBet(betSide: BetSide): Int {
        return when (betSide) {
            BetSide.BEAR -> liveBetInformationTestData.totalBears
            BetSide.BULL -> liveBetInformationTestData.totalBulls
        }
    }

    private fun getBiggestBet(betSide: BetSide): Double {
        return when (betSide) {
            BetSide.BEAR -> liveBetInformationTestData.biggestBearBet
            BetSide.BULL -> liveBetInformationTestData.biggestBullBet
        }
    }

    fun createBetInfoLabel(betInfoType: BetInfoType, betSide: BetSide): String {
        return when (betInfoType) {
            BetInfoType.TOTAL_WAGERED -> getTotalWageredForBetSide(betSide).getFormattedNumber()
            BetInfoType.RETURN_RATIO -> liveBetInformationTestData.getReturnRatio(betSide)
            BetInfoType.TOTAL_USERS -> "%,d".format(getTotalUsersWageringOnBet(betSide))
            BetInfoType.BIGGEST_BET -> getBiggestBet(betSide).getFormattedNumber()
        }
    }

}