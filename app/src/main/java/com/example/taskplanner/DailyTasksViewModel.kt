package com.example.taskplanner

import androidx.lifecycle.ViewModel

class DailyTasksViewModel: ViewModel() {

    val tasks = mutableListOf<Task>()

    init{
        for( i in 0 until 100) {
            val task = Task()
            task.name = "Task #$i"
            task.isCompleted = i % 2 == 0
            tasks += task
        }
    }
}