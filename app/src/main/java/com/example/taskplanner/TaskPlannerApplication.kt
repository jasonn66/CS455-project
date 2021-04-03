package com.example.taskplanner

import android.app.Application

class TaskPlannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        TaskRepository.initialize(this)
    }
}