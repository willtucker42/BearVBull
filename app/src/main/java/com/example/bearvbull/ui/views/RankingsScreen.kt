package com.example.bearvbull.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.data.users.PodiumUsers
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.components.PodiumRow
import com.example.bearvbull.ui.components.RankingsUserList
import com.example.bearvbull.ui.theme.DeepPurple
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.viewmodel.MainViewModel


@Composable
fun RankingsScreen(viewModel: MainViewModel) {
    // Get the first 3 users from the list of RankingsUsers, transform to a Triple for PodiumUsers.kt
    val podiumUsers: PodiumUsers =
        viewModel.fakeRankingsUserList.subList(fromIndex = 0, toIndex = 3).let {
            val list = mutableListOf<UserAccountInformation>()
            it.forEach { i ->
                list.add(i)
            }
            PodiumUsers(list)
        }
    viewModel.fakeRankingsUserList.subList(fromIndex = 0, toIndex = 3).clear()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepPurple),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            RankingsTopBar()
            Spacer(Modifier.height(12.dp))
            PodiumRow(podiumUsers.users)
            RankingsUserList(viewModel.fakeRankingsUserList)
        }
    }
}

@Composable
fun RankingsTopBar() {
    Row(modifier = Modifier.padding(12.dp)) {
        Spacer(modifier = Modifier.weight(1.0f))
        Text(
            text = "Rankings",
            fontFamily = poppinsFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.weight(1.0f))
    }
}