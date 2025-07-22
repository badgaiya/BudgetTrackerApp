package com.example.budgettracker.repo

import com.example.budgettracker.database.Budget
import com.example.budgettracker.database.BudgetDao
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    val budget: Flow<Budget?> = budgetDao.getBudget()

    suspend fun insert(budget: Budget) {
        budgetDao.insert(budget)
    }

    suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }
}