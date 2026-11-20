package com.devilgamehub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devilgamehub.ui.theme.*

@Composable
fun NeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    gradientColors: List<Color> = listOf(NeonPurple, NeonCyan),
    textColor: Color = TextPrimary,
    height: Dp = 48.dp,
    enabled: Boolean = true
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (enabled) {
                    Brush.horizontalGradient(gradientColors)
                } else {
                    Brush.horizontalGradient(listOf(SurfaceBorder, SurfaceDark))
                }
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 6.dp)
                )
            }
            Text(
                text = text.uppercase(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                letterSpacing = 1.sp
            )
        }
    }
}
