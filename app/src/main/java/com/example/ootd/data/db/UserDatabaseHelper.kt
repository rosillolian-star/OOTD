package com.example.ootd.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Patterns
import com.example.ootd.data.User
import java.security.MessageDigest

class UserDatabaseHelper private constructor(context: Context) :
    SQLiteOpenHelper(context.applicationContext, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ootd_users.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val COL_ID = "id"
        private const val COL_FULL_NAME = "full_name"
        private const val COL_USERNAME = "username"
        private const val COL_EMAIL = "email"
        private const val COL_PASSWORD_HASH = "password_hash"
        private const val COL_CREATED_AT = "created_at"

        @Volatile
        private var INSTANCE: UserDatabaseHelper? = null

        fun getInstance(context: Context): UserDatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserDatabaseHelper(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_USERS (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_FULL_NAME TEXT NOT NULL,
                $COL_USERNAME TEXT UNIQUE NOT NULL,
                $COL_EMAIL TEXT UNIQUE NOT NULL,
                $COL_PASSWORD_HASH TEXT NOT NULL,
                $COL_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)

        // Seed default demo user if needed
        val defaultPasswordHash = hashPassword("juan143")
        val values = ContentValues().apply {
            put(COL_FULL_NAME, "Juan Delacruz")
            put(COL_USERNAME, "juandelacruz")
            put(COL_EMAIL, "juan@example.com")
            put(COL_PASSWORD_HASH, defaultPasswordHash)
            put(COL_CREATED_AT, System.currentTimeMillis())
        }
        db.insert(TABLE_USERS, null, values)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    fun registerUser(
        fullName: String,
        username: String,
        email: String,
        password: String
    ): Result<User> {
        val cleanFullName = fullName.trim()
        val cleanUsername = username.trim().lowercase()
        val cleanEmail = email.trim().lowercase()

        if (cleanFullName.isBlank()) {
            return Result.failure<User>(IllegalArgumentException("Full name cannot be empty"))
        }
        if (cleanUsername.isBlank()) {
            return Result.failure<User>(IllegalArgumentException("Username cannot be empty"))
        }
        if (cleanEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.failure<User>(IllegalArgumentException("Please enter a valid email address"))
        }
        if (password.length < 6) {
            return Result.failure<User>(IllegalArgumentException("Password must be at least 6 characters"))
        }

        if (isEmailTaken(cleanEmail)) {
            return Result.failure<User>(IllegalArgumentException("An account with this email already exists"))
        }
        if (isUsernameTaken(cleanUsername)) {
            return Result.failure<User>(IllegalArgumentException("Username is already taken"))
        }

        val passwordHash = hashPassword(password)
        val createdAt = System.currentTimeMillis()

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_FULL_NAME, cleanFullName)
            put(COL_USERNAME, cleanUsername)
            put(COL_EMAIL, cleanEmail)
            put(COL_PASSWORD_HASH, passwordHash)
            put(COL_CREATED_AT, createdAt)
        }

        val rowId = db.insert(TABLE_USERS, null, values)
        return if (rowId != -1L) {
            Result.success(
                User(
                    id = rowId,
                    fullName = cleanFullName,
                    username = cleanUsername,
                    email = cleanEmail,
                    createdAt = createdAt
                )
            )
        } else {
            Result.failure<User>(Exception("Failed to register account. Please try again."))
        }
    }

    fun loginUser(emailOrUsername: String, password: String): Result<User> {
        val queryInput = emailOrUsername.trim().lowercase()
        if (queryInput.isBlank()) {
            return Result.failure<User>(IllegalArgumentException("Please enter your email or username"))
        }
        if (password.isBlank()) {
            return Result.failure<User>(IllegalArgumentException("Please enter your password"))
        }

        val db = readableDatabase
        val passwordHash = hashPassword(password)

        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID, COL_FULL_NAME, COL_USERNAME, COL_EMAIL, COL_PASSWORD_HASH, COL_CREATED_AT),
            "LOWER($COL_EMAIL) = ? OR LOWER($COL_USERNAME) = ?",
            arrayOf(queryInput, queryInput),
            null, null, null
        )

        cursor.use {
            if (it.moveToFirst()) {
                val storedHash = it.getString(it.getColumnIndexOrThrow(COL_PASSWORD_HASH))
                if (storedHash == passwordHash) {
                    val id = it.getLong(it.getColumnIndexOrThrow(COL_ID))
                    val fullName = it.getString(it.getColumnIndexOrThrow(COL_FULL_NAME))
                    val username = it.getString(it.getColumnIndexOrThrow(COL_USERNAME))
                    val email = it.getString(it.getColumnIndexOrThrow(COL_EMAIL))
                    val createdAt = it.getLong(it.getColumnIndexOrThrow(COL_CREATED_AT))

                    return Result.success(
                        User(
                            id = id,
                            fullName = fullName,
                            username = username,
                            email = email,
                            createdAt = createdAt
                        )
                    )
                } else {
                    return Result.failure<User>(IllegalArgumentException("Incorrect password"))
                }
            } else {
                return Result.failure<User>(IllegalArgumentException("No account found with that email or username"))
            }
        }
    }

    fun isEmailTaken(email: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID),
            "LOWER($COL_EMAIL) = ?",
            arrayOf(email.trim().lowercase()),
            null, null, null
        )
        return cursor.use { it.count > 0 }
    }

    fun isUsernameTaken(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_ID),
            "LOWER($COL_USERNAME) = ?",
            arrayOf(username.trim().lowercase()),
            null, null, null
        )
        return cursor.use { it.count > 0 }
    }

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
