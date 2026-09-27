package com.example.ootd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.ClothingItem
import com.example.ootd.ui.theme.*

@Composable
fun WardrobePickerScreen(onDone: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Close, contentDescription = "Close", modifier = Modifier.clickable { onDone() })
                Spacer(Modifier.width(16.dp))
                Text("Select Items", style = MaterialTheme.typography.titleLarge)
            }
            Text(
                "Done",
                color = OotdBlack,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.clickable { onDone() }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            items(AppState.wardrobe) { item ->
                val isSelected = AppState.pendingOutfitItemIds.contains(item.id)
                PickerCard(item, isSelected) {
                    if (isSelected) AppState.pendingOutfitItemIds.remove(item.id)
                    else AppState.pendingOutfitItemIds.add(item.id)
                }
            }
        }
    }
}

@Composable
private fun PickerCard(item: ClothingItem, isSelected: Boolean, onToggle: () -> Unit) {
    Column(modifier = Modifier.clickable { onToggle() }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(16.dp))
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
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) OotdBlack else OotdChipUnselected),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(Icons.Filled.Check, contentDescription = "Selected", tint = OotdWhite, modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(item.name, style = MaterialTheme.typography.bodyLarge)
        Text(item.category, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
