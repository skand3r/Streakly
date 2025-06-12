package com.example.streakly.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.streakly.data.Goal
import com.example.streakly.data.GoalDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = GoalDatabase.getDatabase(application).goalDao()
    private val _goals = MutableStateFlow<List<Goal>>(emptyList())
    val goals = _goals.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllGoals().observeForever {
                _goals.value = it
            }
        }
    }

    fun addGoal(title: String, target: Int) {
        viewModelScope.launch {
            dao.insertGoal(Goal(title = title, target = target))
        }
    }

    fun incrementGoal(goal: Goal) {
        viewModelScope.launch {
            val updated = goal.copy(progress = goal.progress + 1)
            dao.updateGoal(updated)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            dao.deleteGoal(goal)
        }
    }
}
