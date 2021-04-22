package com.example.taskplanner

import android.app.Application

// TaskPlannerApplication initializes the TaskRepository when the application is starting
// Avoids IllegalStateException from the getter in the TaskRepository companion object
class TaskPlannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        TaskRepository.initialize(this)
    }
}