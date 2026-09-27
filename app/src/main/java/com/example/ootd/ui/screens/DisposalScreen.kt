package com.example.ootd.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.ClothingItem
import com.example.ootd.ui.theme.*
import java.util.Locale

private val disposalTabs = listOf("Unworn 7+ Days", "Unworn 30+ Days", "Unworn 90+ Days", "Disposal List")

@Composable
fun DisposalScreen(
    onBack: () -> Unit,
    onItemClick: (ClothingItem) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Unworn 7+ Days") }

    // Recompute list whenever disposalStatus or wardrobe list changes
    val wardrobeStateKey = AppState.wardrobe.map { "${it.id}_${it.disposalStatus}" }
    val filteredItems = remember(selectedTab, wardrobeStateKey, AppState.wardrobe.size) {
        when (selectedTab) {
            "Unworn 7+ Days" -> AppState.wardrobe.filter { it.daysUnworn >= 7 }
            "Unworn 30+ Days" -> AppState.wardrobe.filter { it.daysUnworn >= 30 }
            "Unworn 90+ Days" -> AppState.wardrobe.filter { it.daysUnworn >= 90 }
            "Disposal List" -> AppState.wardrobe.filter { it.disposalStatus == "Disposal" }
            else -> AppState.wardrobe.toList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = OotdTextPrimary)
            }
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Disposal Suggestions", style = MaterialTheme.typography.titleLarge)
                Text("Track unworn clothing & manage disposal items", color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Filter Tabs
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(disposalTabs) { tab ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (tab == selectedTab) 4.dp else 1.dp,
                    color = if (tab == selectedTab) OotdBlack else OotdWhite,
                    modifier = Modifier.clickable { selectedTab = tab }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            tab,
                            color = if (tab == selectedTab) OotdWhite else OotdTextPrimary,
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (filteredItems.isEmpty()) {
            Surface(
                color = OotdWhite,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = null, tint = OotdTextSecondary, modifier = Modifier.size(36.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = when (selectedTab) {
                            "Disposal List" -> "No items marked for disposal yet. Tap 'Mark for Disposal' on any item!"
                            else -> "No items found for this filter. All your clothes are being worn regularly!"
                        },
                        color = OotdTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredItems) { item ->
                    DisposalItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onToggleDisposal = {
                            val newStatus = if (item.disposalStatus == "Disposal") null else "Disposal"
                            AppState.updateItemDisposalStatus(item, newStatus)
                        }
                    )
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun DisposalItemCard(
    item: ClothingItem,
    onClick: () -> Unit,
    onToggleDisposal: () -> Unit
) {
    val isDisposal = (item.disposalStatus == "Disposal")

    Surface(
        color = OotdWhite,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 5.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OotdCardBg)
                ) {
                    if (!item.imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = item.imageUrl,
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(2.dp))
                    Text("${item.category} ${item.brand?.let { "· $it" } ?: ""}", color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)

                    Spacer(Modifier.height(4.dp))

                    val daysText = if (item.daysUnworn == 0) "Worn today" else "Unworn for ${item.daysUnworn} days"
                    val cpw = item.costPerWear()
                    val cpwText = if (cpw != null) "₱${String.format(Locale.getDefault(), "%.0f", cpw)} / wear" else ""

                    Text(
                        text = "🕒 $daysText  ·  $cpwText",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = when {
                            item.daysUnworn >= 90 -> Color(0xFFC5221F)
                            item.daysUnworn >= 30 -> Color(0xFFE65100)
                            item.daysUnworn >= 7 -> Color(0xFFF57F17)
                            else -> OotdBlack
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Disposal Toggle Action Button
            Button(
                onClick = onToggleDisposal,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isDisposal) Color(0xFFC5221F) else OotdBlack,
                    contentColor = OotdWhite
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = if (isDisposal) Icons.Filled.Check else Icons.Filled.Delete,
                    contentDescription = null,
                    tint = OotdWhite,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = if (isDisposal) "Marked for Disposal (Tap to Remove)" else "Mark for Disposal",
                    color = OotdWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
