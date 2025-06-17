package com.example.streakly.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalProgressDao {
    @Query("SELECT SUM(amount) FROM GoalProgress WHERE goalId = :goalId")
    suspend fun getTotalProgress(goalId: Int): Int?

    @Query("SELECT * FROM GoalProgress WHERE goalId = :goalId AND date = :date")
    suspend fun getProgressByDate(goalId: Int, date: String): GoalProgress?

    @Query("SELECT * FROM GoalProgress WHERE goalId = :goalId AND date = :date")
    fun getProgressByDateFlow(goalId: Int, date: String): Flow<GoalProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: GoalProgress)

    @Update
    suspend fun update(progress: GoalProgress)

    @Delete
    suspend fun delete(progress: GoalProgress)
}