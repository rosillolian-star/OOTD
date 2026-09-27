package com.example.ootd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.ClothingItem
import com.example.ootd.ui.theme.*

private val wardrobeTabs = listOf("All", "Tops", "Bottoms", "Outerwear", "Shoes", "Accessories")

@Composable
fun WardrobeScreen(onItemClick: (ClothingItem) -> Unit) {
    var selectedTab by remember { mutableStateOf("All") }

    val filtered = remember(selectedTab, AppState.wardrobe.size) {
        if (selectedTab == "All") AppState.wardrobe.toList()
        else AppState.wardrobe.filter { it.category == selectedTab }
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
            Text("My Wardrobe", style = MaterialTheme.typography.titleLarge)
            Icon(Icons.Filled.FilterList, contentDescription = "Filter", tint = OotdTextPrimary)
        }

        Spacer(Modifier.height(16.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(wardrobeTabs) { tab ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (tab == selectedTab) 6.dp else 2.dp,
                    color = if (tab == selectedTab) OotdBlack else OotdWhite,
                    modifier = Modifier.bounceClick { selectedTab = tab }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            tab,
                            color = if (tab == selectedTab) OotdWhite else OotdTextPrimary,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            gridItems(filtered) { item ->
                WardrobeGridCard(item, onClick = { onItemClick(item) })
            }
        }
    }
}

@Composable
private fun WardrobeGridCard(item: ClothingItem, onClick: () -> Unit) {
    Column(modifier = Modifier.bounceClick { onClick() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            color = OotdCardBg,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!item.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(item.name, style = MaterialTheme.typography.bodyLarge)
        Text(item.category, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
