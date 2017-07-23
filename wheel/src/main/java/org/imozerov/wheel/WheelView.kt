package org.imozerov.wheel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View


class WheelView : View {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        val textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14F, resources.displayMetrics)

        topBottomTextPaint.color = resources.getColor(android.R.color.darker_gray)
        topBottomTextPaint.isAntiAlias = true
        topBottomTextPaint.typeface = Typeface.MONOSPACE
        topBottomTextPaint.textSize = textSize

        centerTextPaint.color = resources.getColor(android.R.color.black)
        centerTextPaint.isAntiAlias = true
        centerTextPaint.textScaleX = 1.05f
        centerTextPaint.typeface = Typeface.MONOSPACE
        centerTextPaint.textSize = textSize

        centerLinePaint.color = resources.getColor(android.R.color.black)
        centerLinePaint.isAntiAlias = true
        centerLinePaint.typeface = Typeface.MONOSPACE
        centerLinePaint.textSize = textSize

        measureTextWidthHeight()

        val halfCircumference = (maxTextHeight.toFloat() * lineSpacingMultiplier * (items.size / 2 + 1).toFloat()).toInt()
        circularDiameter = (halfCircumference * 2 / Math.PI).toInt()
        circularRadius = (halfCircumference / Math.PI).toInt()
    }

    companion object {
        private val UPDATE_RATE = 60L
    }

    private val items = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    private var currentShownItem = 3

    private val topBottomTextPaint = Paint()  //paint that draw top and bottom showItem
    private val centerTextPaint = Paint()  // paint that draw center showItem
    private val centerLinePaint = Paint()  // paint that draw line besides center showItem

    private var widgetWidth: Int = 0
    private var maxTextWidth: Int = 0
    private var maxTextHeight: Int = 0
    private val lineSpacingMultiplier = 2.0f

    private var itemHeight: Float = 0F
    private var paddingLeftRight: Int = 0
    private var paddingTopBottom: Int = 0
    private var circularDiameter: Int = 0
    private var circularRadius: Int
    private var topLineY: Int = 0
    private var bottomLineY: Int = 0

    private var heightMode: Int = View.MeasureSpec.UNSPECIFIED

    private fun measureTextWidthHeight() {
        val rect = Rect()

        for (i in items.indices) {
            val s1 = items[i].toString()

            centerTextPaint.getTextBounds(s1, 0, s1.length, rect)

            val textWidth = rect.width()
            val textHeight = rect.height()

            maxTextWidth = if (textWidth > maxTextWidth) textWidth else maxTextWidth
            maxTextHeight = if (textHeight > maxTextHeight) textHeight else maxTextHeight
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        widgetWidth = measuredWidth
        val widgetHeight = measuredHeight

        heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        itemHeight = lineSpacingMultiplier * maxTextHeight
        paddingLeftRight = (widgetWidth - maxTextWidth) / 2
        paddingTopBottom = (widgetHeight - circularDiameter) / 2

        topLineY = ((circularDiameter - itemHeight) / 2.0f).toInt() + paddingTopBottom
        bottomLineY = ((circularDiameter + itemHeight) / 2.0f).toInt() + paddingTopBottom

        setMeasuredDimension(circularRadius, circularDiameter)
    }

    override fun onDraw(canvas: Canvas) {
        items.forEachIndexed { index, item ->
            canvas.save()
            val itemHeight = maxTextHeight * lineSpacingMultiplier
            val radian = ((itemHeight * index) / circularRadius).toDouble()
            val angle = (radian * 180 / Math.PI).toFloat()

            if (angle >= 180f || angle <= 0f) {
                canvas.restore()
            } else {
                val translateY = (circularRadius.toDouble() - Math.cos(radian) * circularRadius - Math.sin(radian) * maxTextHeight / 2).toInt() + paddingTopBottom

                canvas.translate(0.0f, translateY.toFloat())
                canvas.scale(1.0f, Math.sin(radian).toFloat())

                if (translateY <= topLineY || maxTextHeight + translateY >= bottomLineY) {
                    val diff = if (translateY <= topLineY) topLineY - translateY else bottomLineY - translateY

                    val topBottomPaint = if (translateY <= topLineY) topBottomTextPaint else centerTextPaint
                    val centerPaint = if (translateY <= topLineY) centerTextPaint else topBottomTextPaint

                    canvas.save()
                    canvas.clipRect(0, 0, widgetWidth, diff)
                    canvas.drawText(item.toString(), paddingLeftRight.toFloat(), maxTextHeight.toFloat(), topBottomPaint)
                    canvas.restore()
                    canvas.save()
                    canvas.clipRect(0, diff, widgetWidth, itemHeight.toInt())
                    canvas.drawText(item.toString(), paddingLeftRight.toFloat(), maxTextHeight.toFloat(), centerPaint)
                    canvas.restore()
                } else if (translateY >= topLineY && maxTextHeight + translateY <= bottomLineY) {
                    canvas.clipRect(0, 0, widgetWidth, itemHeight.toInt())
                    canvas.drawText(item.toString(), paddingLeftRight.toFloat(), maxTextHeight.toFloat(), centerTextPaint)
                }

                canvas.restore()
            }
        }
    }

    fun showItem(value: Int, increment: Boolean) {
        if (currentShownItem == value) {
            return
        }
        if (increment) {
            postDelayed(MoveForward(value), UPDATE_RATE)
        } else {
            postDelayed(MoveBackward(value), UPDATE_RATE)
        }
    }

    private inner class MoveForward(private val value: Int) : Runnable {
        override fun run() {
            moveItemsForward()
            invalidate()
            if (currentShownItem != value) {
                postDelayed(MoveForward(value), UPDATE_RATE)
            }
        }
    }

    private inner class MoveBackward(private val value: Int) : Runnable {
        override fun run() {
            moveItemsBackward()
            invalidate()
            if (currentShownItem != value) {
                postDelayed(MoveBackward(value), UPDATE_RATE)
            }
        }
    }

    private fun moveItemsForward() {
        val item0 = items[0]

        var i = 0
        while (i < items.size - 1) {
            items[i] = items[i + 1]
            i += 1
        }
        items[9] = item0
        if (currentShownItem < items.lastIndex) currentShownItem ++ else currentShownItem = 0
    }

    private fun moveItemsBackward() {
        val item9 = items.last()

        var i = items.size - 1
        while (i > 0) {
            items[i] = items[i - 1]
            i -= 1
        }
        items[0] = item9
        if (currentShownItem > 0) currentShownItem -- else currentShownItem = 9
    }
}