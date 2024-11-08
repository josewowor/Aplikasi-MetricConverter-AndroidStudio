package com.example.metricconverter

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private lateinit var spMetrics: Spinner
    private lateinit var spOriginal: Spinner
    private lateinit var spConvert: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultText: TextView

    //Fungsi hitung satuan + deteksi satuan yg dipilih di spinner 2 dan spinner 3
    fun calculateResult(input: Double, index: Int, index2: Int, index3: Int) {
        var index4: Int
        var result = input
        var unit = ""

        val units = when (index) {
            1 -> listOf("Milimeter", "Centimeter", "Desimeter", "Meter", "Dekameter", "Hektometer", "Kilometer")
            2 -> listOf("Miligram", "Centigram", "Desigram", "Gram", "Dekagram", "Hektogram", "Kilogram")
            3 -> listOf("Milisekon", "Centisekon", "Desisekon", "Sekon", "Dekasekon", "Hektosekon", "Kilosekon")
            4 -> listOf("Miliampere", "Centiampere", "Desiampere", "Ampere", "Dekaampere", "Hektoampere", "Kiloampere")
            5 -> listOf("Celsius", "Fahrenheit", "Kelvin")
            else -> listOf()
        }

        if (index == 1 || index == 2 || index == 3 || index == 4) {
            index4 = index2 - index3
            if (index4 > 0) {
                for (x in 1..index4)
                    result *= 10
            } else if (index4 < 0) {
                index4 = abs(index4)
                for (x in 1..index4)
                    result /= 10
            }
            unit = units[index3]
        } else if (index == 5) { // Temperature conversions
            result = when {
                (index2 == 0 && index3 == 1) -> (input * 9 / 5) + 32 // Celsius to Fahrenheit
                (index2 == 0 && index3 == 2) -> input + 273.15 // Celsius to Kelvin
                (index2 == 1 && index3 == 0) -> (input - 32) * 5 / 9 // Fahrenheit to Celsius
                (index2 == 1 && index3 == 2) -> ((input - 32) * 5 / 9) + 273.15 // Fahrenheit to Kelvin
                (index2 == 2 && index3 == 0) -> input - 273.15 // Kelvin to Celsius
                (index2 == 2 && index3 == 1) -> ((input - 273.15) * 9 / 5) + 32 // Kelvin to Fahrenheit
                else -> input
            }
            unit = units[index3]
        }

        // Determine whether to display result as an integer or decimal
        val resultTextValue = if (result % 1.0 == 0.0) {
            getString(R.string.conversion_result, result.toInt().toString(), unit) // Whole number as string
        } else {
            getString(R.string.conversion_result, String.format(Locale.US, "%.2f", result), unit) // Decimal number with Locale.US
        }

        resultText.text = resultTextValue
    }






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //Mencari teks input + teks hasil di xml
        inputValue = findViewById(R.id.getInputValue)
        resultText = findViewById(R.id.resultText)

        //Ubah list spinner 2 3 mengikuti sesuai dengan metrik yg dipilih spinner 1
        val lengths = listOf("Milimeter", "Centimeter", "Desimeter", "Meter", "Dekameter", "Hektometer", "Kilometer")
        val mass = listOf("Miligram", "Centigram", "Desigram", "Gram", "Dekagram", "Hektogram", "Kilogram")
        val time = listOf("Milisekon", "Centisekon", "Desisekon", "Sekon", "Dekasekon", "Hektosekon", "Kilosekon")
        val electricCurrent = listOf("Miliampere", "Centiampere", "Desiampere", "Ampere", "Dekaampere", "Hektoampere", "Kiloampere")
        val temperature = listOf("Celcius", "Fahrenheit", "Kelvin")

        val adapterLengths = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, lengths)
        val adapterMass = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mass)
        val adapterTime = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, time)
        val adapterElectricCurrent = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, electricCurrent)
        val adapterTemperature = ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, temperature)

        var indexMetric = 0
        var indexMetric2 = 0
        var indexMetric3 = 0

        var getInputNum = 0.0

        //Mencari id spinner ke 2 satuan di xml + disable sementara
        spOriginal = findViewById(R.id.spOriginal)
        spOriginal.isEnabled = false

        //Mencari id spinner ke 3 satuan di xml + disable sementara
        spConvert = findViewById(R.id.spConvert)
        spConvert.isEnabled = false

        //Mencari id spinner ke 1 metrik di xml + melakukan deteksi posisi
        spMetrics = findViewById(R.id.spMetrics)
        spMetrics.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position != 0) {
                    spOriginal.isEnabled = true
                    spConvert.isEnabled = true
                }
                when (position) {
                    1 -> {
                        spOriginal.adapter = adapterLengths
                        spConvert.adapter = adapterLengths
                    }
                    2 -> {
                        spOriginal.adapter = adapterMass
                        spConvert.adapter = adapterMass
                    }
                    3 -> {
                        spOriginal.adapter = adapterTime
                        spConvert.adapter = adapterTime
                    }
                    4 -> {
                        spOriginal.adapter = adapterElectricCurrent
                        spConvert.adapter = adapterElectricCurrent
                    }
                    5 -> {
                        spOriginal.adapter = adapterTemperature
                        spConvert.adapter = adapterTemperature
                    }
                }
                indexMetric = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }



        spOriginal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                indexMetric2 = position
                if(getInputNum != 0.0 && indexMetric3 != 0)
                    calculateResult(getInputNum, indexMetric, indexMetric2, indexMetric3)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        spConvert.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                indexMetric3 = position
                if(getInputNum != 0.0 && indexMetric2 != 0)
                    calculateResult(getInputNum, indexMetric, indexMetric2, indexMetric3)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        //Membuat event listener di teks input onchange => dari charseq => string => double => fungsi menghitung
        inputValue.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val getString: String = ""+s
                val getNum: Double = getString.toDouble()
                getInputNum = getNum
                calculateResult(getNum, indexMetric, indexMetric2, indexMetric3)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })



    }
}