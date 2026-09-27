package com.example.ootd.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.ootd.data.db.OotdDatabaseHelper
import com.example.ootd.data.db.SessionManager
import java.util.UUID

/**
 * Shared state store for wardrobe, outfits, planned outfits, and active user session, backed by SQLite & SharedPreferences.
 */
object AppState {
    private var dbHelper: OotdDatabaseHelper? = null
    private var sessionManager: SessionManager? = null

    val wardrobe = mutableStateListOf<ClothingItem>().apply { addAll(SampleData.wardrobe) }
    val outfits = mutableStateListOf<Outfit>().apply { addAll(SampleData.outfits) }
    val plannedOutfits = mutableStateListOf<PlannedOutfit>()

    val currentUser = mutableStateOf<User?>(null)
    val profilePictureUri = mutableStateOf<String?>(null)

    fun init(context: Context) {
        if (dbHelper == null) {
            val appContext = context.applicationContext
            dbHelper = OotdDatabaseHelper.getInstance(appContext)
            sessionManager = SessionManager(appContext)

            // Restore persistent user session if logged in
            val savedUser = sessionManager?.getSavedUser()
            currentUser.value = savedUser

            refreshFromDatabase()
        }
    }

    private fun currentUserId(): Long = currentUser.value?.id ?: 1L

    fun refreshFromDatabase() {
        val helper = dbHelper ?: return
        val uId = currentUserId()

        val dbItems = helper.getAllWardrobeItems(uId)
        wardrobe.clear()
        if (dbItems.isNotEmpty()) {
            wardrobe.addAll(dbItems)
        } else {
            wardrobe.addAll(SampleData.wardrobe)
        }

        val dbOutfits = helper.getAllOutfits(uId)
        outfits.clear()
        if (dbOutfits.isNotEmpty()) {
            outfits.addAll(dbOutfits)
        } else {
            outfits.addAll(SampleData.outfits)
        }

        val dbPlanned = helper.getAllPlannedOutfits(uId)
        plannedOutfits.clear()
        plannedOutfits.addAll(dbPlanned)
    }

    val userName: String
        get() = currentUser.value?.fullName ?: SampleData.userName

    val userHandle: String
        get() = currentUser.value?.let { "@${it.username.removePrefix("@")}" } ?: SampleData.userHandle

    fun updateUserProfile(newName: String, newHandle: String, newProfilePic: String? = profilePictureUri.value) {
        val cleanHandle = if (newHandle.startsWith("@")) newHandle else "@$newHandle"
        val current = currentUser.value
        val userToSave = current?.copy(fullName = newName, username = cleanHandle)
            ?: User(id = 1, fullName = newName, username = cleanHandle, email = "user@ootd.app")

        currentUser.value = userToSave
        sessionManager?.saveUserSession(userToSave)
        profilePictureUri.value = newProfilePic
    }

    fun login(user: User) {
        currentUser.value = user
        sessionManager?.saveUserSession(user)
        refreshFromDatabase()
    }

    fun logout() {
        currentUser.value = null
        sessionManager?.clearSession()
        refreshFromDatabase()
    }

    // Shared list for items selected during outfit creation/edit
    val pendingOutfitItemIds = mutableStateListOf<String>()

    const val styleCount = 5

    fun findItem(id: String?): ClothingItem? = wardrobe.find { it.id == id }
    fun findOutfit(id: String?): Outfit? = outfits.find { it.id == id }

    fun addItem(item: ClothingItem) {
        wardrobe.add(item)
        dbHelper?.insertWardrobeItem(item, currentUserId())
    }

    fun updateItem(item: ClothingItem) {
        val index = wardrobe.indexOfFirst { it.id == item.id }
        if (index != -1) {
            wardrobe[index] = item
            dbHelper?.updateWardrobeItem(item, currentUserId())
        }
    }

    fun toggleItemFavorite(item: ClothingItem) {
        val updated = item.copy(isFavorite = !item.isFavorite)
        updateItem(updated)
    }

    fun updateItemDisposalStatus(item: ClothingItem, newStatus: String?) {
        val updated = item.copy(disposalStatus = newStatus)
        updateItem(updated)
    }

    fun addOutfit(outfit: Outfit) {
        outfits.add(outfit)
        dbHelper?.insertOutfit(outfit, currentUserId())
    }

    fun updateOutfit(outfit: Outfit) {
        val index = outfits.indexOfFirst { it.id == outfit.id }
        if (index != -1) {
            outfits[index] = outfit
            dbHelper?.updateOutfit(outfit, currentUserId())
        }
    }

    fun deleteOutfit(id: String) {
        outfits.removeAll { it.id == id }
        dbHelper?.deleteOutfit(id)
    }

    fun toggleOutfitFavorite(outfit: Outfit) {
        val updated = outfit.copy(isFavorite = !outfit.isFavorite)
        updateOutfit(updated)
    }

    // --- PLANNER SCHEDULE METHODS ---

    fun planOutfitForDate(dateStr: String, outfitId: String, occasion: String? = null) {
        val existingIndex = plannedOutfits.indexOfFirst { it.date == dateStr }
        val newPlanned = PlannedOutfit(
            id = if (existingIndex != -1) plannedOutfits[existingIndex].id else newId(),
            outfitId = outfitId,
            date = dateStr,
            occasion = occasion,
            isWorn = false
        )
        if (existingIndex != -1) {
            plannedOutfits[existingIndex] = newPlanned
        } else {
            plannedOutfits.add(newPlanned)
        }
        dbHelper?.insertPlannedOutfit(newPlanned, currentUserId())
    }

    fun togglePlannedOutfitWorn(dateStr: String) {
        val index = plannedOutfits.indexOfFirst { it.date == dateStr }
        if (index != -1) {
            val current = plannedOutfits[index]
            val updated = current.copy(isWorn = !current.isWorn)
            plannedOutfits[index] = updated
            dbHelper?.updatePlannedOutfit(updated, currentUserId())
        }
    }

    fun removePlannedOutfitForDate(dateStr: String) {
        val planned = plannedOutfits.find { it.date == dateStr }
        if (planned != null) {
            plannedOutfits.remove(planned)
            dbHelper?.deletePlannedOutfit(planned.id)
        }
    }

    fun getPlannedOutfitForDate(dateStr: String): PlannedOutfit? {
        return plannedOutfits.find { it.date == dateStr }
    }

    fun newId(): String = UUID.randomUUID().toString().take(8)
}
