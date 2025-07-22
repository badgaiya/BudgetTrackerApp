package com.example.budgettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.budgettracker.repo.BudgetRepository
import com.example.budgettracker.repo.ExpenseRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map // Import map for Flow transformations
import java.util.Calendar
import java.util.Date

class DashboardViewModel(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    private val currentMonthStartDate: Date
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.time
        }

    private val currentMonthEndDate: Date
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.time
        }

    // Transform budget Flow to always emit a non-nullable Double
    val monthlyBudgetFlow: Flow<Double> = budgetRepository.budget.map { budget ->
        budget?.monthlyBudget ?: 0.0
    }

    // Transform total expenses Flow to always emit a non-nullable Double
    val totalExpensesCurrentMonthFlow: Flow<Double> =
        expenseRepository.getTotalExpensesForDateRange(currentMonthStartDate, currentMonthEndDate)
            .map { it ?: 0.0 } // Convert null to 0.0


    // Now combine can correctly infer types as both inputs are Flow<Double>
    val remainingBalance = combine(monthlyBudgetFlow, totalExpensesCurrentMonthFlow) { budgetAmount, expensesAmount ->
        budgetAmount - expensesAmount
    }.asLiveData()

    // Expose as LiveData for UI observation
    val monthlyBudget = monthlyBudgetFlow.asLiveData()
    val totalExpensesCurrentMonth = totalExpensesCurrentMonthFlow.asLiveData()

    val categorySpending = expenseRepository.getCategorySpending(currentMonthStartDate, currentMonthEndDate).asLiveData()
}

class DashboardViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val budgetRepository: BudgetRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(expenseRepository, budgetRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}