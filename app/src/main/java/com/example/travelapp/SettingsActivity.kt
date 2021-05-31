package com.example.travelapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.travelapp.databinding.ActivitySettingsBinding
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {


    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupTextSizePicker()
        setupColorsPicker()
        setupRadiusPicker()

        saveButton.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupTextSizePicker() {
        val textSizes = resources.getStringArray(R.array.TextSize)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, textSizes)
        binding.textSize.adapter = adapter
        binding.textSize.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> Shared.textSize = 50F
                    1 -> Shared.textSize = 60F
                    2 -> Shared.textSize = 80F
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.textSize.setSelection(translateResourceIDForTextSize(Shared.textSize))
    }

    private fun translateResourceIDForTextSize(textSize: Float): Int {
        var position = 0
        when (textSize) {
            50F -> position = 0
            60F -> position = 1
            80F -> position = 2
        }
        return position
    }

    private fun setupColorsPicker() {
        val colors = resources.getStringArray(R.array.Colors)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, colors)
        binding.colorPicker.adapter = adapter
        binding.colorPicker.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> Shared.textColor = Color.BLACK
                    1 -> Shared.textColor = Color.RED
                    2 -> Shared.textColor = Color.WHITE
                    3 -> Shared.textColor = Color.BLUE
                    4 -> Shared.textColor = Color.YELLOW
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        binding.colorPicker.setSelection(translateResourceIDForColorPicker(Shared.textColor))
    }

    private fun translateResourceIDForColorPicker(textColor: Int): Int {
        var position = 0
        when (textColor) {
            Color.BLACK -> position = 0
            Color.RED -> position = 1
            Color.WHITE -> position = 2
            Color.BLUE -> position = 3
            Color.YELLOW -> position = 4
        }
        return position
    }

    private fun setupRadiusPicker() {
        val radiuses = resources.getStringArray(R.array.Radiuses)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, radiuses)
        binding.radiusPicker.adapter = adapter
        binding.radiusPicker.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                when (position) {
                    0 -> Shared.radius = 500F
                    1 -> Shared.radius = 1000F
                    2 -> Shared.radius = 1500F
                    3 -> Shared.radius = 2000F
                    4 -> Shared.radius = 5000F
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        binding.radiusPicker.setSelection(translateResourceIDForRadiusPicker(Shared.radius))
    }

    private fun translateResourceIDForRadiusPicker(radius: Float): Int {
        var position = 0
        when (radius) {
            500F -> position = 0
            1000F -> position = 1
            1500F -> position = 2
            2000F -> position = 3
            5000F -> position = 4
        }
        return position
    }
}