package com.devilgamehub.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devilgamehub.ui.theme.*

@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SignalCellularConnectedNoInternet0Bar,
                contentDescription = null,
                tint = NeonRed,
                modifier = Modifier.size(54.dp)
            )

            Text(
                text = "CONNECTION NOTICE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )

            Text(
                text = message,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            NeonButton(
                text = "RETRY SYNC",
                onClick = onRetry,
                icon = Icons.Default.Refresh,
                gradientColors = listOf(NeonRed, NeonPurple)
            )
        }
    }
}
