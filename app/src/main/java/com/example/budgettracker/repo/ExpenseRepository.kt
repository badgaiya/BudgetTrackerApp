package com.example.budgettracker.repo

import com.example.budgettracker.database.Expense
import com.example.budgettracker.database.ExpenseDao
import kotlinx.coroutines.flow.Flow
import java.util.Date

class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        expenseDao.insert(expense)
    }

    suspend fun update(expense: Expense) {
        expenseDao.update(expense)
    }

    suspend fun delete(expense: Expense) {
        expenseDao.delete(expense)
    }

    fun getExpensesForDateRange(startDate: Date, endDate: Date): Flow<List<Expense>> {
        return expenseDao.getExpensesForDateRange(startDate, endDate)
    }

    fun getTotalExpensesForDateRange(startDate: Date, endDate: Date): Flow<Double?> {
        return expenseDao.getTotalExpensesForDateRange(startDate, endDate)
    }

    fun getCategorySpending(startDate: Date, endDate: Date): Flow<Map<String, Double>> {
        return expenseDao.getCategorySpending(startDate, endDate)
    }

    fun getExpensesByCategory(category: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(category)
    }
}