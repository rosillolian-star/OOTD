package com.example.ootd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ootd.data.AppState
import com.example.ootd.data.ClothingItem
import com.example.ootd.ui.components.OotdImagePicker
import com.example.ootd.ui.theme.*

private val swatches = listOf(
    Color(0xFF1A1A1A), Color(0xFF7A7A7A), Color(0xFFD9C7B0),
    Color(0xFF3B5A8C), Color(0xFF1F2A24), Color(0xFF4C7A4E)
)

@Composable
fun AddItemScreen(
    existingItem: ClothingItem? = null,
    onBack: () -> Unit,
    onSave: (ClothingItem) -> Unit
) {
    var itemName by remember { mutableStateOf(existingItem?.name ?: "") }
    var category by remember { mutableStateOf(existingItem?.category ?: "Tops") }
    var brand by remember { mutableStateOf(existingItem?.brand ?: "") }
    var purchaseDate by remember { mutableStateOf(existingItem?.purchaseDate ?: "") }
    var price by remember { mutableStateOf(existingItem?.price ?: "") }
    var imageUrl by remember { mutableStateOf(existingItem?.imageUrl ?: "") }
    var selectedColor by remember { mutableStateOf(0) }
    val isEditing = existingItem != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { onBack() }
            )
            Spacer(Modifier.width(16.dp))
            Text(if (isEditing) "Edit Item" else "Add Item", style = MaterialTheme.typography.titleLarge)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Photo picker (Camera, Gallery, or Web URL)
            OotdImagePicker(
                imageUrlOrUri = imageUrl,
                onImageSelected = { imageUrl = it },
                title = "Item Photo"
            )

            Spacer(Modifier.height(20.dp))
            FormLabel("Item Name")
            OotdTextField(value = itemName, onValueChange = { itemName = it }, placeholder = "e.g. Black Blazer")

            Spacer(Modifier.height(16.dp))
            FormLabel("Category")
            CategoryDropdown(selected = category, onSelected = { category = it })

            Spacer(Modifier.height(16.dp))
            FormLabel("Brand")
            OotdTextField(value = brand, onValueChange = { brand = it }, placeholder = "e.g. Zara (optional)")

            Spacer(Modifier.height(16.dp))
            FormLabel("Color")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                swatches.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (index == selectedColor)
                                    Modifier.border(2.dp, OotdBlack, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColor = index }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            FormLabel("Purchase Date")
            OotdTextField(
                value = purchaseDate,
                onValueChange = { purchaseDate = it },
                placeholder = "Select date",
                trailingIcon = Icons.Filled.CalendarToday
            )

            Spacer(Modifier.height(16.dp))
            FormLabel("Price (optional)")
            OotdTextField(value = price, onValueChange = { price = it }, placeholder = "e.g. ₱2,500")

            Spacer(Modifier.height(28.dp))
            Button(
                onClick = {
                    val item = ClothingItem(
                        id = existingItem?.id ?: AppState.newId(),
                        name = itemName.ifBlank { "Untitled Item" },
                        category = category,
                        brand = brand.ifBlank { null },
                        purchaseDate = purchaseDate.ifBlank { null },
                        price = price.ifBlank { null },
                        imageUrl = imageUrl.ifBlank { null }
                    )
                    onSave(item)
                },
                colors = ButtonDefaults.buttonColors(containerColor = OotdBlack, contentColor = OotdWhite),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (isEditing) "Update Item" else "Save Item", fontSize = 16.sp)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FormLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = OotdTextPrimary)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun OotdTextField(
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryDropdown(selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Tops", "Bottoms", "Outerwear", "Shoes", "Accessories")

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
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
