package com.example.ootd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.ClothingItem
import com.example.ootd.data.Outfit
import com.example.ootd.ui.theme.*

private val categoryFilters = listOf("All", "Formal", "Casual", "Streetwear", "Sport", "Evening")

@Composable
fun HomeScreen(
    onOutfitClick: (Outfit) -> Unit,
    onSeeAllOutfits: () -> Unit,
    onWardrobeItemClick: (ClothingItem) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    // Real-time filtered outfits list
    val filteredOutfits = remember(searchQuery, selectedFilter, AppState.outfits.size) {
        AppState.outfits.filter { outfit ->
            val matchesCategory = (selectedFilter == "All" || outfit.style.equals(selectedFilter, ignoreCase = true))
            val matchesQuery = searchQuery.isBlank() ||
                    outfit.name.contains(searchQuery, ignoreCase = true) ||
                    outfit.style.contains(searchQuery, ignoreCase = true) ||
                    outfit.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesQuery
        }
    }

    // Real-time filtered wardrobe items list
    val filteredWardrobe = remember(searchQuery, selectedFilter, AppState.wardrobe.size) {
        AppState.wardrobe.filter { item ->
            val matchesCategory = (selectedFilter == "All" || item.category.equals(selectedFilter, ignoreCase = true))
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true) ||
                    (item.brand?.contains(searchQuery, ignoreCase = true) == true)
            matchesCategory && matchesQuery
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(20.dp))

        // Greeting Header (Padded 20.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Good morning,", style = MaterialTheme.typography.bodyMedium, color = OotdTextSecondary)
                Text(AppState.userName, style = MaterialTheme.typography.headlineMedium, color = OotdTextPrimary)
            }
            Surface(
                shape = CircleShape,
                shadowElevation = 6.dp,
                color = OotdOffBlack,
                modifier = Modifier.size(44.dp)
            ) {
                if (!AppState.profilePictureUri.value.isNullOrBlank()) {
                    AsyncImage(
                        model = AppState.profilePictureUri.value,
                        contentDescription = "Profile Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Floating Search Bar with Elevation (Padded 20.dp)
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 6.dp,
                color = OotdWhite,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Search outfits, clothes, or tags...", color = OotdTextSecondary, style = MaterialTheme.typography.bodyMedium)
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = OotdTextSecondary)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Close, contentDescription = "Clear search", tint = OotdTextSecondary)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = OotdBlack,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Edge-to-Edge Floating Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categoryFilters) { filter ->
                FilterChipItem(
                    label = filter,
                    selected = filter == selectedFilter,
                    onClick = { selectedFilter = filter }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // Today's Picks Header (Padded 20.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Today's Picks", style = MaterialTheme.typography.titleLarge)
            Text(
                "See all >",
                color = OotdTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onSeeAllOutfits() }
            )
        }
        Spacer(Modifier.height(12.dp))

        // Edge-to-Edge Floating Outfits Carousel
        if (filteredOutfits.isEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Surface(
                    color = OotdWhite,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No matching outfits found",
                        color = OotdTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredOutfits) { outfit ->
                    OutfitCard(outfit, onClick = { onOutfitClick(outfit) })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Your Wardrobe Header (Padded 20.dp)
        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text("Your Wardrobe", style = MaterialTheme.typography.titleLarge)
        }
        Spacer(Modifier.height(12.dp))

        // Edge-to-Edge Floating Wardrobe Carousel
        if (filteredWardrobe.isEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                Surface(
                    color = OotdWhite,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No matching wardrobe items found",
                        color = OotdTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(filteredWardrobe) { item ->
                    WardrobeThumb(item, onClick = { onWardrobeItemClick(item) })
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        shadowElevation = if (selected) 6.dp else 2.dp,
        color = if (selected) OotdBlack else OotdWhite,
        modifier = Modifier.bounceClick { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                label,
                color = if (selected) OotdWhite else OotdTextPrimary,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun OutfitCard(outfit: Outfit, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(160.dp)
            .bounceClick { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            color = OotdCardBg,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!outfit.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = outfit.imageUrl,
                        contentDescription = outfit.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                IconButton(
                    onClick = { AppState.toggleOutfitFavorite(outfit) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(OotdWhite.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (outfit.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (outfit.isFavorite) Color(0xFFE53935) else OotdTextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(outfit.name, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyLarge)
        Text(outfit.date, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun WardrobeThumb(item: ClothingItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .bounceClick { onClick() }
    ) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            shadowElevation = 4.dp,
            color = OotdCardBg,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
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
                IconButton(
                    onClick = { AppState.toggleItemFavorite(item) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(OotdWhite.copy(alpha = 0.85f))
                ) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (item.isFavorite) Color(0xFFE53935) else OotdTextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(item.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1)
        Text(item.category, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
