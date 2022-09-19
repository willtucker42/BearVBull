package com.example.bearvbull.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bearvbull.util.Utility
import com.example.bearvbull.util.Utility.formatTime
import com.example.bearvbull.util.helper.SingleLiveEvent

class MainViewModel : ViewModel() {
    private var countDownTimer: CountDownTimer? = null

    private val _time = MutableLiveData(Utility.TIME_COUNTDOWN.formatTime())
    val time: LiveData<String> = _time

    private val _progress = MutableLiveData(1.00f)
    val progress: LiveData<Float> = _progress

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _celebrate = SingleLiveEvent<Boolean>()

    private val celebrate : LiveData<Boolean> get() = _celebrate

    fun handleCountDownTimer() {
        if (isPlaying.value == true) {
            pauseTimer()
            _celebrate.postValue(false)
        } else {
            startTimer()
        }
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        handleTimerValues(false, Utility.TIME_COUNTDOWN.formatTime(), 1.0F, false)
    }

    private fun startTimer() {
        _isPlaying.value = true
        countDownTimer = object : CountDownTimer(Utility.TIME_COUNTDOWN, 1000) {
            override fun onTick(millisRemaining: Long) {
                val progressValue = millisRemaining.toFloat() / Utility.TIME_COUNTDOWN
                handleTimerValues(true, millisRemaining.formatTime(), progressValue, false)
                _celebrate.postValue((false))
            }

            override fun onFinish() {
                pauseTimer()
                _celebrate.postValue(true)
            }
        }.start()
    }

    private fun handleTimerValues(isPlaying: Boolean, text: String, progress: Float, celebrate: Boolean) {
        _isPlaying.value = isPlaying
        _time.value = text
        _progress.value = progress
        _celebrate.postValue(celebrate)
    }
}