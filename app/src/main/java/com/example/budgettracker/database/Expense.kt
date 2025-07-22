package com.example.budgettracker.database



import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amount: Double,
    val category: String,
    val date: Date, // Store as Date, Room will handle conversion
    val note: String? = null
)