package com.learnkannadanumbers.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val itemId: String,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val lastAttemptAt: Long = 0,
)
