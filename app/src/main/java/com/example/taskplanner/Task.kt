package com.example.taskplanner

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Task(@PrimaryKey val id: UUID = UUID.randomUUID(),
                var name: String = "",
                var date: Date = Date(),
                var note: String = "",
                var isCompleted: Boolean = false)