package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HeatmapDay
import com.example.ui.theme.BentoViolet
import com.example.ui.theme.BentoVioletLight
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.HeatmapLevel0
import com.example.ui.theme.HeatmapLevel1
import com.example.ui.theme.HeatmapLevel2
import com.example.ui.theme.HeatmapLevel3
import com.example.ui.theme.HeatmapLevel4
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun ConsistencyHeatmap(
    days: List<HeatmapDay>,
    modifier: Modifier = Modifier,
    onDayClick: ((HeatmapDay) -> Unit)? = null
) {
    var selectedDay by remember { mutableStateOf<HeatmapDay?>(null) }
    val scrollState = rememberScrollState()

    val weeks = remember(days) {
        val list = mutableListOf<List<HeatmapDay>>()
        var currentWeek = mutableListOf<HeatmapDay>()
        days.forEach { day ->
            currentWeek.add(day)
            if (day.dayOfWeek == 7) { // Sunday ends week
                list.add(currentWeek)
                currentWeek = mutableListOf()
            }
        }
        if (currentWeek.isNotEmpty()) {
            list.add(currentWeek)
        }
        list
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        borderColor = GlassBorder,
        glowColor = BentoViolet.copy(alpha = 0.25f)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Consistency Heatmap",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate50
                    )
                    Text(
                        text = "Past 12 weeks of activity",
                        fontSize = 12.sp,
                        color = Slate400
                    )
                }

                // Selected info pill
                if (selectedDay != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(Slate800)
                            .border(1.dp, BentoViolet.copy(alpha = 0.6f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${DateUtils.formatShortMonthDay(selectedDay!!.dateString)}: ${selectedDay!!.count} habits",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoVioletLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Heatmap Grid Container with Horizontal Scroll
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day of week labels (M, W, F)
                Column(
                    modifier = Modifier.padding(end = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val labels = listOf("M", "", "W", "", "F", "", "S")
                    labels.forEach { label ->
                        Box(
                            modifier = Modifier.size(13.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate500
                            )
                        }
                    }
                }

                // Weeks columns
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    weeks.forEach { week ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            val dayMap = week.associateBy { it.dayOfWeek }
                            for (dayIndex in 1..7) {
                                val day = dayMap[dayIndex]
                                if (day != null) {
                                    val cellColor = when (day.intensity) {
                                        4 -> HeatmapLevel4
                                        3 -> HeatmapLevel3
                                        2 -> HeatmapLevel2
                                        1 -> HeatmapLevel1
                                        else -> HeatmapLevel0
                                    }

                                    val isCurrentSelection = selectedDay?.dateString == day.dateString

                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(cellColor)
                                            .then(
                                                if (isCurrentSelection) {
                                                    Modifier.border(1.dp, BentoVioletLight, RoundedCornerShape(3.dp))
                                                } else Modifier
                                            )
                                            .clickable {
                                                selectedDay = day
                                                onDayClick?.invoke(day)
                                            }
                                    )
                                } else {
                                    Box(modifier = Modifier.size(13.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Less",
                    fontSize = 10.sp,
                    color = Slate500,
                    modifier = Modifier.padding(end = 4.dp)
                )
                listOf(HeatmapLevel0, HeatmapLevel1, HeatmapLevel2, HeatmapLevel3, HeatmapLevel4).forEach { col ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(10.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(col)
                    )
                }
                Text(
                    text = "More",
                    fontSize = 10.sp,
                    color = Slate500,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
    }
}

