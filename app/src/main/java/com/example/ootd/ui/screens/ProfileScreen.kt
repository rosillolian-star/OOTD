package com.example.ootd.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ootd.data.AppState
import com.example.ootd.ui.theme.*

private data class ProfileMenuItem(val icon: ImageVector, val label: String)

private val menuItemsTop = listOf(
    ProfileMenuItem(Icons.Outlined.Checkroom, "My Wardrobe"),
    ProfileMenuItem(Icons.Outlined.History, "Outfit History"),
    ProfileMenuItem(Icons.Outlined.FavoriteBorder, "Favorites"),
    ProfileMenuItem(Icons.Outlined.DeleteSweep, "Disposal Suggestions"),
    ProfileMenuItem(Icons.Outlined.Category, "Categories"),
    ProfileMenuItem(Icons.Outlined.Palette, "Style Preferences"),
    ProfileMenuItem(Icons.Outlined.NotificationsNone, "Reminders")
)

private val menuItemsBottom = listOf(
    ProfileMenuItem(Icons.Outlined.HelpOutline, "Help & Support"),
    ProfileMenuItem(Icons.Outlined.Info, "About"),
    ProfileMenuItem(Icons.Outlined.Logout, "Log Out")
)

@Composable
fun ProfileScreen(onMenuItemClick: (String) -> Unit) {
    var showEditProfileModal by remember { mutableStateOf(false) }

    // Launcher to pick custom profile picture from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            AppState.profilePictureUri.value = it.toString()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OotdCream)
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(20.dp))

        // Profile Header with 1x1 Square Avatar & Edit Profile Action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Fully Rounded Circular Profile Frame
                Surface(
                    shape = CircleShape,
                    shadowElevation = 6.dp,
                    color = OotdOffBlack,
                    modifier = Modifier.size(72.dp)
                ) {
                    if (!AppState.profilePictureUri.value.isNullOrBlank()) {
                        AsyncImage(
                            model = AppState.profilePictureUri.value,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                tint = OotdWhite,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                Column {
                    Text(AppState.userName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(AppState.userHandle, color = OotdTextSecondary, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Edit Profile Button
            OutlinedButton(
                onClick = { showEditProfileModal = true },
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, OotdBlack),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, tint = OotdBlack, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(4.dp))
                Text("Edit", color = OotdBlack, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(Modifier.height(20.dp))

        // Floating Stats Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            color = OotdWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(AppState.outfits.size.toString(), "Outfits")
                StatColumn(AppState.wardrobe.size.toString(), "Items")
                StatColumn(AppState.styleCount.toString(), "Styles")
            }
        }

        Spacer(Modifier.height(20.dp))

        // Floating Top Menu Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            color = OotdWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                menuItemsTop.forEach { item -> MenuRow(item) { onMenuItemClick(item.label) } }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Floating Bottom Menu Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 6.dp,
            color = OotdWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                menuItemsBottom.forEach { item -> MenuRow(item) { onMenuItemClick(item.label) } }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // Edit Profile Modal Dialog
    if (showEditProfileModal) {
        EditProfileModalDialog(
            onChangePhoto = { photoPickerLauncher.launch("image/*") },
            onDismiss = { showEditProfileModal = false },
            onLogOut = {
                showEditProfileModal = false
                onMenuItemClick("Log Out")
            }
        )
    }
}

@Composable
private fun EditProfileModalDialog(
    onChangePhoto: () -> Unit,
    onDismiss: () -> Unit,
    onLogOut: () -> Unit
) {
    var editName by remember { mutableStateOf(AppState.userName) }
    var editHandle by remember { mutableStateOf(AppState.userHandle) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = OotdWhite,
            shadowElevation = 10.dp,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Profile", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = OotdTextPrimary)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Change Photo Button
                OutlinedButton(
                    onClick = onChangePhoto,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, OotdBlack),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = OotdBlack, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Change Profile Picture", color = OotdBlack, fontSize = 13.sp)
                }

                Spacer(Modifier.height(16.dp))

                Text("Display Name", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Text("Username Handle", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = editHandle,
                    onValueChange = { editHandle = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        AppState.updateUserProfile(editName, editHandle)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = OotdBlack, contentColor = OotdWhite),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Save Changes", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(10.dp))

                TextButton(
                    onClick = onLogOut,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Out of Account", color = Color(0xFFC5221F), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        Text(label, color = OotdTextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun MenuRow(item: ProfileMenuItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(item.icon, contentDescription = null, tint = OotdTextPrimary)
            Spacer(Modifier.width(14.dp))
            Text(item.label, style = MaterialTheme.typography.bodyLarge)
        }
        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = OotdTextSecondary)
    }
}
