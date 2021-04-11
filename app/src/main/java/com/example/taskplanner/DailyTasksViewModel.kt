package com.example.taskplanner

import androidx.lifecycle.ViewModel

class DailyTasksViewModel: ViewModel() {

    private val taskRepository = TaskRepository.get()
    val taskListLiveData = taskRepository.getTasks()

    fun addTask(task: Task) {
        taskRepository.addTask(task)
    }
}