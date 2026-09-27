package com.example.ootd.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.ClothingItem
import com.example.ootd.data.Outfit
import com.example.ootd.ui.components.OotdImagePicker
import com.example.ootd.ui.theme.*

@Composable
fun CreateOutfitScreen(
    existingOutfit: Outfit? = null,
    onBack: () -> Unit,
    onSave: (Outfit) -> Unit,
    onAddFromWardrobe: () -> Unit
) {
    var name by remember { mutableStateOf(existingOutfit?.name ?: "") }
    var style by remember { mutableStateOf(existingOutfit?.style ?: "Casual") }
    var location by remember { mutableStateOf(existingOutfit?.location ?: "") }
    var eventType by remember { mutableStateOf(existingOutfit?.eventType ?: "Casual Outing") }
    var date by remember { mutableStateOf(existingOutfit?.date ?: "") }
    var notes by remember { mutableStateOf(existingOutfit?.about ?: "") }
    var imageUrl by remember { mutableStateOf(existingOutfit?.imageUrl ?: "") }
    val isEditing = existingOutfit != null

    // Seed the shared "items in this outfit" selection once when this screen is first entered
    LaunchedEffect(existingOutfit?.id) {
        AppState.pendingOutfitItemIds.clear()
        existingOutfit?.let { AppState.pendingOutfitItemIds.addAll(it.itemIds) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { onBack() })
            Spacer(Modifier.width(16.dp))
            Text(if (isEditing) "Edit Outfit" else "Create Outfit", style = MaterialTheme.typography.titleLarge)
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Photo capture / Picker
            OotdImagePicker(
                imageUrlOrUri = imageUrl,
                onImageSelected = { imageUrl = it },
                title = "Outfit Photo"
            )

            val selectedItems = AppState.pendingOutfitItemIds.mapNotNull { AppState.findItem(it) }

            if (selectedItems.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                SelectedItemsPicker(
                    outfitItems = selectedItems,
                    onRemove = { itemId -> AppState.pendingOutfitItemIds.remove(itemId) },
                    onAddMore = onAddFromWardrobe
                )
            }

            Spacer(Modifier.height(20.dp))
            Text("Outfit Details", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            FormLabelSimple("Name")
            SimpleField(name, { name = it }, "e.g. Weekend Vibes")

            Spacer(Modifier.height(16.dp))
            FormLabelSimple("Location / Venue")
            SimpleField(location, { location = it }, "e.g. BGC High Street, Makati", trailingIcon = Icons.Filled.LocationOn)

            Spacer(Modifier.height(16.dp))
            FormLabelSimple("Event / Occasion")
            EventDropdown(selected = eventType, onSelected = { eventType = it })

            Spacer(Modifier.height(16.dp))
            FormLabelSimple("Style")
            StyleDropdown(selected = style, onSelected = { style = it })

            Spacer(Modifier.height(16.dp))
            FormLabelSimple("Date")
            SimpleField(date, { date = it }, "Aug 13, 2025", trailingIcon = Icons.Filled.CalendarToday)

            Spacer(Modifier.height(16.dp))
            FormLabelSimple("Notes (optional)")
            SimpleField(notes, { notes = it }, "How did it feel? Where did you wear it?")
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Button(
                onClick = {
                    val outfit = Outfit(
                        id = existingOutfit?.id ?: AppState.newId(),
                        name = name.ifBlank { "Untitled Outfit" },
                        style = style,
                        location = location.ifBlank { null },
                        eventType = eventType.ifBlank { null },
                        date = date.ifBlank { "Today" },
                        about = notes.ifBlank { null },
                        tags = existingOutfit?.tags ?: emptyList(),
                        itemIds = AppState.pendingOutfitItemIds.toList(),
                        imageUrl = imageUrl.ifBlank { null }
                    )
                    onSave(outfit)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OotdBlack, contentColor = OotdWhite),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (isEditing) "Update Outfit" else "Save Outfit", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun SelectedItemsPicker(
    outfitItems: List<ClothingItem>,
    onRemove: (String) -> Unit,
    onAddMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OotdWhite)
            .border(BorderStroke(1.dp, OotdLightGray), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text("${outfitItems.size} item${if (outfitItems.size == 1) "" else "s"} added", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(outfitItems) { item ->
                Column(
                    modifier = Modifier.width(84.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
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
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(20.dp)
                                .clip(RoundedCornerShape(50))
                                .background(OotdBlack)
                                .clickable { onRemove(item.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Remove ${item.name}",
                                tint = OotdWhite,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(item.name, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = onAddMore,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OotdBlack),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add more from Wardrobe", color = OotdBlack)
        }
    }
}

@Composable
private fun FormLabelSimple(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun SimpleField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    trailingIcon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = OotdTextSecondary) },
        trailingIcon = trailingIcon?.let { { Icon(it, contentDescription = null, tint = OotdTextSecondary) } },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = OotdWhite,
            unfocusedContainerColor = OotdWhite,
            focusedBorderColor = OotdBlack,
            unfocusedBorderColor = OotdLightGray
        ),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(4.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StyleDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Formal", "Casual", "Streetwear", "Sport", "Evening")

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = OotdWhite,
                unfocusedContainerColor = OotdWhite,
                focusedBorderColor = OotdBlack,
                unfocusedBorderColor = OotdLightGray
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Work Meeting", "Casual Outing", "Dinner Date", "Party / Celebration", "Workout", "Travel", "Wedding / Gala")

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = OotdWhite,
                unfocusedContainerColor = OotdWhite,
                focusedBorderColor = OotdBlack,
                unfocusedBorderColor = OotdLightGray
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}
