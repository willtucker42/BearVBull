package com.example.bearvbull.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.R
import com.example.bearvbull.data.users.UserAccountInformation
import com.example.bearvbull.ui.theme.*
import com.example.bearvbull.util.formatBigLong
import com.example.bearvbull.R.drawable.*
import com.example.bearvbull.util.trimName


@Composable
fun PodiumRow(podiumUsers: List<UserAccountInformation>) {
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (podiumUsers.size >= 3) {
            val reorderedListForPodium = podiumUsers.let {
                listOf(it[1], it[0], it[2])
            }
            reorderedListForPodium.forEachIndexed { index, userAccountInformation ->
                RankingsPodiumBox(
                    user = userAccountInformation,
                    rank = index,
                    profileImage = green_wojak
                )
            }
        }

    }
}

@Composable
fun RankingsUserList(userList: List<UserAccountInformation>) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .fillMaxWidth()
            .background(NotSoDeepPurple)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(items = userList) { user ->
                RankingsUserRow(user = user)
            }
        }
    }
}

@Composable
fun RankingsUserRow(user: UserAccountInformation) {
    Box(
        Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(ButtonOutline)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = CenterVertically) {
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = (user.rank + 1).toString(),
                    fontFamily = poppinsFontFamily,
                    fontSize = 18.sp,
                    color = Color.White,

                )
                Image(
                    painter = painterResource(green_wojak),
                    contentDescription = "${user.userName}, rank: ${user.rank}",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Purple200)
                )
                Text(
                    text = user.userName.trimName(),
                    fontFamily = poppinsFontFamily, fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = CenterVertically
            ) {
                Image(
                    painter = painterResource(id = cash_icon),
                    contentDescription = "Amount of cash",
                    Modifier
                        .size(16.dp)
                        .padding(end = 2.dp)
                )
                Text(
                    text = user.userBalance.formatBigLong(),
                    color = Color.White,
                    fontSize = 16.sp
                )
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
                painter = painterResource(id = gold_crown_icon),
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
                text = user.userName.trimName(),
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
                    painter = painterResource(id = cash_icon),
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
