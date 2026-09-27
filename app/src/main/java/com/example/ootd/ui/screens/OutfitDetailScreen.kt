package com.example.ootd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.Outfit
import com.example.ootd.ui.theme.*

@Composable
fun OutfitDetailScreen(
    outfit: Outfit,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showFullscreenPhoto by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image container
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .background(OotdCardBg)
                .clickable {
                    if (!outfit.imageUrl.isNullOrBlank()) {
                        showFullscreenPhoto = true
                    }
                }
        ) {
            if (!outfit.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = outfit.imageUrl,
                    contentDescription = outfit.name,
                    contentScale = ContentScale.Fit, // Full photo visible without cropping top/bottom!
                    modifier = Modifier.fillMaxSize()
                )

                // Fullscreen indicator badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OotdBlack.copy(alpha = 0.7f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.Fullscreen,
                            contentDescription = null,
                            tint = OotdWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Full Photo", color = OotdWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Top action buttons overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(OotdBlack.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OotdWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(OotdBlack.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Filled.MoreHoriz,
                        contentDescription = "More",
                        tint = OotdWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(outfit.name, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.width(10.dp))
                Tag(outfit.style)
            }
            Spacer(Modifier.height(6.dp))
            Text(
                listOfNotNull(outfit.date, outfit.weather).joinToString("   ·   "),
                color = OotdTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            if (!outfit.location.isNullOrBlank() || !outfit.eventType.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!outfit.eventType.isNullOrBlank()) {
                        Tag("🎉 ${outfit.eventType}")
                        Spacer(Modifier.width(8.dp))
                    }
                    if (!outfit.location.isNullOrBlank()) {
                        Tag("📍 ${outfit.location}")
                    }
                }
            }

            if (outfit.about != null) {
                Spacer(Modifier.height(20.dp))
                Text("About this look", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                Text(outfit.about, color = OotdTextSecondary, style = MaterialTheme.typography.bodyMedium)
            }

            // Items in this outfit
            val items = outfit.itemIds.mapNotNull { AppState.findItem(it) }
            if (items.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text("Items in this look", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items) { item ->
                        Column(
                            modifier = Modifier.width(80.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
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
                            Spacer(Modifier.height(4.dp))
                            Text(item.name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                        }
                    }
                }
            }

            if (outfit.tags.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text("Tags", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(outfit.tags) { tag -> Tag(tag) }
                }
            }

            Spacer(Modifier.height(28.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(Icons.Filled.Edit, "Edit") { onEdit() }
                ActionButton(Icons.Filled.ContentCopy, "Duplicate") { }
                ActionButton(Icons.Filled.Delete, "Delete") { onDelete() }
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    // Fullscreen Image Dialog
    if (showFullscreenPhoto && !outfit.imageUrl.isNullOrBlank()) {
        FullscreenPhotoDialog(
            imageUrl = outfit.imageUrl,
            title = outfit.name,
            onDismiss = { showFullscreenPhoto = false }
        )
    }
}

@Composable
fun FullscreenPhotoDialog(
    imageUrl: String,
    title: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    color = OotdWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(OotdWhite.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = OotdWhite)
                }
            }
        }
    }
}

@Composable
private fun Tag(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(OotdChipUnselected)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = OotdTextPrimary)
    }
}

@Composable
private fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(icon, contentDescription = label, tint = OotdTextPrimary)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = OotdTextPrimary)
    }
}
