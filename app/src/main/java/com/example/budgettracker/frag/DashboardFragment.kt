package com.example.budgettracker.frag


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.budgettracker.BudgetTrackerApplication
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentDashboardBinding
import com.example.budgettracker.viewmodel.DashboardViewModel
import com.example.budgettracker.viewmodel.DashboardViewModelFactory
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val dashboardViewModel: DashboardViewModel by activityViewModels {
        DashboardViewModelFactory(
            (activity?.application as BudgetTrackerApplication).expenseRepository,
            (activity?.application as BudgetTrackerApplication).budgetRepository
        ) as DashboardViewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- FIX START ---
        // monthlyBudget now directly emits Double, not Budget?
        dashboardViewModel.monthlyBudget.observe(viewLifecycleOwner) { budgetAmount ->
            binding.tvMonthlyBudget.text = formatCurrency(budgetAmount)
        }
        // --- FIX END ---

        dashboardViewModel.totalExpensesCurrentMonth.observe(viewLifecycleOwner) { totalExpenses ->
            binding.tvTotalExpenses.text = formatCurrency(totalExpenses)
        }

        dashboardViewModel.remainingBalance.observe(viewLifecycleOwner) { remainingBalanceValue ->
            binding.tvRemainingBalance.text = formatCurrency(remainingBalanceValue)
            if (remainingBalanceValue < 0) {
                binding.tvRemainingBalance.setTextColor(Color.RED)
            } else {
                binding.tvRemainingBalance.setTextColor(
                    resources.getColor(
                        android.R.color.holo_green_dark,
                        null
                    )
                )
            }
        }

        dashboardViewModel.categorySpending.observe(viewLifecycleOwner) { categoryMap ->
            updatePieChart(categoryMap)
        }

        // Set up button click listeners
        binding.btnAddExpense.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_addExpenseActivity)
        }

        binding.btnSetBudget.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_setBudgetActivity)
        }
    }

    private fun updatePieChart(categoryMap: Map<String, Double>) {
        val entries = ArrayList<PieEntry>()
        for ((category, amount) in categoryMap) {
            if (amount > 0) { // Only add categories with spending
                entries.add(PieEntry(amount.toFloat(), category))
            }
        }

        if (entries.isEmpty()) {
            binding.pieChartCategories.visibility = View.GONE
            binding.tvSpendingCategoriesLabel.text =
                "Spending Categories (This Month): No expenses yet."
            return
        } else {
            binding.pieChartCategories.visibility = View.VISIBLE
            binding.tvSpendingCategoriesLabel.text = "Spending Categories (This Month):"
        }

        val dataSet = PieDataSet(entries, "Spending Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList() // Use Material colors
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val data = PieData(dataSet)
        binding.pieChartCategories.data = data
        binding.pieChartCategories.setUsePercentValues(true)
        binding.pieChartCategories.description.isEnabled = false // Disable description label
        binding.pieChartCategories.isDrawHoleEnabled = true
        binding.pieChartCategories.setHoleColor(Color.WHITE)
        binding.pieChartCategories.setTransparentCircleRadius(61f)
        binding.pieChartCategories.setEntryLabelColor(Color.BLACK)
        binding.pieChartCategories.setEntryLabelTextSize(10f)
        binding.pieChartCategories.animateY(1400) // Animation
        binding.pieChartCategories.invalidate() // Refresh chart
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN")) // For Indian Rupee
        return format.format(amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}