package com.example.bearvbull.ui.views

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.bearvbull.ui.components.ProfileBetHistoryContainer
import com.example.bearvbull.ui.components.ProfilePictureAndInfo
import com.example.bearvbull.ui.components.ProfileTopBar
import com.example.bearvbull.ui.theme.DeepPurple
import com.example.bearvbull.viewmodel.MainViewModel

@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple),
    ) {
        val activeUser = viewModel.activeUser.collectAsState()
        val changeUserNameValue = viewModel.changeUserNameValue.collectAsState()
        val badUserNameValue = viewModel.badUserName.collectAsState()
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileTopBar(viewModel = viewModel)
            ProfilePictureAndInfo(
                userAccountInformation = activeUser.value,
                editNameFunction = { newUserName: String ->
                    viewModel.checkNewUserName(
                        newUserName,
                        context
                    )
                },
                changeUserNameValue = changeUserNameValue.value,
                badUserName = badUserNameValue.value
            )
            ProfileBetHistoryContainer(viewModel = viewModel)
        }
    }
}

