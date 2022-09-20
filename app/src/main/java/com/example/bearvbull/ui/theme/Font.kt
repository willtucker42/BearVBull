package com.example.bearvbull.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.bearvbull.R

val interFontFamily = FontFamily(
    Font(R.font.inter_regular, weight = FontWeight.Normal),
    Font(R.font.inter_black, weight = FontWeight.Black),
    Font(R.font.inter_bold, weight = FontWeight.Bold),
    Font(R.font.inter_extra_bold, weight = FontWeight.ExtraBold),
    Font(R.font.inter_semibold, weight = FontWeight.SemiBold),
    Font(R.font.inter_light, weight = FontWeight.Light),
    Font(R.font.inter_extra_light, weight = FontWeight.ExtraLight),
    Font(R.font.inter_thin, weight = FontWeight.Thin),
    Font(R.font.inter_medium, weight = FontWeight.Medium)
)

val poppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular, weight = FontWeight.Normal),
    Font(R.font.poppins_bold, weight = FontWeight.Bold),
    Font(R.font.poppins_bold_italic, style = FontStyle.Italic, weight = FontWeight.Bold),
    Font(R.font.poppins_extra_bold, weight = FontWeight.ExtraBold),
    Font(R.font.poppins_medium, weight = FontWeight.Medium),
    Font(R.font.poppins_italic, weight = FontWeight.Normal, style = FontStyle.Italic),

)