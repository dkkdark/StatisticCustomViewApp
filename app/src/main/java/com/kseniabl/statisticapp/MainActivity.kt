package com.kseniabl.statisticapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kseniabl.statisticapp.databinding.ActivityMainBinding
import kotlin.math.max
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val prodsNames = arrayListOf("Coffee", "Pizza", "Nuggets", "Milk", "Apples", "Banana", "Orange", "Juice", "Tea", "Ice cream", "Waffles")
    private val valuesFromOneRect = arrayListOf(20, 50, 80, 100, 150, 200, 250, 280, 300, 320, 360, 400, 450)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setBarChart()
        buttonClick()
    }

    private fun setBarChart() {
        val count = (5..9).random()
        val map = mutableMapOf<String, ArrayList<Int>>()
        for (i in 0 until count) {
            val valuesCount = (1..6).random()
            val numArray = arrayListOf<Int>()
            for (j in 0 until valuesCount) {
                numArray.add(valuesFromOneRect.random())
            }
            map["${17+i}.08"] = numArray
        }

        val prodsArray = arrayListOf<String>()
        var maxSize = 0
        for (i in map.values) {
            if (maxSize < i.size)
                maxSize = i.size
        }
        for (i in 0 until maxSize) {
            prodsArray.add(prodsNames.random())
        }

        binding.barChartView.barChart = BarChartData(map, prodsArray)
    }

    private fun buttonClick() {
        binding.randomChartButton.setOnClickListener { setBarChart() }
    }
}