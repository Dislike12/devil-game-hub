package com.devilgamehub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devilgamehub.data.model.Category
import com.devilgamehub.ui.theme.*

@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onSelect: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(listOf(NeonPurple, NeonCyan))
                } else {
                    Brush.horizontalGradient(listOf(SurfaceDark, SurfaceLightDark))
                }
            )
            .border(
                width = 1.dp,
                color = if (isSelected) NeonCyan else SurfaceBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect(category) }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TextPrimary else TextSecondary
        )
    }
}
