package com.example.budgettracker


import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.budgettracker.database.Expense

import com.example.budgettracker.databinding.ActivityAddExpenseBinding
import com.example.budgettracker.viewmodel.ExpenseViewModel
import com.example.budgettracker.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar

import java.util.Locale

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add New Expense"

        val application = application as BudgetTrackerApplication
        expenseViewModel = ViewModelProvider(this, ExpenseViewModelFactory(application.expenseRepository))
            .get(ExpenseViewModel::class.java)

        setupCategorySpinner()
        setupDatePicker()

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun setupCategorySpinner() {
        val categories = resources.getStringArray(R.array.expense_categories)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        updateDateInView() // Set initial date

        binding.tvSelectedDate.setOnClickListener {
            val year = selectedDate.get(Calendar.YEAR)
            val month = selectedDate.get(Calendar.MONTH)
            val day = selectedDate.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                updateDateInView()
            }, year, month, day).show()
        }
    }

    private fun updateDateInView() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.tvSelectedDate.text = dateFormat.format(selectedDate.time)
    }

    private fun saveExpense() {
        val amountStr = binding.etAmount.text.toString()
        val category = binding.spinnerCategory.selectedItem.toString()
        val note = binding.etNote.text.toString().trim()
        val date = selectedDate.time

        if (amountStr.isBlank()) {
            binding.tilAmount.error = "Amount cannot be empty"
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.tilAmount.error = "Please enter a valid amount"
            return
        }

        val newExpense = Expense(
            amount = amount,
            category = category,
            date = date,
            note = note.ifEmpty { null }
        )

        expenseViewModel.insert(newExpense)
        Toast.makeText(this, "Expense added successfully!", Toast.LENGTH_SHORT).show()
        finish() // Go back to previous screen
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}