package com.roma.myapplication4

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.roma.myapplication4.viewmodel.TaskViewModel

class StatisticsFragment : Fragment() {

    private lateinit var taskViewModel: TaskViewModel

    private val chartColors = listOf(
        Color.parseColor("#74b9ff"),
        Color.parseColor("#a29bfe"),
        Color.parseColor("#ffeaa7"),
        Color.parseColor("#55efc4"),
        Color.parseColor("#fd79a8")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmail = getCurrentUserEmail()
        if (userEmail != null) {
            taskViewModel.getTasksForUser(userEmail).observe(viewLifecycleOwner) { tasks ->
                view.findViewById<BarChart>(R.id.tasksBarChart)?.let {
                    setupTasksBarChart(it, tasks.size)
                }
            }
        }

        view.findViewById<BarChart>(R.id.barChart)?.let { setupBarChart(it) }
        view.findViewById<PieChart>(R.id.pieChart)?.let { setupPieChart(it) }
    }

    private fun setupTasksBarChart(barChart: BarChart, taskCount: Int) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, taskCount.toFloat()))

        val dataSet = BarDataSet(entries, "Задачи к выполнению")
        dataSet.color = chartColors[0] // A single color for the bar
        dataSet.setValueTextColor(Color.parseColor("#34495E"))
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD)
        dataSet.valueTextSize = 14f // Make value text bigger

        val barData = BarData(dataSet)
        barData.barWidth = 0.2f // A bit narrower bar
        barData.setValueFormatter(DefaultValueFormatter(0))

        barChart.data = barData

        // val roundedRenderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)
        // roundedRenderer.radius = 20f
        // barChart.renderer = roundedRenderer

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.animateY(1500)
        barChart.setDrawValueAboveBar(true)
        barChart.setDrawGridBackground(false)
        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.setDrawLabels(false) // Hide X-axis labels as it's a single value
        xAxis.setDrawAxisLine(false) // Hide the axis line itself

        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        barChart.marker = null // Disable marker for this chart

        barChart.invalidate()
    }

    private fun getCurrentUserEmail(): String? {
        val prefs = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return prefs.getString("email", null)
    }

    private fun setupBarChart(barChart: BarChart) {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 4f))
        entries.add(BarEntry(1f, 8f))
        entries.add(BarEntry(2f, 6f))
        entries.add(BarEntry(3f, 5f))
        entries.add(BarEntry(4f, 7f))
        entries.add(BarEntry(5f, 3f))
        entries.add(BarEntry(6f, 5f))

        val dataSet = BarDataSet(entries, "Задачи за неделю")
        dataSet.colors = chartColors
        dataSet.setValueTextColor(Color.parseColor("#34495E"))
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD)
        dataSet.valueTextSize = 12f

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f
        barData.setValueFormatter(DefaultValueFormatter(0))

        barChart.data = barData

        // val roundedRenderer = RoundedBarChartRenderer(barChart, barChart.animator, barChart.viewPortHandler)
        // roundedRenderer.radius = 30f
        // barChart.renderer = roundedRenderer

        // --- UI/UX Customization ---
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.animateY(1500)
        barChart.setDrawValueAboveBar(true)
        barChart.setDrawGridBackground(false)
        barChart.setPinchZoom(false)
        barChart.isDoubleTapToZoomEnabled = false

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.setTypeface(Typeface.DEFAULT_BOLD)
        xAxis.setTextColor(Color.parseColor("#7F8C8D"))
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"))

        barChart.axisLeft.isEnabled = false
        barChart.axisRight.isEnabled = false

        val marker = CustomMarkerView(requireContext(), R.layout.marker_view)
        barChart.marker = marker

        barChart.invalidate()
    }

    private fun setupPieChart(pieChart: PieChart) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(40f, "Работа"))
        entries.add(PieEntry(25f, "Учеба"))
        entries.add(PieEntry(20f, "Дом"))
        entries.add(PieEntry(15f, "Хобби"))

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = chartColors
        dataSet.sliceSpace = 4f

        // Display values inside the slices
        dataSet.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        dataSet.setValueTextColor(Color.WHITE)
        dataSet.valueTextSize = 16f
        dataSet.setValueTypeface(Typeface.DEFAULT_BOLD)

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(PercentFormatter(pieChart))

        pieChart.data = pieData

        // --- UI/UX Customization ---
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false

        // Donut hole
        pieChart.isDrawHoleEnabled = true
        pieChart.holeRadius = 65f
        pieChart.transparentCircleRadius = 70f

        // Center text
        pieChart.centerText = "Категории"
        pieChart.setCenterTextSize(18f)
        pieChart.setCenterTextColor(Color.parseColor("#2C3E50"))
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD)

        // Disable entry labels, as we now use a legend
        pieChart.setDrawEntryLabels(false)

        // Legend setup
        val legend = pieChart.legend
        legend.isEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)
        legend.form = Legend.LegendForm.SQUARE
        legend.formSize = 12f
        legend.textSize = 14f
        legend.xEntrySpace = 16f

        pieChart.animateY(1500)

        pieChart.invalidate()
    }
}
