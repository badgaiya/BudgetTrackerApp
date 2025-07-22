package com.example.budgettracker
data class CategorySpending(
    val category: String,
    val totalAmount: Double // This name MUST match the alias in your SQL query below
)