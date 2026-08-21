package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TierLevel

@Composable
fun TierBadge(
    tier: TierLevel,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    fontSize: TextUnit = 12.sp,
    onClick: (() -> Unit)? = null
) {
    val tierColor = try {
        Color(android.graphics.Color.parseColor(tier.primaryColorHex))
    } catch (_: Exception) {
        Color(0xFF8B5CF6)
    }

    val animatedColor by animateColorAsState(targetValue = tierColor, label = "tier_color")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        animatedColor.copy(alpha = 0.22f),
                        animatedColor.copy(alpha = 0.08f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        animatedColor.copy(alpha = 0.75f),
                        animatedColor.copy(alpha = 0.35f)
                    )
                ),
                shape = RoundedCornerShape(100.dp)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .testTag("tier_badge_${tier.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = tier.iconEmoji,
                fontSize = fontSize
            )
            Text(
                text = tier.title,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = animatedColor
            )
        }
    }
}
