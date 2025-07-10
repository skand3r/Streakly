package com.example.streakly

import com.example.streakly.data.Goal
import org.junit.Assert.*
import org.junit.Test

class GoalTest {
    @Test
    fun defaultGoalIsNotDefault() {
        val goal = Goal(title = "Test", target = 5)
        assertFalse(goal.isDefault)
    }

    @Test
    fun copyCreatesModifiedInstance() {
        val goal = Goal(id = 1, title = "Test", target = 5)
        val copy = goal.copy(title = "Other")
        assertEquals("Other", copy.title)
        assertEquals(goal.id, copy.id)
    }
}