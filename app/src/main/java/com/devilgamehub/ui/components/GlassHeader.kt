package com.devilgamehub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
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
import com.devilgamehub.ui.theme.*

@Composable
fun GlassHeader(
    isOffline: Boolean = false,
    subtitle: String = "DYNAMIC GAMING HUB",
    onHeaderClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(NeonPurple, NeonCyan)
                        )
                    )
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = "Logo",
                        tint = NeonCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "DEVIL ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonPurple
                    )
                    Text(
                        text = "GAME HUB",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan
                    )
                }
                Text(
                    text = subtitle.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
            }
        }

        // Network State Badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isOffline) Color(0x33FF2A6D) else Color(0x3300FF66))
                .border(
                    width = 1.dp,
                    color = if (isOffline) NeonRed else NeonGreen,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (isOffline) NeonRed else NeonGreen)
                )
                Icon(
                    imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                    contentDescription = if (isOffline) "Offline Mode" else "GitHub Live",
                    tint = if (isOffline) NeonRed else NeonGreen,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = if (isOffline) "OFFLINE" else "LIVE DB",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isOffline) NeonRed else NeonGreen
                )
            }
        }
    }
}
