package com.example.taskplanner

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.taskplanner.database.TaskDao
import com.example.taskplanner.database.TaskDatabase
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "task-database"

// TaskRepository determines how to fetch and store data for the application
class TaskRepository private constructor(context: Context) {

    // Creates a concrete implementation of the abstract TaskDatabase
    private val database : TaskDatabase = Room.databaseBuilder(
            context.applicationContext,
            TaskDatabase::class.java,
            DATABASE_NAME
    ).build()

    private val taskDao = database.taskDao()

    // Creates a new thread to execute code on
    private val executor = Executors.newSingleThreadExecutor()


    // Database functions

    fun getUpcomingTasks(date: Date): LiveData<List<Task>> = taskDao.getUpcomingTasks(date)

    fun getTask(id: UUID): LiveData<Task?> = taskDao.getTask(id)

    fun getDailyTasks(date: Date): LiveData<List<Task>> = taskDao.getDailyTasks(date)

    fun updateTask(task: Task) {
        executor.execute {
            taskDao.updateTask(task)
        }
    }

    fun addTask(task: Task) {
        executor.execute {
            taskDao.addTask(task)
        }
    }

    fun deleteTask(task: Task) {
        executor.execute {
            taskDao.deleteTask(task)
        }
    }

    companion object {

        private var INSTANCE: TaskRepository? = null

        fun initialize(context: Context) {
            if(INSTANCE == null) {
                INSTANCE = TaskRepository(context)
            }
        }

        fun get(): TaskRepository {
            return INSTANCE ?:
                    throw IllegalStateException("TaskRepository must be initialized")
        }
    }
}