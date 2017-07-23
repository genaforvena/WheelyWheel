package org.imozerov.wheelywheel

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : LifecycleActivity() {
    private val handler = Handler()

    private lateinit var viewModel : MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.wheelValue().observe(this, Observer<Int> { wheelValue ->
            wheelValue?.let {
                wheel.showNumber(it)
            }
        })

        viewModel.mode().observe(this, Observer { mode ->
            when (mode) {
                Mode.TIMER -> {
                    timer.isChecked = true
                    number_to_show.visibility = View.GONE
                }
                Mode.EDIT -> {
                    pick_number.isChecked = true
                    number_to_show.visibility = View.VISIBLE
                }
                Mode.RANDOM -> {
                    random_number.isChecked = true
                    number_to_show.visibility = View.GONE
                }
            }
        })

        timer.setOnCheckedChangeListener { _, isChecked -> if (isChecked) viewModel.setMode(Mode.TIMER) }
        pick_number.setOnCheckedChangeListener { _, isChecked -> if (isChecked) {
            viewModel.setMode(Mode.EDIT)
            viewModel.safelySetNumberOrIgnore(number_to_show.text)
        }}
        random_number.setOnCheckedChangeListener { _, isChecked -> if (isChecked) viewModel.setMode(Mode.RANDOM) }

        number_to_show.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    viewModel.safelySetNumberOrIgnore(p0)
                }, 300)
            }
        })
    }

    private fun MainViewModel.safelySetNumberOrIgnore(numberStr: CharSequence?) {
        if (number_to_show.visibility != View.VISIBLE) {
            return
        }
        numberStr?.let {
            if (!it.isBlank()) {
                setValue(Integer.parseInt(numberStr.toString()))
            }
        }
    }
}
