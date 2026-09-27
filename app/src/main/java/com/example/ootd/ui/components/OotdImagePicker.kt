package com.example.ootd.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.ootd.ui.theme.*
import com.example.ootd.utils.FashionImageVerifier
import java.io.File

@Composable
fun OotdImagePicker(
    imageUrlOrUri: String,
    onImageSelected: (String) -> Unit,
    title: String = "Outfit Photo"
) {
    val context = LocalContext.current

    var isVerifying by remember { mutableStateOf(false) }

    // Camera Preview launcher with ML Kit Fashion Verification
    val cameraPreviewLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            isVerifying = true
            FashionImageVerifier.verifyFashionBitmap(bitmap) { isFashion, detectedLabel ->
                isVerifying = false
                if (isFashion) {
                    try {
                        val savedFile = saveBitmapToCache(context, bitmap)
                        onImageSelected(Uri.fromFile(savedFile).toString())
                        val labelText = detectedLabel?.let { " ($it)" } ?: ""
                        Toast.makeText(context, "Outfit detected$labelText", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to save captured photo", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(
                        context,
                        "No clothing or outfit detected. Please capture a picture with clothing/outfit.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    // Permission launcher for Camera
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraPreviewLauncher.launch(null)
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery photo launcher with ML Kit Fashion Verification
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            isVerifying = true
            FashionImageVerifier.verifyFashionImage(context, selectedUri) { isFashion, detectedLabel ->
                isVerifying = false
                if (isFashion) {
                    onImageSelected(selectedUri.toString())
                    val labelText = detectedLabel?.let { " ($it)" } ?: ""
                    Toast.makeText(context, "Outfit detected$labelText", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        context,
                        "No clothing or outfit detected in selected photo. Please choose a fashion/outfit photo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun launchCamera() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            cameraPreviewLauncher.launch(null)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = OotdTextPrimary)

            if (imageUrlOrUri.isNotBlank()) {
                Text(
                    text = "Remove",
                    color = Color(0xFFC5221F),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.clickable { onImageSelected("") }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(OotdWhite)
                .border(1.dp, OotdLightGray, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (isVerifying) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = OotdBlack, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.height(10.dp))
                    Text("Analyzing outfit...", style = MaterialTheme.typography.bodyMedium, color = OotdTextSecondary)
                }
            } else if (imageUrlOrUri.isNotBlank()) {
                // Display selected photo preview
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = imageUrlOrUri,
                        contentDescription = "Selected Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay clear button
                    IconButton(
                        onClick = { onImageSelected("") },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(OotdBlack.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Clear", tint = OotdWhite, modifier = Modifier.size(18.dp))
                    }
                }
            } else {
                // Choice buttons: Camera vs Gallery
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Add photo of your look", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Spacer(Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Camera Button
                        Button(
                            onClick = { launchCamera() },
                            colors = ButtonDefaults.buttonColors(containerColor = OotdBlack, contentColor = OotdWhite),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Camera", fontSize = 13.sp)
                        }

                        // Gallery Button
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, OotdBlack),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, contentDescription = null, tint = OotdBlack, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Gallery", color = OotdBlack, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
    val imageDir = File(context.cacheDir, "images").apply { mkdirs() }
    val file = File(imageDir, "OOTD_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return file
}
