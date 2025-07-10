package com.example.streakly

import com.example.streakly.data.Goal
import org.junit.Assert.*
import org.junit.Test

class GoalTest {
    @Test
    fun newGoalIsNotDefault() {
        val goal = Goal(title = "Test", target = 5)
        assertFalse(goal.isDefault)
    }
}