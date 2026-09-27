package com.example.ootd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.example.ootd.data.Outfit
import com.example.ootd.ui.theme.*

private val favoritesTabs = listOf("Clothes", "Outfits")

@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onItemClick: (ClothingItem) -> Unit,
    onOutfitClick: (Outfit) -> Unit
) {
    var selectedTab by remember { mutableStateOf("Clothes") }

    val favoritedItems = remember(AppState.wardrobe.map { "${it.id}_${it.isFavorite}" }) {
        AppState.wardrobe.filter { it.isFavorite }
    }

    val favoritedOutfits = remember(AppState.outfits.map { "${it.id}_${it.isFavorite}" }) {
        AppState.outfits.filter { it.isFavorite }
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
                Text("My Favorites", style = MaterialTheme.typography.titleLarge)
                Text("Your bookmarked clothing and outfits", color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Filter Tabs
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(favoritesTabs) { tab ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    shadowElevation = if (tab == selectedTab) 4.dp else 1.dp,
                    color = if (tab == selectedTab) OotdBlack else OotdWhite,
                    modifier = Modifier.clickable { selectedTab = tab }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
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

        if (selectedTab == "Clothes") {
            if (favoritedItems.isEmpty()) {
                EmptyFavoritesCard("No favorited clothing items yet. Tap the heart icon on any clothes to bookmark!")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(favoritedItems) { item ->
                        FavoriteItemRow(
                            item = item,
                            onClick = { onItemClick(item) },
                            onToggleFavorite = { AppState.toggleItemFavorite(item) }
                        )
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        } else {
            if (favoritedOutfits.isEmpty()) {
                EmptyFavoritesCard("No favorited outfits yet. Tap the heart icon on any outfit to bookmark!")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(favoritedOutfits) { outfit ->
                        FavoriteOutfitRow(
                            outfit = outfit,
                            onClick = { onOutfitClick(outfit) },
                            onToggleFavorite = { AppState.toggleOutfitFavorite(outfit) }
                        )
                    }
                    item { Spacer(Modifier.height(20.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavoritesCard(message: String) {
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
            Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                color = OotdTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun FavoriteItemRow(
    item: ClothingItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        color = OotdWhite,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 5.dp,
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
                Text(item.price ?: "Price unlisted", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = OotdBlack)
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(Icons.Filled.Favorite, contentDescription = "Favorite", tint = Color(0xFFE53935))
            }
        }
    }
}

@Composable
private fun FavoriteOutfitRow(
    outfit: Outfit,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Surface(
        color = OotdWhite,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 5.dp,
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

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(outfit.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(2.dp))
                Text(outfit.style, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text(outfit.date, color = OotdTextSecondary, fontSize = 12.sp)
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(Icons.Filled.Favorite, contentDescription = "Favorite", tint = Color(0xFFE53935))
            }
        }
    }
}
