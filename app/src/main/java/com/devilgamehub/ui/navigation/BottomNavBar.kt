package com.devilgamehub.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.devilgamehub.ui.theme.*

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route

    // Hide navigation bar when in player screen
    if (currentRoute != null && currentRoute.startsWith("player")) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceDark.copy(alpha = 0.95f))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(listOf(NeonPurple.copy(alpha = 0.4f), NeonCyan.copy(alpha = 0.4f))),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Screen.bottomNavItems.forEach { screen ->
                val isSelected = currentDestination?.hierarchy?.any {
                    it.route != null && it.route!!.startsWith(screen.route)
                } == true

                val iconTint by animateColorAsState(
                    targetValue = if (isSelected) NeonCyan else TextMuted,
                    label = "iconTint"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) TextPrimary else TextMuted,
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) SurfaceLightDark else Color.Transparent)
                        .clickable {
                            if (screen.route == Screen.Home.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        inclusive = false
                                    }
                                    launchSingleTop = true
                                }
                            } else {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        screen.icon?.let { icon ->
                            Icon(
                                imageVector = icon,
                                contentDescription = screen.title,
                                tint = iconTint,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = screen.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }
    }
}
