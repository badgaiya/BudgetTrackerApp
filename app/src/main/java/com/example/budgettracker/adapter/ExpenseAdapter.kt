package com.example.budgettracker.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.database.Expense
import com.example.budgettracker.databinding.ItemExpenseBinding

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseAdapter : ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder>(ExpenseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val currentExpense = getItem(position)
        holder.bind(currentExpense)
    }

    class ExpenseViewHolder(private val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN")) // For Indian Rupee

        fun bind(expense: Expense) {
            binding.tvExpenseAmount.text = currencyFormat.format(expense.amount)
            binding.tvExpenseCategory.text = "(${expense.category})"
            binding.tvExpenseDate.text = dateFormat.format(expense.date)
            if (!expense.note.isNullOrBlank()) {
                binding.tvExpenseNote.text = expense.note
                binding.tvExpenseNote.visibility = ViewGroup.VISIBLE
            } else {
                binding.tvExpenseNote.visibility = ViewGroup.GONE
            }
        }
    }
}

class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>() {
    override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean {
        return oldItem == newItem
    }
}