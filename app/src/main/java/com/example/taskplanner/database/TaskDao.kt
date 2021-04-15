package com.example.taskplanner.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.taskplanner.Task
import java.util.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE task.id=(:id)")
    fun getTask(id:UUID): LiveData<Task?>

    @Query("SELECT * FROM task WHERE task.date=(:date)")
    fun getDailyTasks(date:Date): LiveData<List<Task>>

    @Update
    fun updateTask(task: Task)

    @Insert
    fun addTask(task: Task)

    @Delete
    fun deleteTask(task: Task)
}