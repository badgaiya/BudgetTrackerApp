package com.example.budgettracker.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.budgettracker.database.Budget
import com.example.budgettracker.repo.BudgetRepository

import kotlinx.coroutines.launch

class BudgetViewModel(private val repository: BudgetRepository) : ViewModel() {

    val currentBudget = repository.budget.asLiveData()

    fun setBudget(amount: Double) = viewModelScope.launch {
        repository.insert(Budget(monthlyBudget = amount))
    }

    fun updateBudget(budget: Budget) = viewModelScope.launch {
        repository.update(budget)
    }
}

class BudgetViewModelFactory(private val repository: BudgetRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BudgetViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}