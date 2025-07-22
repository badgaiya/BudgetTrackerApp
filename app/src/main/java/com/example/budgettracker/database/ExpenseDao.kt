package com.example.budgettracker.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete
import androidx.room.Update
import androidx.room.MapInfo // Import MapInfo
import java.util.Date

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<Expense>>

    // For current month's expenses for dashboard calculations
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getExpensesForDateRange(startDate: Date, endDate: Date): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    fun getTotalExpensesForDateRange(startDate: Date, endDate: Date): Flow<Double?>

    @Query("SELECT category, SUM(amount) AS totalAmount FROM expenses WHERE date BETWEEN :startDate AND :endDate GROUP BY category")
    @MapInfo(keyColumn = "category", valueColumn = "totalAmount") // Add this annotation
    fun getCategorySpending(startDate: Date, endDate: Date): Flow<Map<String, Double>>
}