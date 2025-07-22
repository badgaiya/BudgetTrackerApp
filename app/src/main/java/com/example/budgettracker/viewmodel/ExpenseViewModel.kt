package com.example.budgettracker.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.database.Expense
import com.example.budgettracker.repo.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Date

class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    val allExpenses = repository.allExpenses.asLiveData()

    fun insert(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }

    fun update(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }

    fun delete(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }

    fun getExpensesForDateRange(startDate: Date, endDate: Date) =
        repository.getExpensesForDateRange(startDate, endDate).asLiveData()

    fun getExpensesByCategory(category: String) =
        repository.getExpensesByCategory(category).asLiveData()
}

class ExpenseViewModelFactory(private val repository: ExpenseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}