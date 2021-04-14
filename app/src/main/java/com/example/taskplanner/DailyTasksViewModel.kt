package com.example.taskplanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class DailyTasksViewModel: ViewModel() {

    private val taskRepository = TaskRepository.get()
    private val taskDateLiveData = MutableLiveData<Date>()

    var taskListLiveData: LiveData<List<Task>> =
            Transformations.switchMap(taskDateLiveData) {
                date -> taskRepository.getDailyTasks(date)
            }

    fun loadTasks(date: Date) {
        taskDateLiveData.value = date
    }

    fun addTask(task: Task) {
        taskRepository.addTask(task)
    }
}