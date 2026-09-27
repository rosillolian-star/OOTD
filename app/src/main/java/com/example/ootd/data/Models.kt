package com.example.ootd.data

data class User(
    val id: Long = 0,
    val fullName: String,
    val username: String,
    val email: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class ClothingItem(
    val id: String,
    val name: String,
    val category: String, // Tops, Bottoms, Outerwear, Shoes, Accessories
    val brand: String? = null,
    val colorHex: String? = null,
    val purchaseDate: String? = null,
    val price: String? = null,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false,
    val wearCount: Int = 0,
    val daysUnworn: Int = 0, // Days elapsed since last worn
    val lastWornDate: String? = null,
    val disposalStatus: String? = null // null, "Disposal"
) {
    // Calculate Cost-Per-Wear (CPW) in pesos or raw currency
    fun costPerWear(): Double? {
        if (price.isNullOrBlank()) return null
        val cleanPrice = price.replace("[^0-9.]".toRegex(), "").toDoubleOrNull() ?: return null
        val count = if (wearCount <= 0) 1 else wearCount
        return cleanPrice / count
    }
}

data class Outfit(
    val id: String,
    val name: String,
    val style: String, // Formal, Casual, Streetwear, Sport, Evening
    val date: String,
    val location: String? = null, // e.g. "BGC High Street"
    val eventType: String? = null, // e.g. "Work Meeting", "Dinner Date", "Casual Outing"
    val weather: String? = null,
    val about: String? = null,
    val tags: List<String> = emptyList(),
    val itemIds: List<String> = emptyList(),
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)

data class PlannedOutfit(
    val id: String,
    val outfitId: String,
    val date: String, // Format: YYYY-MM-DD e.g. "2025-08-13"
    val occasion: String? = null,
    val isWorn: Boolean = false
)

object SampleData {
    val wardrobe = listOf(
        ClothingItem(
            id = "1",
            name = "White Shirt",
            category = "Tops",
            brand = "Uniqlo",
            price = "₱1,290",
            wearCount = 12,
            daysUnworn = 2,
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=600&q=80"
        ),
        ClothingItem(
            id = "2",
            name = "Black Blazer",
            category = "Outerwear",
            brand = "Zara",
            price = "₱3,490",
            wearCount = 8,
            daysUnworn = 4,
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&q=80"
        ),
        ClothingItem(
            id = "3",
            name = "Wide Leg Pants",
            category = "Bottoms",
            brand = "Mango",
            price = "₱2,290",
            wearCount = 6,
            daysUnworn = 9,
            imageUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=600&q=80"
        ),
        ClothingItem(
            id = "4",
            name = "Graphic Tee",
            category = "Tops",
            brand = "H&M",
            price = "₱890",
            wearCount = 15,
            daysUnworn = 14,
            imageUrl = "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=600&q=80"
        ),
        ClothingItem(
            id = "5",
            name = "Sneakers",
            category = "Shoes",
            brand = "Nike",
            price = "₱4,500",
            wearCount = 20,
            daysUnworn = 35,
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-1552346154-21d32810aba3?w=600&q=80"
        ),
        ClothingItem(
            id = "6",
            name = "Leather Bag",
            category = "Accessories",
            brand = "Charles & Keith",
            price = "₱3,800",
            wearCount = 1,
            daysUnworn = 95,
            imageUrl = "https://images.unsplash.com/photo-1584917865442-de89df76afd3?w=600&q=80"
        ),
        ClothingItem(
            id = "7",
            name = "Floral Summer Dress",
            category = "Dresses",
            brand = "Shein",
            price = "₱1,890",
            wearCount = 0,
            daysUnworn = 120, // Unworn 120 days -> Disposal candidate
            disposalStatus = "Disposal",
            imageUrl = "https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=600&q=80"
        )
    )

    val outfits = listOf(
        Outfit(
            id = "o1",
            name = "Office Ready",
            style = "Formal",
            date = "Aug 12, 2025",
            location = "Makati Financial Tower",
            eventType = "Work Meeting",
            weather = "28°C · Partly Cloudy",
            about = "Wore this for my executive presentation. Tailored black blazer with crisp white inner shirt.",
            tags = listOf("formal", "neutral", "work", "blazer"),
            itemIds = listOf("2", "1", "3", "5", "6"),
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-1487222477894-8943e31ef7b2?w=800&q=80"
        ),
        Outfit(
            id = "o2",
            name = "Casual Day",
            style = "Casual",
            date = "Aug 10, 2025",
            location = "BGC High Street",
            eventType = "Casual Outing",
            weather = "30°C · Sunny",
            about = "Relaxed weekend style with graphic tee and breathable wide trousers.",
            tags = listOf("casual", "yellow", "streetwear", "comfort"),
            itemIds = listOf("4", "3", "5"),
            imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800&q=80"
        ),
        Outfit(
            id = "o3",
            name = "Street Vibes",
            style = "Streetwear",
            date = "Aug 7, 2025",
            location = "Poblacion Night Market",
            eventType = "Casual Outing",
            weather = "26°C · Overcast",
            about = "Urban street aesthetic with relaxed dark trousers and clean sneakers.",
            tags = listOf("streetwear", "urban", "dark", "sneakers"),
            itemIds = listOf("4", "3"),
            imageUrl = "https://images.unsplash.com/photo-1509631179647-0177331693ae?w=800&q=80"
        ),
        Outfit(
            id = "o4",
            name = "Date Night",
            style = "Evening",
            date = "Aug 5, 2025",
            location = "Blackbird Makati",
            eventType = "Dinner Date",
            weather = "24°C · Breeze",
            about = "Chic evening dress paired with statement leather bag for dinner date.",
            tags = listOf("evening", "elegant", "dress", "date"),
            itemIds = listOf("2", "1"),
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-1496747611176-843222e1e57c?w=800&q=80"
        ),
        Outfit(
            id = "o5",
            name = "Gym Fit",
            style = "Sport",
            date = "Aug 2, 2025",
            location = "Fitness First Gym",
            eventType = "Workout",
            weather = "29°C · Clear",
            about = "Breathable athletic activewear for morning workout and running.",
            tags = listOf("sport", "workout", "activewear", "gym"),
            itemIds = listOf("4", "5"),
            imageUrl = "https://images.unsplash.com/photo-1518310383802-640c2de311b2?w=800&q=80"
        ),
        Outfit(
            id = "o6",
            name = "Monochrome Minimal",
            style = "Streetwear",
            date = "Jul 29, 2025",
            location = "Ayala Museum",
            eventType = "Casual Outing",
            weather = "25°C · Mild",
            about = "Black-on-black minimalist palette for art gallery visits.",
            tags = listOf("monochrome", "minimal", "black", "chic"),
            itemIds = listOf("1", "3", "5"),
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=800&q=80"
        ),
        Outfit(
            id = "o7",
            name = "Summer Resort",
            style = "Casual",
            date = "Jul 25, 2025",
            location = "Boracay Beach",
            eventType = "Travel",
            weather = "32°C · Very Hot",
            about = "Light linen shirt paired with airy shorts for beach weekend trip.",
            tags = listOf("summer", "resort", "linen", "vacation"),
            itemIds = listOf("1", "5"),
            imageUrl = "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=800&q=80"
        ),
        Outfit(
            id = "o8",
            name = "Smart Executive",
            style = "Formal",
            date = "Jul 20, 2025",
            location = "Grand Hyatt Ballroom",
            eventType = "Work Meeting",
            weather = "27°C · Indoor AC",
            about = "Tailored suit jacket with structured trousers for board meetings.",
            tags = listOf("executive", "formal", "suit", "business"),
            itemIds = listOf("2", "3", "6"),
            isFavorite = true,
            imageUrl = "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=800&q=80"
        )
    )

    const val userName = "Juan Delacruz"
    const val userHandle = "@juandelacruz"
    const val outfitCount = 12
    const val itemCount = 48
    const val styleCount = 5
}
