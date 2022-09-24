package com.example.bearvbull.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bearvbull.R
import androidx.compose.ui.unit.sp
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.Purple200
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.util.PodiumRanks
import com.example.bearvbull.util.formatBigLong
import com.example.bearvbull.util.testProfilePics

@Preview
@Composable
fun PodiumRow(podiumUsers : ) {
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (rank in PodiumRanks.values()) {
            RankingsPodiumBox(rank = rank, profileImage = testProfilePics[rank.ordinal])
        }
    }
}

@Composable
fun RankingsList(users: List<UserAccountInformation>) {

}

@Composable
fun UserRowRankingsRow(user: UserAccountInformation) {
    Row() {
        Row() {
            Text(text = user.rank.toString(), fontFamily = poppinsFontFamily, fontSize = 18.sp)
            Image(
                painter = painterResource(user.profileImage),
                contentDescription = "${user.userName}, rank: ${user.rank}",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Purple200)
            )
            Text(text = user.userName, fontFamily = poppinsFontFamily, fontSize = 14.sp)
        }
        Row() {
            Row(
                modifier = Modifier.align(CenterVertically),
                verticalAlignment = CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cash_icon),
                    contentDescription = "Amount of cash",
                    Modifier
                        .size(12.dp)
                        .padding(end = 2.dp)
                )
                Text(text = user.userBalance.formatBigLong(), color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun RankingsPodiumBox(
    rank: PodiumRanks,
    profileImage: Int,
    username: String = "willTucker42",
    cashAmount: Long = 1987382
) {
    Box(
        Modifier.fillMaxHeight()
    ) {
        Column(modifier = Modifier.align(if (rank.rank == "First") Alignment.TopCenter else Alignment.Center)) {
            Image(
                painter = painterResource(id = R.drawable.gold_crown_icon),
                contentDescription = "Leader",
                Modifier
                    .size(28.dp)
                    .padding(4.dp)
                    .align(CenterHorizontally)
                    .alpha(if (rank.rank == "First") 1f else 0f)
            )
            Image(
                painter = painterResource(profileImage),
                contentDescription = rank.rank,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(width = 3.dp, color = rank.color, CircleShape)
                    .background(Purple200)
            )
            Text(
                text = username,
                fontFamily = poppinsFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = rank.color,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp)
            )
            Row(
                modifier = Modifier.align(CenterHorizontally),
                verticalAlignment = CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cash_icon),
                    contentDescription = "Amount of cash",
                    Modifier
                        .size(12.dp)
                        .padding(end = 2.dp)
                )
                Text(text = cashAmount.formatBigLong(), color = Color.White, fontSize = 12.sp)
            }
        }
    }
}
