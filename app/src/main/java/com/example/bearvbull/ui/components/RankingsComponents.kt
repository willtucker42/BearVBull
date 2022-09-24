package com.example.bearvbull.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.util.formatBigLong
import com.example.bearvbull.R.drawable


@Composable
fun PodiumRow(podiumUsers: List<UserAccountInformation>) {
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val reorderedListForPodium = podiumUsers.let {
            listOf(it[1], it[0], it[2])
        }
        reorderedListForPodium.forEachIndexed { index, userAccountInformation ->
            RankingsPodiumBox(
                user = userAccountInformation,
                rank = index,
                profileImage = userAccountInformation.profileImage
            )
        }
    }
}

@Composable
fun RankingsUserList(userList: List<UserAccountInformation>) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        LazyColumn() {
            items(items = userList) { user ->
                RankingsUserRow(user = user)
            }
        }
    }
}

@Composable
fun RankingsUserRow(user: UserAccountInformation) {
    Row(verticalAlignment = CenterVertically) {
        Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            Text(
                text = user.rank.toString(),
                fontFamily = poppinsFontFamily,
                fontSize = 18.sp,
                color = Color.White
            )
            Image(
                painter = painterResource(user.profileImage),
                contentDescription = "${user.userName}, rank: ${user.rank}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Purple200)
            )
            Text(
                text = user.userName,
                fontFamily = poppinsFontFamily,
                fontSize = 14.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row() {
            Row(
                modifier = Modifier.align(CenterVertically),
                verticalAlignment = CenterVertically
            ) {
                Image(
                    painter = painterResource(id = drawable.cash_icon),
                    contentDescription = "Amount of cash",
                    Modifier
                        .size(12.dp)
                        .padding(end = 2.dp)
                )
                Text(text = user.userBalance.formatBigLong(), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RankingsPodiumBox(
    user: UserAccountInformation,
    rank: Int,
    profileImage: Int
) {
    val color = when (rank) {
        1 -> PodiumGold
        0 -> PodiumSilver
        2 -> PodiumBronze
        else -> Color.Black
    }
    Box(
        Modifier.fillMaxHeight()
    ) {
        Column(modifier = Modifier.align(if (rank == 1) Alignment.TopCenter else Alignment.Center)) {
            Image(
                painter = painterResource(id = drawable.gold_crown_icon),
                contentDescription = "Leader",
                Modifier
                    .size(28.dp)
                    .padding(4.dp)
                    .align(CenterHorizontally)
                    .alpha(if (rank == 1) 1f else 0f)
            )
            Image(
                painter = painterResource(profileImage),
                contentDescription = rank.toString(),
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(width = 3.dp, color = color, CircleShape)
                    .background(Purple200)
            )
            Text(
                text = user.userName,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = color,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp)
            )
            Row(
                modifier = Modifier.align(CenterHorizontally),
                verticalAlignment = CenterVertically
            ) {
                Image(
                    painter = painterResource(id = drawable.cash_icon),
                    contentDescription = "Amount of cash",
                    Modifier
                        .size(12.dp)
                        .padding(end = 2.dp)
                )
                Text(text = user.userBalance.formatBigLong(), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
