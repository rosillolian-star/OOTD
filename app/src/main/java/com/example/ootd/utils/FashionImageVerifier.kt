package com.example.ootd.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

object FashionImageVerifier {
    private val labeler by lazy {
        ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    }

    fun verifyFashionImage(
        context: Context,
        uri: Uri,
        onResult: (isFashion: Boolean, detectedLabel: String?) -> Unit
    ) {
        try {
            val image = InputImage.fromFilePath(context, uri)
            processImage(image, onResult)
        } catch (e: Exception) {
            // Fallback gracefully if image cannot be parsed
            onResult(true, "Clothing")
        }
    }

    fun verifyFashionBitmap(
        bitmap: Bitmap,
        onResult: (isFashion: Boolean, detectedLabel: String?) -> Unit
    ) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            processImage(image, onResult)
        } catch (e: Exception) {
            onResult(true, "Clothing")
        }
    }

    private fun processImage(
        image: InputImage,
        onResult: (isFashion: Boolean, detectedLabel: String?) -> Unit
    ) {
        labeler.process(image)
            .addOnSuccessListener { labels ->
                val matchingLabel = labels.firstOrNull { label ->
                    isFashionRelated(label.text) && label.confidence >= 0.40f
                }
                if (matchingLabel != null) {
                    onResult(true, matchingLabel.text)
                } else {
                    onResult(false, null)
                }
            }
            .addOnFailureListener {
                // If processing fails, allow photo so user isn't blocked
                onResult(true, "Clothing")
            }
    }

    private fun isFashionRelated(labelText: String): Boolean {
        val text = labelText.lowercase()
        return fashionKeywords.any { text.contains(it) }
    }

    private val fashionKeywords = setOf(
        "clothing", "apparel", "shirt", "pant", "trouser", "jean", "dress", "skirt",
        "coat", "jacket", "suit", "blazer", "outerwear", "top", "footwear", "shoe",
        "sneaker", "boot", "heel", "sandal", "bag", "handbag", "backpack", "pocket",
        "sleeve", "collar", "accessory", "fashion", "model", "person", "human",
        "outfit", "costume", "t-shirt", "activewear", "sportswear", "shorts",
        "sweatshirt", "sweater", "hoodie", "vest", "textile", "style", "belt",
        "hat", "cap", "glasses", "sunglasses", "watch", "jewelry", "necktie", "scarf", "denim"
    )
}
