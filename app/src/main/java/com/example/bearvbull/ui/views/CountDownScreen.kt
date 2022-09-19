package com.example.bearvbull.ui.views

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.example.bearvbull.util.Utility
import com.example.bearvbull.util.Utility.formatTime
import com.example.bearvbull.viewmodel.MainViewModel

@Composable
fun CountDownView(viewModel: MainViewModel) {
    val time by viewModel.time.observe(Utility.TIME_COUNTDOWN.formatTime())
}