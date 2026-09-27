package com.example.ootd.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.data.Outfit
import com.example.ootd.ui.theme.*

private val weekDayHeaders = listOf("S", "M", "T", "W", "T", "F", "S")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    onOutfitClick: (Outfit) -> Unit
) {
    // Current month state (August 2025 as demo baseline)
    var currentMonthIndex by remember { mutableStateOf(7) } // 7 = August (0-based)
    var currentYear by remember { mutableStateOf(2025) }
    var selectedDay by remember { mutableStateOf(13) }

    var showOutfitPicker by remember { mutableStateOf(false) }

    val monthNames = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val currentMonthName = monthNames[currentMonthIndex]

    val daysInMonth = when (currentMonthIndex) {
        1 -> if (currentYear % 4 == 0) 29 else 28
        3, 5, 8, 10 -> 30
        else -> 31
    }

    // Date key string format: "YYYY-MM-DD" e.g. "2025-08-13"
    fun dateKey(day: Int): String {
        val m = (currentMonthIndex + 1).toString().padStart(2, '0')
        val d = day.toString().padStart(2, '0')
        return "$currentYear-$m-$d"
    }

    val selectedDateKey = dateKey(selectedDay)
    val plannedForSelectedDay = AppState.getPlannedOutfitForDate(selectedDateKey)
    val outfitForSelectedDay = plannedForSelectedDay?.let { AppState.findOutfit(it.outfitId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(Modifier.height(20.dp))

        // Screen Header
        Text(
            text = "OOTD Planner",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(16.dp))

        // Month Navigation Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(OotdWhite)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (currentMonthIndex > 0) {
                        currentMonthIndex--
                    } else {
                        currentMonthIndex = 11
                        currentYear--
                    }
                }
            ) {
                Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous Month", tint = OotdTextPrimary)
            }

            Text(
                text = "$currentMonthName $currentYear",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = OotdTextPrimary
            )

            IconButton(
                onClick = {
                    if (currentMonthIndex < 11) {
                        currentMonthIndex++
                    } else {
                        currentMonthIndex = 0
                        currentYear++
                    }
                }
            ) {
                Icon(Icons.Filled.ChevronRight, contentDescription = "Next Month", tint = OotdTextPrimary)
            }
        }

        Spacer(Modifier.height(12.dp))

        // Calendar Grid Container
        Surface(
            color = OotdWhite,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Week day headers (S M T W T F S)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    weekDayHeaders.forEach { dayHeader ->
                        Text(
                            text = dayHeader,
                            fontWeight = FontWeight.SemiBold,
                            color = OotdTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Days Grid (1..daysInMonth)
                val daysList = (1..daysInMonth).toList()
                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                ) {
                    items(daysList) { day ->
                        val dayKey = dateKey(day)
                        val isSelected = (day == selectedDay)
                        val isToday = (day == 13 && currentMonthIndex == 7 && currentYear == 2025)
                        val hasPlan = AppState.getPlannedOutfitForDate(dayKey) != null

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isToday -> OotdBlack
                                        else -> Color.Transparent
                                    }
                                )
                                .then(
                                    if (isSelected && !isToday)
                                        Modifier.border(2.dp, OotdBlack, CircleShape)
                                    else Modifier
                                )
                                .clickable { selectedDay = day },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 13.sp,
                                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isToday) OotdWhite else OotdTextPrimary
                                )
                                if (hasPlan) {
                                    Spacer(Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isToday) OotdWhite else Color(0xFF4CAF50))
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Selected Day Schedule Card Section
        Text(
            text = "$currentMonthName $selectedDay, $currentYear",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(10.dp))

        if (plannedForSelectedDay != null && outfitForSelectedDay != null) {
            // Planned Outfit Card
            Surface(
                color = OotdWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOutfitClick(outfitForSelectedDay) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OotdCardBg)
                        ) {
                            if (!outfitForSelectedDay.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = outfitForSelectedDay.imageUrl,
                                    contentDescription = outfitForSelectedDay.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        Spacer(Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                outfitForSelectedDay.name,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "Style: ${outfitForSelectedDay.style}",
                                color = OotdTextSecondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                            if (!plannedForSelectedDay.occasion.isNullOrBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Occasion: ${plannedForSelectedDay.occasion}",
                                    color = OotdBlack,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Worn Badge
                        if (plannedForSelectedDay.isWorn) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text("Worn", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { AppState.togglePlannedOutfitWorn(selectedDateKey) },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(
                                1.dp,
                                if (plannedForSelectedDay.isWorn) OotdGray else OotdBlack
                            )
                        ) {
                            Text(
                                if (plannedForSelectedDay.isWorn) "Mark Unworn" else "Mark Worn Today",
                                color = OotdBlack,
                                fontSize = 12.sp
                            )
                        }

                        Row {
                            IconButton(onClick = { showOutfitPicker = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Change", tint = OotdTextPrimary)
                            }
                            IconButton(onClick = { AppState.removePlannedOutfitForDate(selectedDateKey) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color(0xFFC5221F))
                            }
                        }
                    }
                }
            }
        } else {
            // Empty state for selected date
            Surface(
                color = OotdWhite,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No outfit scheduled for $currentMonthName $selectedDay",
                        color = OotdTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(Modifier.height(14.dp))

                    Button(
                        onClick = { showOutfitPicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = OotdBlack, contentColor = OotdWhite),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Plan Outfit for $currentMonthName $selectedDay")
                    }
                }
            }
        }
    }

    // Outfit Selection Sheet / Dialog
    if (showOutfitPicker) {
        AlertDialog(
            onDismissRequest = { showOutfitPicker = false },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showOutfitPicker = false }) {
                    Text("Cancel", color = OotdTextPrimary)
                }
            },
            title = {
                Text("Select Outfit for $currentMonthName $selectedDay", style = MaterialTheme.typography.titleMedium)
            },
            text = {
                if (AppState.outfits.isEmpty()) {
                    Text("No saved outfits available. Please create an outfit first.", color = OotdTextSecondary)
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(260.dp)
                    ) {
                        items(AppState.outfits) { outfit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(OotdCardBg)
                                    .clickable {
                                        AppState.planOutfitForDate(selectedDateKey, outfit.id)
                                        showOutfitPicker = false
                                    }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OotdWhite)
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
                                Column {
                                    Text(outfit.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    Text(outfit.style, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = OotdWhite
        )
    }
}
