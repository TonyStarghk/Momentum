package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BentoCardBg
import com.example.ui.theme.GlassBorder

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = GlassBorder,
    backgroundColor: Color = BentoCardBg,
    glowColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val finalBorderBrush = if (glowColor != null) {
        Brush.linearGradient(
            listOf(
                glowColor.copy(alpha = 0.5f),
                borderColor.copy(alpha = 0.25f),
                Color.Transparent
            )
        )
    } else {
        Brush.linearGradient(
            listOf(
                borderColor,
                borderColor.copy(alpha = 0.15f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, finalBorderBrush, shape)
    ) {
        content()
    }
}

