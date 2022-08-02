package com.kseniabl.statisticapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.max

class BarChartView(context: Context, attributeSet: AttributeSet?,
                    defStyleAttr: Int, defStyleRes: Int)
    : View(context, attributeSet, defStyleAttr, defStyleRes) {

    private var listener: OnBarChartChangedListener = {
        invalidate()
    }
    private var isMonth = false

    private val oneColumnSize = 100
    private var columnsCount = 0
    private val sideNumsCount = 4
    private var maxSum = 0
    private var maxValueHeight = 600
    private val topBottomPadding = 60
    private val leftRightPadding = 100

    private val fieldRect = RectF()
    private var cellWidth = 0
    private var cellPadding = 0F

    private var selectedColumn = -1
    private var chartCenter = -1
    private var chartAmount = 0
    private var numOfChart = -1

    private lateinit var rectPaint: Paint
    private lateinit var dottedPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var strokeRectPaint: Paint
    private val colors = arrayListOf<Int>()

    constructor(context: Context, attributesSet: AttributeSet?, defStyleAttr: Int) : this(context, attributesSet, defStyleAttr, 0)
    constructor(context: Context, attributesSet: AttributeSet?) : this(context, attributesSet, 0)
    constructor(context: Context) : this(context, null)

    var barChart: BarChartData? = null
        set(value) {
            barChart?.listener = null
            field = value
            value?.listener = listener
            setSizes()
            requestLayout()
            invalidate()
        }

    init {
        if (attributeSet != null) {
            initAttributes(attributeSet, defStyleAttr, defStyleRes)
        }
        initPaints()

        if (isInEditMode) {
            /*barChart = BarChartData(mutableMapOf(
                "17.06" to arrayListOf(100, 150, 50),
                "18.06" to arrayListOf(400, 200),
                "19.06" to arrayListOf(100, 50),
                "20.06" to arrayListOf(80, 80, 300),
                "21.06" to arrayListOf(400, 100, 100, 200),
                "22.06" to arrayListOf(50, 50),
                "23.06" to arrayListOf(200, 70)))*/
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        barChart?.listener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        barChart?.listener = null
    }

    private fun initAttributes(attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.BarChartView, defStyleAttr, defStyleRes)
        isMonth = typedArray.getBoolean(R.styleable.BarChartView_month, false)
        typedArray.recycle()
    }

    private fun setColorsAndCheckArray() {
        val randomColors = arrayListOf(ContextCompat.getColor(context, R.color.nice_black), ContextCompat.getColor(context, R.color.nice_blue), ContextCompat.getColor(context, R.color.nice_purple), ContextCompat.getColor(context, R.color.nice_green), ContextCompat.getColor(context, R.color.nice_orange), ContextCompat.getColor(context, R.color.nice_orange), ContextCompat.getColor(context, R.color.nice_yellow))
        val map = barChart?.getMap()
        if (map != null) {
            var maxSize = 0
            for (i in map.values) {
                if (maxSize < i.size)
                    maxSize = i.size
            }

            if (barChart?.getValuesForMap() != null) {
                if (barChart?.getValuesForMap()?.size != maxSize)
                    throw IncorrectArraySize()
            }

            // colors set by order and then choose random
            for (i in 0 until maxSize) {
                val rand = if (randomColors.size-1 < i) {
                    randomColors.random()
                } else {
                    randomColors[i]
                }
                colors.add(rand)
            }
        }
    }

    private fun initPaints() {
        rectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        rectPaint.color = ContextCompat.getColor(context, R.color.nice_black)
        rectPaint.style = Paint.Style.FILL
        rectPaint.strokeWidth = 3F

        strokeRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokeRectPaint.color = ContextCompat.getColor(context, R.color.white)
        strokeRectPaint.style = Paint.Style.FILL

        dottedPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        dottedPaint.style = Paint.Style.STROKE
        dottedPaint.color = Color.GRAY
        dottedPaint.strokeWidth = 1F
        dottedPaint.pathEffect = DashPathEffect(floatArrayOf(10F, 10F), 0f)

        textPaint = Paint()
        textPaint.style = Paint.Style.FILL
        textPaint.color = Color.GRAY
        textPaint.textSize = 30F
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val setWidth = max(oneColumnSize * columnsCount + paddingLeft + paddingRight + leftRightPadding * 2, minWidth)
        val setHeight = max( maxValueHeight + topBottomPadding * 2 + paddingBottom + paddingTop, minHeight)

        setMeasuredDimension(
            resolveSize(setWidth, widthMeasureSpec),
            resolveSize(setHeight, heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setSizes()
    }

    private fun setSizes() {
        val map = barChart?.getMap()
        if (map != null) {
            columnsCount = map.size
        }

        val realWidth = width - paddingRight - paddingLeft - leftRightPadding * 2
        val realHeight = height - paddingTop - paddingBottom
        cellWidth = realWidth / columnsCount
        cellPadding = cellWidth * 0.2F

        val fieldWidth = cellWidth * columnsCount - leftRightPadding * 2
        val fieldHeight = maxValueHeight + topBottomPadding * 2

        // center view
        fieldRect.left = (paddingLeft + (realWidth - fieldWidth) / 2).toFloat()
        fieldRect.top = (paddingTop + (realHeight - fieldHeight) / 2).toFloat()
        fieldRect.right = fieldRect.left + fieldWidth + leftRightPadding * 2
        fieldRect.bottom = fieldRect.top + fieldHeight
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setColorsAndCheckArray()

        drawCharts(canvas)
        drawDividers(canvas)
        drawDates(canvas)
        drawSideNums(canvas)
        drawSelectedRect(canvas)
    }

    private fun drawSideNums(canvas: Canvas) {
        var yFirstNum = fieldRect.top + maxValueHeight
        val yLastNum = fieldRect.top + topBottomPadding + cellPadding
        val differ = (yFirstNum - yLastNum) / sideNumsCount

        var nums = 0
        val numsDiffer = maxSum / sideNumsCount
        for (i in 0..sideNumsCount) {
            canvas.drawText("$nums", (leftRightPadding/2).toFloat(), yFirstNum, textPaint)
            yFirstNum -= differ
            nums += numsDiffer
        }
    }

    private fun drawCharts(canvas: Canvas) {
        val map = barChart?.getMap()
        if (map != null) {
            // max sum of one chart
            for (i in map.values) {
                val sum = i.sum()
                if (maxSum < sum)
                    maxSum = sum
            }
            var num = 0
            for (i in map.values) {
                val rec1 = getCellRect(num)
                for ((index, j) in i.withIndex()) {
                    // value how much of field get = value from array * value of 100% height / sum
                    val pixels = j * (maxValueHeight - topBottomPadding - cellPadding) / maxSum
                    rec1.top = rec1.bottom - pixels
                    if (colors.size != 0)
                        rectPaint.color = colors[index]
                    canvas.drawRect(rec1, rectPaint)
                    rec1.bottom = rec1.top
                }
                num += 1
            }
        }
    }

    private fun drawDividers(canvas: Canvas) {
        val yStart = fieldRect.top + topBottomPadding + cellPadding
        val yEnd = yStart + maxValueHeight - topBottomPadding - cellPadding
        for (i in 0..columnsCount) {
            val x = fieldRect.left + cellWidth * i
            canvas.drawLine(x, yStart, x, yEnd, dottedPaint)
        }
    }

    private fun drawDates(canvas: Canvas) {
        val map = barChart?.getMap()
        if (map != null) {
            val k = map.keys.toTypedArray()
            for (i in 0 until columnsCount) {
                val rec1 = getCellRect(i)
                canvas.drawText(k[i], (rec1.left + rec1.right) / 2, rec1.bottom + cellPadding + topBottomPadding/2, textPaint)
            }
        }
    }

    private fun drawSelectedRect(canvas: Canvas) {
        if (selectedColumn != -1 && chartCenter != -1) {
            strokeRectPaint.color = ContextCompat.getColor(context, R.color.white)
            strokeRectPaint.style = Paint.Style.FILL
            canvas.drawRect((selectedColumn*cellWidth + fieldRect.left), chartCenter-100F, (selectedColumn*cellWidth + cellWidth + fieldRect.left), chartCenter.toFloat(), strokeRectPaint)

            strokeRectPaint.color = ContextCompat.getColor(context, R.color.nice_black)
            strokeRectPaint.style = Paint.Style.STROKE
            canvas.drawRect((selectedColumn*cellWidth + fieldRect.left), chartCenter-100F, (selectedColumn*cellWidth + cellWidth + fieldRect.left), chartCenter.toFloat(), strokeRectPaint)

            val rectCenterX = ((selectedColumn*cellWidth + fieldRect.left) + (selectedColumn*cellWidth + cellWidth + fieldRect.left)) / 2
            val rectCenterY = (chartCenter-100F + chartCenter) / 2

            val nameText = barChart?.getValuesForMap()?.get(numOfChart)

            canvas.drawText("$nameText", rectCenterX, rectCenterY-10, textPaint)
            canvas.drawText("$chartAmount", rectCenterX, rectCenterY+30, textPaint)
        }
    }

    private fun getCellRect(num: Int): RectF {
        val cellRect = RectF()
        cellRect.left = fieldRect.left + cellPadding + num * cellWidth
        cellRect.top = fieldRect.top + topBottomPadding + cellPadding
        cellRect.right = cellRect.left + cellWidth - cellPadding * 2
        cellRect.bottom = cellRect.top + maxValueHeight - topBottomPadding - cellPadding
        return cellRect
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                updateCurrentCell(event)
                return true
            }
        }
        return false
    }

    private fun updateCurrentCell(event: MotionEvent) {
        selectedColumn = ((event.x - fieldRect.left + leftRightPadding) / cellWidth).toInt() - 1

        val map = barChart?.getMap()
        var ind = false
        if (map != null && selectedColumn < columnsCount && selectedColumn >= 0) {
            val neededArray = map.values.toTypedArray()[selectedColumn]
            var bottomDot = fieldRect.top + maxValueHeight

            for ((index, i) in neededArray.withIndex()) {
                val pixels = i * (maxValueHeight - topBottomPadding - cellPadding) / maxSum
                val topDot = bottomDot - pixels
                if (event.y > topDot && event.y < bottomDot) {
                    chartCenter = ((bottomDot + topDot)/2).toInt()
                    chartAmount = i
                    numOfChart = index
                    invalidate()
                    ind = true
                }
                bottomDot = topDot
            }

            if (!ind) {
                selectedColumn = -1
                chartCenter = -1
                invalidate()
            }
        }
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }
}