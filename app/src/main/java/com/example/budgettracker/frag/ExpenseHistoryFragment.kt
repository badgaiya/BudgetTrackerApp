package com.example.budgettracker.frag

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.BudgetTrackerApplication
import com.example.budgettracker.R
import com.example.budgettracker.adapter.ExpenseAdapter
import com.example.budgettracker.database.Expense
import com.example.budgettracker.databinding.FragmentExpenseHistoryBinding
import com.example.budgettracker.viewmodel.ExpenseViewModel
import com.example.budgettracker.viewmodel.ExpenseViewModelFactory
import java.util.Calendar
import java.util.Date

class ExpenseHistoryFragment : Fragment() {

    private var _binding: FragmentExpenseHistoryBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var expenseAdapter: ExpenseAdapter

    private var selectedFilterCategory: String = "All"
    private var filterStartDate: Date? = null
    private var filterEndDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpenseHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel using the application's repository instances
        val application = activity?.application as BudgetTrackerApplication
        expenseViewModel = ViewModelProvider(this, ExpenseViewModelFactory(application.expenseRepository))
            .get(ExpenseViewModel::class.java)

        setupRecyclerView()
        setupCategoryFilterSpinner()
        setupDateFilterButton()
        observeExpenses() // Start observing expenses with initial filters

        // Set up Floating Action Button click listener
        binding.fabAddExpenseHistory.setOnClickListener {
            findNavController().navigate(R.id.action_expenseHistoryFragment_to_addExpenseActivity)
        }
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter()
        binding.rvExpenseHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = expenseAdapter
        }
    }

    private fun setupCategoryFilterSpinner() {
        // Get categories including "All" from strings.xml
        val categories = resources.getStringArray(R.array.expense_categories_filter)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterCategory.adapter = adapter

        binding.spinnerFilterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedFilterCategory = parent?.getItemAtPosition(position).toString()
                filterExpenses() // Re-filter whenever category changes
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun setupDateFilterButton() {
        binding.btnFilterDate.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog for Start Date
        DatePickerDialog(requireContext(), { _, startYear, startMonth, startDay ->
            val startDateCalendar = Calendar.getInstance().apply {
                set(startYear, startMonth, startDay, 0, 0, 0) // Set time to beginning of the day
                set(Calendar.MILLISECOND, 0)
            }

            // DatePickerDialog for End Date (shown after start date is picked)
            DatePickerDialog(requireContext(), { _, endYear, endMonth, endDay ->
                val endDateCalendar = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDay, 23, 59, 59) // Set time to end of the day
                    set(Calendar.MILLISECOND, 999)
                }

                // Ensure start date is not after end date; swap if it is
                if (startDateCalendar.time.after(endDateCalendar.time)) {
                    filterStartDate = endDateCalendar.time
                    filterEndDate = startDateCalendar.time
                } else {
                    filterStartDate = startDateCalendar.time
                    filterEndDate = endDateCalendar.time
                }
                filterExpenses() // Re-filter with new date range
            }, currentYear, currentMonth, currentDay).show()
        }, currentYear, currentMonth, currentDay).show()
    }

    private fun observeExpenses() {
        // This observer will be the primary source of truth for the list
        // It should react to changes in the database based on the currently applied filters.
        // We'll call `filterExpenses()` which internally subscribes to the correct Flow/LiveData.
        filterExpenses()
    }

    private fun filterExpenses() {
        // Remove previous observers to avoid multiple subscriptions
        expenseViewModel.allExpenses.removeObservers(viewLifecycleOwner)
        expenseViewModel.getExpensesForDateRange(Date(), Date()).removeObservers(viewLifecycleOwner) // Dummy call to remove old observer
        expenseViewModel.getExpensesByCategory("").removeObservers(viewLifecycleOwner) // Dummy call to remove old observer

        when {
            // Case 1: Filter by both Date Range and Category
            filterStartDate != null && filterEndDate != null && selectedFilterCategory != "All" -> {
                expenseViewModel.getExpensesForDateRange(filterStartDate!!, filterEndDate!!).observe(viewLifecycleOwner) { expensesInDateRange ->
                    val filteredByCategory = expensesInDateRange.filter { it.category == selectedFilterCategory }
                    updateExpenseList(filteredByCategory)
                }
            }
            // Case 2: Filter only by Date Range
            filterStartDate != null && filterEndDate != null -> {
                expenseViewModel.getExpensesForDateRange(filterStartDate!!, filterEndDate!!).observe(viewLifecycleOwner) { expenses ->
                    updateExpenseList(expenses)
                }
            }
            // Case 3: Filter only by Category
            selectedFilterCategory != "All" -> {
                expenseViewModel.getExpensesByCategory(selectedFilterCategory).observe(viewLifecycleOwner) { expenses ->
                    updateExpenseList(expenses)
                }
            }
            // Case 4: No filters (show all expenses)
            else -> {
                expenseViewModel.allExpenses.observe(viewLifecycleOwner) { expenses ->
                    updateExpenseList(expenses)
                }
            }
        }
    }

    private fun updateExpenseList(expenses: List<Expense>) {
        if (expenses.isEmpty()) {
            binding.tvNoExpenses.visibility = View.VISIBLE
            binding.rvExpenseHistory.visibility = View.GONE
        } else {
            binding.tvNoExpenses.visibility = View.GONE
            binding.rvExpenseHistory.visibility = View.VISIBLE
            expenseAdapter.submitList(expenses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clear the binding when the view is destroyed
    }
}