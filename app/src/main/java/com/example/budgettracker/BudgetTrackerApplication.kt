package com.example.budgettracker


import android.app.Application
import com.example.budgettracker.database.AppDatabase
import com.example.budgettracker.repo.BudgetRepository
import com.example.budgettracker.repo.ExpenseRepository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class BudgetTrackerApplication : Application() {
    // Using a Scope for the application's lifetime for database operations
    val applicationScope = CoroutineScope(SupervisorJob())

    // Lazy initialization for the database and repositories
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val budgetRepository by lazy { BudgetRepository(database.budgetDao()) }
}