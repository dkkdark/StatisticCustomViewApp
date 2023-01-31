package com.kseniabl.statisticapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kseniabl.statisticapp.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val prodsNames = arrayListOf("Coffee", "Pizza", "Nuggets", "Milk", "Apples", "Banana", "Orange", "Juice", "Tea", "Ice cream", "Waffles")
    private val valuesFromOneRect = arrayListOf(20, 50, 80, 100, 150, 200, 250, 280, 300, 320, 360, 400, 450)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.barChartView.barChart = BarChartData(mutableMapOf(
            "17.06" to arrayListOf(100, 150, 50),
            "18.06" to arrayListOf(400, 200),
            "19.06" to arrayListOf(100, 50),
            "20.06" to arrayListOf(80, 80, 300),
            "21.06" to arrayListOf(400, 100, 100, 200),
            "22.06" to arrayListOf(50, 50),
            "23.06" to arrayListOf(200, 70)),
            arrayListOf("1 Article", "2 Article", "3 Article", "4 Article")
        )
    }

}