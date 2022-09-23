package com.example.bearvbull.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bearvbull.util.PodiumRanks


@Composable
fun RankingsPodiumBox(rank: PodiumRanks, image: Int) {
    Box() {
        Image(
            painter = painterResource(image),
            contentDescription = rank.name,
            modifier = Modifier.size(44.dp).clip(CircleShape)
                .border(width = 2.dp,Colo)
        )
    }
}