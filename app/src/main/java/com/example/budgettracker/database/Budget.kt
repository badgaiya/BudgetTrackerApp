package com.example.budgettracker.database


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget")
data class Budget(
    @PrimaryKey val id: Int = 1, // There will only be one budget entry
    val monthlyBudget: Double
)