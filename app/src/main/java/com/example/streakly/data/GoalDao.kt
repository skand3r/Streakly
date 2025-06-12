package com.example.streakly.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface GoalDao {
    @Query("SELECT * FROM Goal")
    fun getAllGoals(): LiveData<List<Goal>>

    @Insert
    suspend fun insertGoal(goal: Goal)

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)
}
