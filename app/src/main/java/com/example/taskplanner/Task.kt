package com.example.taskplanner

import java.util.*

data class Task(val id: UUID = UUID.randomUUID(),
                var name: String = "",
                var date: Date = Date(),
                var note: String = "",
                var isCompleted: Boolean = false)