package com.example.budgettracker


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
 import com.example.budgettracker.databinding.ActivitySetBudgetBinding
import com.example.budgettracker.viewmodel.BudgetViewModel
import com.example.budgettracker.viewmodel.BudgetViewModelFactory

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetBudgetBinding
    private lateinit var budgetViewModel: BudgetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Set Monthly Budget"

        val application = application as BudgetTrackerApplication
        budgetViewModel = ViewModelProvider(this, BudgetViewModelFactory(application.budgetRepository))
            .get(BudgetViewModel::class.java)

        // Populate current budget if exists
        budgetViewModel.currentBudget.observe(this) { budget ->
            budget?.let {
                binding.etMonthlyBudget.setText(it.monthlyBudget.toString())
            }
        }

        binding.btnSaveBudget.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val budgetAmountStr = binding.etMonthlyBudget.text.toString()
        if (budgetAmountStr.isBlank()) {
            binding.tilMonthlyBudget.error = "Budget amount cannot be empty"
            return
        }
        val budgetAmount = budgetAmountStr.toDoubleOrNull()
        if (budgetAmount == null || budgetAmount < 0) {
            binding.tilMonthlyBudget.error = "Please enter a valid budget amount"
            return
        }

        budgetViewModel.setBudget(budgetAmount) // Insert or update using replace strategy
        Toast.makeText(this, "Budget set successfully!", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}