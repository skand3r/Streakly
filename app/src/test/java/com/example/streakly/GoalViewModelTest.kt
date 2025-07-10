package com.example.streakly

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import com.example.streakly.data.Goal
import com.example.streakly.data.GoalDao
import com.example.streakly.data.GoalProgress
import com.example.streakly.data.GoalProgressDao
import com.example.streakly.viewmodel.GoalViewModel
import com.example.streakly.StepTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class GoalViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val application: Application =
        ApplicationProvider.getApplicationContext()

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)
    private lateinit var goalDao: GoalDao
    private lateinit var progressDao: GoalProgressDao
    private lateinit var viewModel: GoalViewModel

    private class EmptyStepTracker : StepTracker {
        override fun steps(): Flow<Int> = emptyFlow()
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        goalDao = mock()
        progressDao = mock()

        runBlocking {
            whenever(goalDao.getDefaultGoal()).thenReturn(null)
        }

        whenever(goalDao.getAllGoals())
            .thenReturn(MutableLiveData(emptyList()))

        viewModel = GoalViewModel(
            application,
            goalDao,
            progressDao,
            EmptyStepTracker()
        )
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test fun addGoal_callsInsert() = scope.runTest {
        viewModel.addGoal("Test", 5)
        advanceUntilIdle()
        verify(goalDao).insertGoal(Goal(title = "Test", target = 5))
    }

    @Test
    fun deleteGoal_nonDefault_callsDao() = scope.runTest {
        val goal = Goal(id = 1, title = "Push", target = 5)
        viewModel.deleteGoal(goal)
        advanceUntilIdle()
        verify(goalDao).deleteGoal(goal)
    }

    @Test
    fun deleteGoal_defaultGoal_doesNotCallDao() = scope.runTest {
        val goal = Goal(id = 1, title = "Daily Steps", target = 10, isDefault = true)
        viewModel.deleteGoal(goal)
        advanceUntilIdle()
        verify(goalDao, org.mockito.Mockito.never()).deleteGoal(goal)
    }

    @Test
    fun addProgressToday_incrementsExistingByOne() = scope.runTest {
        val goal = Goal(id = 1, title = "Push", target = 5)
        val today = java.time.LocalDate.now().toString()
        whenever(progressDao.getProgressByDate(goal.id, today)).thenReturn(GoalProgress(goalId = goal.id, date = today, amount = 1))

        viewModel.addProgressToday(goal)
        advanceUntilIdle()
        verify(progressDao).update(GoalProgress(goalId = goal.id, date = today, amount = 2))
    }
}