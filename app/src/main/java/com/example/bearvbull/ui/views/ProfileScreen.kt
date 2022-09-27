package com.example.bearvbull.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.bearvbull.ui.components.ProfileBetHistoryContainer
import com.example.bearvbull.ui.components.ProfilePictureAndInfo
import com.example.bearvbull.ui.components.ProfileTopBar
import com.example.bearvbull.ui.theme.DeepPurple
import com.example.bearvbull.viewmodel.MainViewModel

@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileTopBar()
            ProfilePictureAndInfo(userAccountInformation = viewModel.fakeUser)
            ProfileBetHistoryContainer(viewModel = viewModel)
        }
    }
}

