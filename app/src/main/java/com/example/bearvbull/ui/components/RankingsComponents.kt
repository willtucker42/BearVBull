package com.example.bearvbull.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bearvbull.ui.theme.Purple200
import com.example.bearvbull.ui.theme.poppinsFontFamily
import com.example.bearvbull.util.PodiumRanks
import com.example.bearvbull.util.testProfilePics

@Preview
@Composable
fun PodiumRow() {
    Row(
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (rank in PodiumRanks.values()) {
            RankingsPodiumBox(rank = rank, image = testProfilePics[rank.ordinal])
        }
    }
}

@Composable
fun RankingsPodiumBox(rank: PodiumRanks, image: Int, username: String = "willTucker42") {
    Box(
        Modifier.fillMaxHeight()
    ) {
        Column(modifier = Modifier.align(if (rank.rank == "First") Alignment.TopCenter else Alignment.Center)) {
            Image(
                painter = painterResource(image),
                contentDescription = rank.rank,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = rank.color, CircleShape)

                    .background(Purple200)
            )
            Text(
                text = username,
                fontFamily = poppinsFontFamily,
                fontStyle = FontStyle.Italic,
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}
