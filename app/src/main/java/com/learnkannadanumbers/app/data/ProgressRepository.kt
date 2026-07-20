package com.learnkannadanumbers.app.data

import android.content.Context

class ProgressRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).progressDao()

    suspend fun recordAttempt(itemId: String, correct: Boolean) {
        val existing = dao.get(itemId) ?: ProgressEntity(itemId = itemId)
        val updated = existing.copy(
            correctCount = existing.correctCount + if (correct) 1 else 0,
            incorrectCount = existing.incorrectCount + if (correct) 0 else 1,
            lastAttemptAt = System.currentTimeMillis(),
        )
        dao.upsert(updated)
    }

    suspend fun weakestItemIds(limit: Int = 20): List<String> =
        dao.weakest(limit).map { it.itemId }
}
