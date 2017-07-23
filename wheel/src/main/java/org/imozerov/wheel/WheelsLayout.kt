package org.imozerov.wheel

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

class WheelsLayout : LinearLayout {
    private val wheel0: TextView
    private val wheel1: TextView
    private val wheel2: TextView
    private val wheel3: TextView
    private val wheel4: TextView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        orientation = LinearLayout.HORIZONTAL
        layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        wheel4 = TextView(context)
        addView(wheel4)

        wheel3 = TextView(context)
        addView(wheel3)

        wheel2 = TextView(context)
        addView(wheel2)

        wheel1 = TextView(context)
        addView(wheel1)

        wheel0 = TextView(context)
        addView(wheel0)
    }

    fun showNumber(number: Int) {
        clearWheels()
        val digits = extractDigits(number)
        digits.forEachIndexed { index, value -> setWheelValue(index, value) }
    }

    private fun clearWheels() {
        wheel0.text = 0.toString()
        wheel1.text = 0.toString()
        wheel2.text = 0.toString()
        wheel3.text = 0.toString()
        wheel4.text = 0.toString()
    }

    private fun extractDigits(number: Int): List<Int> {
        val digits = mutableListOf<Int>()
        var remaining = number
        while (remaining > 0) {
            digits.add(remaining % 10)
            remaining /= 10
        }
        return digits
    }

    private fun setWheelValue(index: Int, value: Int) {
        when (index) {
            0 -> wheel0.text = value.toString()
            1 -> wheel1.text = value.toString()
            2 -> wheel2.text = value.toString()
            3 -> wheel3.text = value.toString()
            4 -> wheel4.text = value.toString()
        }
    }
}