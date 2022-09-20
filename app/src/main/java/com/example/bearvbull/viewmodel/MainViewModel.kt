package com.example.bearvbull.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bearvbull.util.Utility
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

}