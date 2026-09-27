package com.example.ootd.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ootd.ui.theme.OotdBlack
import com.example.ootd.ui.theme.OotdTextSecondary
import com.example.ootd.ui.theme.OotdWhite

enum class NavTab { HOME, WARDROBE, PLANNER, OUTFITS, PROFILE }

@Composable
fun OotdBottomBar(
    current: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onAddClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Bottom Navigation Bar with 5 main tabs
        Surface(
            color = OotdWhite,
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(Icons.Outlined.Home, "Home", current == NavTab.HOME) { onTabSelected(NavTab.HOME) }
                NavItem(Icons.Outlined.Checkroom, "Wardrobe", current == NavTab.WARDROBE) { onTabSelected(NavTab.WARDROBE) }
                NavItem(Icons.Outlined.CalendarToday, "Planner", current == NavTab.PLANNER) { onTabSelected(NavTab.PLANNER) }
                NavItem(Icons.Outlined.Style, "Outfits", current == NavTab.OUTFITS) { onTabSelected(NavTab.OUTFITS) }
                NavItem(Icons.Outlined.Person, "Profile", current == NavTab.PROFILE) { onTabSelected(NavTab.PROFILE) }
            }
        }

        // Top-Centered Floating Add (+) Button raised higher up above the top edge
        Surface(
            color = OotdBlack,
            shape = CircleShape,
            shadowElevation = 10.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-32).dp)
                .size(48.dp)
                .clickable { onAddClick() }
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add",
                    tint = OotdWhite,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
private fun NavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val animatedColor by animateColorAsState(
        targetValue = if (selected) OotdBlack else OotdTextSecondary,
        animationSpec = tween(220),
        label = "TabColor"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (selected) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "TabScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = animatedColor,
            modifier = Modifier
                .graphicsLayer(scaleX = animatedScale, scaleY = animatedScale)
                .size(20.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = animatedColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
