package com.example.streakly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.streakly.StepTracker
import com.example.streakly.StreaklyStepTrackerService
import com.example.streakly.data.Goal
import com.example.streakly.data.GoalDao
import com.example.streakly.data.GoalDatabase
import com.example.streakly.data.GoalProgress
import com.example.streakly.data.GoalProgressDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private var dao: GoalDao = GoalDatabase.getDatabase(application).goalDao()
    private var progressDao: GoalProgressDao = GoalDatabase.getDatabase(application).goalProgressDao()
    private var stepTracker: StepTracker = StreaklyStepTrackerService()
    private val tracker = stepTracker

    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals = _goals.asStateFlow()

    constructor(
        application: Application,
        dao: GoalDao,
        progressDao: GoalProgressDao,
        stepTracker: StepTracker
    ) :this(application) {
        initialize(application, dao, progressDao, stepTracker)
    }

    private fun initialize(application: Application, dao: GoalDao?, progressDao: GoalProgressDao?, stepTracker: StepTracker?) {
        this.dao = dao ?: GoalDatabase.getDatabase(application).goalDao()
        this.progressDao = progressDao ?: GoalDatabase.getDatabase(application).goalProgressDao()
        this.stepTracker = stepTracker ?: StreaklyStepTrackerService()
    }

    init {
        initialize(application, null, null, null)
        viewModelScope.launch {
            if (dao.getDefaultGoal() == null) {
                dao.insertGoal(
                    Goal(title = "Daily Steps", target = 10000, isDefault = true)
                )
            }
            dao.getAllGoals().observeForever { _goals.value = it }
        }

        // Listen for step updates from the tracker and store them for the
        // default goal
        tracker.steps()
            .onEach { count ->
                viewModelScope.launch {
                    val defaultGoal = dao.getDefaultGoal()
                    if (defaultGoal != null) {
                        addProgressToday(defaultGoal, count)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun addGoal(title: String, target: Int) {
        viewModelScope.launch {
            dao.insertGoal(Goal(title = title, target = target))
        }
    }

    fun deleteGoal(goal: Goal) {
        if (goal.isDefault) return
        viewModelScope.launch { dao.deleteGoal(goal) }
    }

    fun getTodayProgress(goal: Goal): Flow<Int> {
        val today = LocalDate.now().toString()
        return progressDao.getProgressByDateFlow(goal.id, today)
            .map { it?.amount ?: 0 }
    }


    fun getTotalProgress(goal: Goal): Flow<Int> = flow {
        val total = progressDao.getTotalProgress(goal.id) ?: 0
        emit(total)
    }

    fun getWeeklyProgress(goal: Goal): Flow<List<Int>> {
        val flows = (6 downTo 0).map { offset ->
            val date = LocalDate.now().minusDays(offset.toLong()).toString()
            progressDao.getProgressByDateFlow(goal.id, date).map { it?.amount ?: 0 }
        }
        return combine(flows) { it.toList() }
    }

    fun addProgressToday(goal: Goal, amount: Int = 1) {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            val existing = progressDao.getProgressByDate(goal.id, today)

            if (existing != null) {
                progressDao.update(existing.copy(amount = existing.amount + amount))
            } else {
                progressDao.insert(GoalProgress(goalId = goal.id, date = today, amount = amount))
            }
        }
    }

}
