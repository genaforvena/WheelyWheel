package org.imozerov.wheel

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout

class WheelsLayout : LinearLayout {
    private val wheel0: WheelView
    private val wheel1: WheelView
    private val wheel2: WheelView
    private val wheel3: WheelView
    private val wheel4: WheelView

    private var lastNumber: Int = 0

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        wheel4 = WheelView(context)
        addView(wheel4)

        wheel3 = WheelView(context)
        addView(wheel3)

        wheel2 = WheelView(context)
        addView(wheel2)

        wheel1 = WheelView(context)
        addView(wheel1)

        wheel0 = WheelView(context)
        addView(wheel0)
    }

    fun showNumber(number: Int) {
        if (number == lastNumber) {
            return
        }
        val digits = extractDigits(number)
        digits.forEachIndexed { index, value -> setWheelValue(index, value, lastNumber < number) }
        lastNumber = number
    }

    private fun extractDigits(number: Int): List<Int> {
        val digits = mutableListOf<Int>()
        var remaining = number
        for (i in 0..4) {
            if (remaining > 0) {
                digits.add(remaining % 10)
                remaining /= 10
            } else {
                digits.add(0)
            }
        }
        return digits
    }

    private fun setWheelValue(index: Int, value: Int, increment: Boolean) {
        when (index) {
            0 -> wheel0.showItem(value, increment)
            1 -> wheel1.showItem(value, increment)
            2 -> wheel2.showItem(value, increment)
            3 -> wheel3.showItem(value, increment)
            4 -> wheel4.showItem(value, increment)
        }
    }
}