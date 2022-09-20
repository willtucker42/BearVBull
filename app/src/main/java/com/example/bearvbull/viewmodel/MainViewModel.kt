package com.example.bearvbull.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bearvbull.util.Utility
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private var _countDownTime = MutableStateFlow(60000F)
    val countDownTime: StateFlow<Float> = _countDownTime

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
        collectTimerFlow()
    }

    private fun collectTimerFlow() {
        viewModelScope.launch {
            countDownTime.collectLatest {
                println(it)
            }
        }
    }

//    fun handleCountDownTimer() {
//        if (isPlaying.value == true) {
//            _celebrate.postValue(false)
//        } else {
//            startTimer()
//        }
//    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(Utility.TIME_COUNTDOWN, 1000) {
            override fun onTick(millisRemaining: Long) {
//                val progressValue = millisRemaining.toFloat() / Utility.TIME_COUNTDOWN
                _countDownTime.value = millisRemaining.toFloat()
            }

            override fun onFinish() {
            }
        }.start()
    }

}