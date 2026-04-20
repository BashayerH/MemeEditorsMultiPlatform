package com.example.memeeditor.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import memeeditor.composeapp.generated.resources.ElMessiri_VariableFont_wght
import memeeditor.composeapp.generated.resources.Res
import memeeditor.composeapp.generated.resources.Tajawal_Medium
import memeeditor.composeapp.generated.resources.impact
import org.jetbrains.compose.resources.Font

object Fonts {
     val Impact  @Composable get() =
         FontFamily(
             Font( resource = Res.font.impact,
                 weight = FontWeight.Normal,
             )
         )

    val Tajwal @Composable get() =
        FontFamily(
            Font(resource = Res.font.Tajawal_Medium,
                weight = FontWeight.Normal
                )
        )
    val Elmessiri @Composable get() =
        FontFamily(
            Font(resource = Res.font.ElMessiri_VariableFont_wght,
                weight = FontWeight.Normal)
        )
 }

val Typography: Typography
    @Composable get()=
        Typography(
            displayLarge = TextStyle(
                fontFamily = Fonts.Impact,
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp,
                lineHeight = 64.sp

            ),
            displayMedium = TextStyle(
                fontFamily = Fonts.Impact,
                fontWeight = FontWeight.Normal,
                fontSize = 45.sp,
                lineHeight = 52.sp
            ),
            displaySmall = TextStyle(
                fontFamily = Fonts.Impact,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                lineHeight = 44.sp
            ),
            headlineLarge = TextStyle(
                fontFamily = Fonts.Impact,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 40.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = Fonts.Tajwal,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp
            ),
            bodySmall = TextStyle(
                fontFamily = Fonts.Tajwal,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),
            labelLarge = TextStyle(
                fontFamily = Fonts.Elmessiri,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp
            ),

            )





