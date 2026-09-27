package com.example.ootd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.Outfit
import com.example.ootd.ui.theme.*

private val historyFilters = listOf("All", "Work Meeting", "Casual Outing", "Dinner Date", "Workout", "Travel")

@Composable
fun OutfitHistoryScreen(onOutfitClick: (Outfit) -> Unit) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredOutfits = remember(selectedFilter, AppState.outfits.size) {
        if (selectedFilter == "All") AppState.outfits.toList()
        else AppState.outfits.filter { it.eventType.equals(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Outfit History", style = MaterialTheme.typography.titleLarge)
                Text("Track when & where outfits were worn", color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
            Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = OotdTextPrimary)
        }

        Spacer(Modifier.height(16.dp))

        // Event / Occasion Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(historyFilters) { filter ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (filter == selectedFilter) 4.dp else 1.dp,
                    color = if (filter == selectedFilter) OotdBlack else OotdWhite,
                    modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                        Text(
                            filter,
                            color = if (filter == selectedFilter) OotdWhite else OotdTextPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (filteredOutfits.isEmpty()) {
            Surface(
                color = OotdWhite,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "No history recorded for $selectedFilter yet.",
                    color = OotdTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredOutfits) { outfit ->
                    HistoryRow(outfit, onClick = { onOutfitClick(outfit) })
                }
                item { Spacer(Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
private fun HistoryRow(outfit: Outfit, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 5.dp,
        color = OotdWhite,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(OotdCardBg)
            ) {
                if (!outfit.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = outfit.imageUrl,
                        contentDescription = outfit.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(outfit.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)

                val detailsText = listOfNotNull(outfit.date, outfit.location?.let { "📍 $it" }).joinToString(" · ")
                Text(detailsText, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)

                Spacer(Modifier.height(6.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(OotdChipUnselected)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(outfit.style, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp)
                    }

                    if (!outfit.eventType.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(OotdCardBg)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("🎉 ${outfit.eventType}", style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = OotdTextSecondary)
        }
    }
}
