package com.example.streakly.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = ["id"],
        childColumns = ["goalId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("goalId")]
)
data class GoalProgress(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val goalId: Int,
    val date: String, // like "2025-06-17"
    val amount: Int = 0
)