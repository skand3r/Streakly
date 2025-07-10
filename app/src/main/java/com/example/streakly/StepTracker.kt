package com.example.streakly

import kotlinx.coroutines.flow.Flow

interface StepTracker {
    fun steps(): Flow<Int>
}