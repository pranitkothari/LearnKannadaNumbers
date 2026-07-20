package com.learnkannadanumbers.app.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress WHERE itemId = :itemId")
    suspend fun get(itemId: String): ProgressEntity?

    @Upsert
    suspend fun upsert(entity: ProgressEntity)

    // Weakest first: lowest accuracy, then most total wrong attempts as a tiebreaker.
    // Items never attempted are excluded - "weak" means "struggled with", not "untried".
    @Query(
        "SELECT * FROM progress " +
            "WHERE (correctCount + incorrectCount) > 0 " +
            "ORDER BY (CAST(correctCount AS REAL) / (correctCount + incorrectCount)) ASC, incorrectCount DESC " +
            "LIMIT :limit",
    )
    suspend fun weakest(limit: Int): List<ProgressEntity>
}
