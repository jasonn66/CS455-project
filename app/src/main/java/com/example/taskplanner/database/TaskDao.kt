package com.example.taskplanner.database

import androidx.room.Dao
import androidx.room.Query
import com.example.taskplanner.Task
import java.util.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getTasks(): List<Task>

    @Query("SELECT * FROM task WHERE task.id=(:id)")
    fun getTask(id:UUID): Task?
}