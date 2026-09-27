package com.example.ootd.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ootd.data.ClothingItem
import com.example.ootd.data.Outfit
import com.example.ootd.data.PlannedOutfit
import com.example.ootd.data.SampleData

class OotdDatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ootd_app.db"
        private const val DATABASE_VERSION = 4

        private const val TABLE_WARDROBE = "wardrobe_items"
        private const val TABLE_OUTFITS = "outfits"
        private const val TABLE_PLANNED = "planned_outfits"

        // Common columns
        private const val COL_ID = "id"
        private const val COL_USER_ID = "user_id"
        private const val COL_NAME = "name"
        private const val COL_IMAGE_URL = "image_url"
        private const val COL_IS_FAVORITE = "is_favorite"

        // Wardrobe columns
        private const val COL_CATEGORY = "category"
        private const val COL_BRAND = "brand"
        private const val COL_COLOR_HEX = "color_hex"
        private const val COL_PURCHASE_DATE = "purchase_date"
        private const val COL_PRICE = "price"
        private const val COL_WEAR_COUNT = "wear_count"
        private const val COL_LAST_WORN_DATE = "last_worn_date"
        private const val COL_DISPOSAL_STATUS = "disposal_status"

        // Outfit columns
        private const val COL_STYLE = "style"
        private const val COL_DATE = "date"
        private const val COL_LOCATION = "location"
        private const val COL_EVENT_TYPE = "event_type"
        private const val COL_WEATHER = "weather"
        private const val COL_ABOUT = "about"
        private const val COL_TAGS = "tags"
        private const val COL_ITEM_IDS = "item_ids"

        // Planned Outfit columns
        private const val COL_OUTFIT_ID = "outfit_id"
        private const val COL_OCCASION = "occasion"
        private const val COL_IS_WORN = "is_worn"

        @Volatile
        private var INSTANCE: OotdDatabaseHelper? = null

        fun getInstance(context: Context): OotdDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: OotdDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createWardrobeTable = """
            CREATE TABLE $TABLE_WARDROBE (
                $COL_ID TEXT PRIMARY KEY,
                $COL_USER_ID INTEGER NOT NULL DEFAULT 1,
                $COL_NAME TEXT NOT NULL,
                $COL_CATEGORY TEXT NOT NULL,
                $COL_BRAND TEXT,
                $COL_COLOR_HEX TEXT,
                $COL_PURCHASE_DATE TEXT,
                $COL_PRICE TEXT,
                $COL_IMAGE_URL TEXT,
                $COL_IS_FAVORITE INTEGER NOT NULL DEFAULT 0,
                $COL_WEAR_COUNT INTEGER NOT NULL DEFAULT 0,
                $COL_LAST_WORN_DATE TEXT,
                $COL_DISPOSAL_STATUS TEXT
            )
        """.trimIndent()

        val createOutfitsTable = """
            CREATE TABLE $TABLE_OUTFITS (
                $COL_ID TEXT PRIMARY KEY,
                $COL_USER_ID INTEGER NOT NULL DEFAULT 1,
                $COL_NAME TEXT NOT NULL,
                $COL_STYLE TEXT NOT NULL,
                $COL_DATE TEXT NOT NULL,
                $COL_LOCATION TEXT,
                $COL_EVENT_TYPE TEXT,
                $COL_WEATHER TEXT,
                $COL_ABOUT TEXT,
                $COL_TAGS TEXT,
                $COL_ITEM_IDS TEXT,
                $COL_IMAGE_URL TEXT,
                $COL_IS_FAVORITE INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        val createPlannedTable = """
            CREATE TABLE $TABLE_PLANNED (
                $COL_ID TEXT PRIMARY KEY,
                $COL_USER_ID INTEGER NOT NULL DEFAULT 1,
                $COL_OUTFIT_ID TEXT NOT NULL,
                $COL_DATE TEXT NOT NULL,
                $COL_OCCASION TEXT,
                $COL_IS_WORN INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent()

        db.execSQL(createWardrobeTable)
        db.execSQL(createOutfitsTable)
        db.execSQL(createPlannedTable)

        // Seed preset data
        seedPresetData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WARDROBE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_OUTFITS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLANNED")
        onCreate(db)
    }

    private fun seedPresetData(db: SQLiteDatabase) {
        for (item in SampleData.wardrobe) {
            val cv = ContentValues().apply {
                put(COL_ID, item.id)
                put(COL_USER_ID, 1)
                put(COL_NAME, item.name)
                put(COL_CATEGORY, item.category)
                put(COL_BRAND, item.brand)
                put(COL_COLOR_HEX, item.colorHex)
                put(COL_PURCHASE_DATE, item.purchaseDate)
                put(COL_PRICE, item.price)
                put(COL_IMAGE_URL, item.imageUrl)
                put(COL_IS_FAVORITE, if (item.isFavorite) 1 else 0)
                put(COL_WEAR_COUNT, item.wearCount)
                put(COL_LAST_WORN_DATE, item.lastWornDate)
                put(COL_DISPOSAL_STATUS, item.disposalStatus)
            }
            db.insert(TABLE_WARDROBE, null, cv)
        }

        for (outfit in SampleData.outfits) {
            val cv = ContentValues().apply {
                put(COL_ID, outfit.id)
                put(COL_USER_ID, 1)
                put(COL_NAME, outfit.name)
                put(COL_STYLE, outfit.style)
                put(COL_DATE, outfit.date)
                put(COL_LOCATION, outfit.location)
                put(COL_EVENT_TYPE, outfit.eventType)
                put(COL_WEATHER, outfit.weather)
                put(COL_ABOUT, outfit.about)
                put(COL_TAGS, outfit.tags.joinToString(","))
                put(COL_ITEM_IDS, outfit.itemIds.joinToString(","))
                put(COL_IMAGE_URL, outfit.imageUrl)
                put(COL_IS_FAVORITE, if (outfit.isFavorite) 1 else 0)
            }
            db.insert(TABLE_OUTFITS, null, cv)
        }
    }

    // --- WARDROBE CRUD ---

    fun getAllWardrobeItems(userId: Long = 1): List<ClothingItem> {
        val list = mutableListOf<ClothingItem>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_WARDROBE,
            null,
            "$COL_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                val item = ClothingItem(
                    id = it.getString(it.getColumnIndexOrThrow(COL_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COL_NAME)),
                    category = it.getString(it.getColumnIndexOrThrow(COL_CATEGORY)),
                    brand = it.getString(it.getColumnIndexOrThrow(COL_BRAND)),
                    colorHex = it.getString(it.getColumnIndexOrThrow(COL_COLOR_HEX)),
                    purchaseDate = it.getString(it.getColumnIndexOrThrow(COL_PURCHASE_DATE)),
                    price = it.getString(it.getColumnIndexOrThrow(COL_PRICE)),
                    imageUrl = it.getString(it.getColumnIndexOrThrow(COL_IMAGE_URL)),
                    isFavorite = it.getInt(it.getColumnIndexOrThrow(COL_IS_FAVORITE)) == 1,
                    wearCount = it.getInt(it.getColumnIndexOrThrow(COL_WEAR_COUNT)),
                    lastWornDate = it.getString(it.getColumnIndexOrThrow(COL_LAST_WORN_DATE)),
                    disposalStatus = it.getString(it.getColumnIndexOrThrow(COL_DISPOSAL_STATUS))
                )
                list.add(item)
            }
        }
        return list
    }

    fun insertWardrobeItem(item: ClothingItem, userId: Long = 1): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ID, item.id)
            put(COL_USER_ID, userId)
            put(COL_NAME, item.name)
            put(COL_CATEGORY, item.category)
            put(COL_BRAND, item.brand)
            put(COL_COLOR_HEX, item.colorHex)
            put(COL_PURCHASE_DATE, item.purchaseDate)
            put(COL_PRICE, item.price)
            put(COL_IMAGE_URL, item.imageUrl)
            put(COL_IS_FAVORITE, if (item.isFavorite) 1 else 0)
            put(COL_WEAR_COUNT, item.wearCount)
            put(COL_LAST_WORN_DATE, item.lastWornDate)
            put(COL_DISPOSAL_STATUS, item.disposalStatus)
        }
        val row = db.insertWithOnConflict(TABLE_WARDROBE, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return row != -1L
    }

    fun updateWardrobeItem(item: ClothingItem, userId: Long = 1): Boolean {
        return insertWardrobeItem(item, userId)
    }

    fun deleteWardrobeItem(id: String): Boolean {
        val db = writableDatabase
        val count = db.delete(TABLE_WARDROBE, "$COL_ID = ?", arrayOf(id))
        return count > 0
    }

    // --- OUTFITS CRUD ---

    fun getAllOutfits(userId: Long = 1): List<Outfit> {
        val list = mutableListOf<Outfit>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_OUTFITS,
            null,
            "$COL_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                val tagsStr = it.getString(it.getColumnIndexOrThrow(COL_TAGS)) ?: ""
                val itemIdsStr = it.getString(it.getColumnIndexOrThrow(COL_ITEM_IDS)) ?: ""

                val tagsList = if (tagsStr.isNotBlank()) tagsStr.split(",") else emptyList()
                val itemIdsList = if (itemIdsStr.isNotBlank()) itemIdsStr.split(",") else emptyList()

                val outfit = Outfit(
                    id = it.getString(it.getColumnIndexOrThrow(COL_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(COL_NAME)),
                    style = it.getString(it.getColumnIndexOrThrow(COL_STYLE)),
                    date = it.getString(it.getColumnIndexOrThrow(COL_DATE)),
                    location = it.getString(it.getColumnIndexOrThrow(COL_LOCATION)),
                    eventType = it.getString(it.getColumnIndexOrThrow(COL_EVENT_TYPE)),
                    weather = it.getString(it.getColumnIndexOrThrow(COL_WEATHER)),
                    about = it.getString(it.getColumnIndexOrThrow(COL_ABOUT)),
                    tags = tagsList,
                    itemIds = itemIdsList,
                    imageUrl = it.getString(it.getColumnIndexOrThrow(COL_IMAGE_URL)),
                    isFavorite = it.getInt(it.getColumnIndexOrThrow(COL_IS_FAVORITE)) == 1
                )
                list.add(outfit)
            }
        }
        return list
    }

    fun insertOutfit(outfit: Outfit, userId: Long = 1): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ID, outfit.id)
            put(COL_USER_ID, userId)
            put(COL_NAME, outfit.name)
            put(COL_STYLE, outfit.style)
            put(COL_DATE, outfit.date)
            put(COL_LOCATION, outfit.location)
            put(COL_EVENT_TYPE, outfit.eventType)
            put(COL_WEATHER, outfit.weather)
            put(COL_ABOUT, outfit.about)
            put(COL_TAGS, outfit.tags.joinToString(","))
            put(COL_ITEM_IDS, outfit.itemIds.joinToString(","))
            put(COL_IMAGE_URL, outfit.imageUrl)
            put(COL_IS_FAVORITE, if (outfit.isFavorite) 1 else 0)
        }
        val row = db.insertWithOnConflict(TABLE_OUTFITS, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return row != -1L
    }

    fun updateOutfit(outfit: Outfit, userId: Long = 1): Boolean {
        return insertOutfit(outfit, userId)
    }

    fun deleteOutfit(id: String): Boolean {
        val db = writableDatabase
        val count = db.delete(TABLE_OUTFITS, "$COL_ID = ?", arrayOf(id))
        return count > 0
    }

    // --- PLANNED OUTFITS CRUD ---

    fun getAllPlannedOutfits(userId: Long = 1): List<PlannedOutfit> {
        val list = mutableListOf<PlannedOutfit>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_PLANNED,
            null,
            "$COL_USER_ID = ?",
            arrayOf(userId.toString()),
            null, null, null
        )

        cursor.use {
            while (it.moveToNext()) {
                val planned = PlannedOutfit(
                    id = it.getString(it.getColumnIndexOrThrow(COL_ID)),
                    outfitId = it.getString(it.getColumnIndexOrThrow(COL_OUTFIT_ID)),
                    date = it.getString(it.getColumnIndexOrThrow(COL_DATE)),
                    occasion = it.getString(it.getColumnIndexOrThrow(COL_OCCASION)),
                    isWorn = it.getInt(it.getColumnIndexOrThrow(COL_IS_WORN)) == 1
                )
                list.add(planned)
            }
        }
        return list
    }

    fun insertPlannedOutfit(planned: PlannedOutfit, userId: Long = 1): Boolean {
        val db = writableDatabase
        val cv = ContentValues().apply {
            put(COL_ID, planned.id)
            put(COL_USER_ID, userId)
            put(COL_OUTFIT_ID, planned.outfitId)
            put(COL_DATE, planned.date)
            put(COL_OCCASION, planned.occasion)
            put(COL_IS_WORN, if (planned.isWorn) 1 else 0)
        }
        val row = db.insertWithOnConflict(TABLE_PLANNED, null, cv, SQLiteDatabase.CONFLICT_REPLACE)
        return row != -1L
    }

    fun updatePlannedOutfit(planned: PlannedOutfit, userId: Long = 1): Boolean {
        return insertPlannedOutfit(planned, userId)
    }

    fun deletePlannedOutfit(id: String): Boolean {
        val db = writableDatabase
        val count = db.delete(TABLE_PLANNED, "$COL_ID = ?", arrayOf(id))
        return count > 0
    }
}
