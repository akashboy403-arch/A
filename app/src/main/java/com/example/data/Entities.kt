package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workers")
data class Worker(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val mobile: String,
    val address: String,
    val photoPath: String? = null,
    val dailyWage: Double,
    val workerId: String, // Manual ID provided by admin
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "attendance")
data class Attendance(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerId: Long,
    val date: Long, // Start of day timestamp
    val status: String // "PRESENT", "ABSENT", "HALF_DAY"
)

@Entity(tableName = "payments")
data class Payment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workerId: Long,
    val amount: Double,
    val date: Long,
    val type: String, // "WAGE", "ADVANCE"
    val isPaid: Boolean = true,
    val notes: String? = null
)
