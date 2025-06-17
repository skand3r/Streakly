package com.example.streakly.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val target: Int
)
