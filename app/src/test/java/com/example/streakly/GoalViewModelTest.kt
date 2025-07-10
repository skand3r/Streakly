package com.example.streakly.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.streakly.StreaklyStepTrackerService
import com.example.streakly.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.Mockito.`when`
import java.time.LocalDate
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class GoalViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var application: Application
    private lateinit var mockDao: GoalDao
    private lateinit var mockProgressDao: GoalProgressDao

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        application = mock()
        mockDao = mock()
        mockProgressDao = mock()

        val mockDb = mock(GoalDatabase::class.java).apply {
            `when`(goalDao()).thenReturn(mockDao)
            `when`(goalProgressDao()).thenReturn(mockProgressDao)
        }
        mockStatic(GoalDatabase::class.java)
            .`when`<GoalDatabase> { GoalDatabase.getDatabase(any()) }
            .thenReturn(mockDb)

        // Stub step tracker
        mockStatic(StreaklyStepTrackerService::class.java)
            .`when` { StreaklyStepTrackerService.steps() }
            .thenReturn(flowOf())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun addGoalShouldInsertNewGoal() = runTest(testDispatcher) {
        val vm = GoalViewModel(application)
        vm.addGoal("Test Goal", 500)
        runCurrent()
        verify(mockDao).insertGoal(Goal(title = "Test Goal", target = 500))
    }

    @Test
    fun deleteGoalShouldNotDeleteDefault() = runTest(testDispatcher) {
        val defaultGoal = Goal(id = 1, title = "Default", target = 100, isDefault = true)
        val vm = GoalViewModel(application)
        vm.deleteGoal(defaultGoal)
        runCurrent()
        verify(mockDao, never()).deleteGoal(any())
    }

    @Test
    fun deleteGoalShouldDeleteNonDefault() = runTest(testDispatcher) {
        val goal = Goal(id = 2, title = "Other", target = 200, isDefault = false)
        val vm = GoalViewModel(application)
        vm.deleteGoal(goal)
        runCurrent()
        verify(mockDao).deleteGoal(goal)
    }

    @Test
    fun getTodayProgressReturnsCorrectValue() = runTest(testDispatcher) {
        val goal = Goal(id = 3, title = "G", target = 10)
        val today = LocalDate.now().toString()
        `when`(mockProgressDao.getProgressByDateFlow(goal.id, today))
            .thenReturn(flowOf(GoalProgress(goal.id, today, 42)))

        val vm = GoalViewModel(application)
        val result = vm.getTodayProgress(goal).first()
        assertEquals(42, result)
    }

    @Test
    fun getTotalProgressReturnsTotal() = runTest(testDispatcher) {
        val goal = Goal(id = 4, title = "G2", target = 20)
        `when`(mockProgressDao.getTotalProgress(goal.id)).thenReturn(123)

        val vm = GoalViewModel(application)
        val total = vm.getTotalProgress(goal).first()
        assertEquals(123, total)
    }

    @Test
    fun getWeeklyProgressEmitsSevenDays() = runTest(testDispatcher) {
        val goal = Goal(id = 5, title = "G3", target = 30)
        (0..6).forEach { i ->
            val date = LocalDate.now().minusDays((6 - i).toLong()).toString()
            `when`(mockProgressDao.getProgressByDateFlow(goal.id, date))
                .thenReturn(flowOf(null))
        }

        val vm = GoalViewModel(application)
        val weekly = vm.getWeeklyProgress(goal).first()
        assertEquals(7, weekly.size)
        assert(weekly.all { it == 0 })
    }

    @Test
    fun addProgressTodayInsertsWhenNoneExists() = runTest(testDispatcher) {
        val goal = Goal(id = 6, title = "G4", target = 40)
        val today = LocalDate.now().toString()
        `when`(mockProgressDao.getProgressByDate(goal.id, today)).thenReturn(null)

        val vm = GoalViewModel(application)
        vm.addProgressToday(goal, 5)
        runCurrent()
        verify(mockProgressDao).insert(GoalProgress(goal.id, today, 5))
    }

    @Test
    fun addProgressTodayUpdatesWhenExists() = runTest(testDispatcher) {
        val goal = Goal(id = 7, title = "G5", target = 50)
        val today = LocalDate.now().toString()
        val existing = GoalProgress(goal.id, today, 3)
        `when`(mockProgressDao.getProgressByDate(goal.id, today)).thenReturn(existing)

        val vm = GoalViewModel(application)
        vm.addProgressToday(goal, 2)
        runCurrent()
        verify(mockProgressDao).update(existing.copy(amount = 5))
    }
}
