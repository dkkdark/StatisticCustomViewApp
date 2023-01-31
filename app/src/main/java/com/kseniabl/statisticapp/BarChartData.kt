package com.kseniabl.statisticapp

typealias OnBarChartChangedListener = (data: BarChartData) -> Unit

class BarChartData(private val map: MutableMap<String, ArrayList<Int>>,
                    private val valuesForMap: ArrayList<String>) {

    var listener: OnBarChartChangedListener? = null

    fun getMap(): Map<String, ArrayList<Int>> {
        return map
    }

    fun getValuesForMap(): ArrayList<String> {
        return valuesForMap
    }
}