package com.example.streakly.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Query("SELECT * FROM Goal")
    fun getAllGoals(): LiveData<List<Goal>>

    @Query("SELECT * FROM Goal WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultGoal(): Goal?

    @Insert
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}
