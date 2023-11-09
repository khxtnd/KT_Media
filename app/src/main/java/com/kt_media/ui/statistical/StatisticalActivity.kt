package com.kt_media.ui.statistical

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import by.dzmitry_lakisau.month_year_picker_dialog.MonthYearPickerDialog
import com.kt_media.R
import com.kt_media.databinding.ActivityStatisticalBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StatisticalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticalBinding
    private var mode = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBackSa.setOnClickListener {
            finish()
        }

        binding.btMonthSa.setOnClickListener {
            mode = 0
            binding.btMonthSa.setBackgroundResource(R.drawable.bg_btn_filter)
            binding.btMonthSa.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding.btDateSa.setBackgroundResource(R.drawable.bg_btn_outline)
            binding.btDateSa.setTextColor(ContextCompat.getColor(this, R.color.geek_blue_6))
            binding.tvStartSa.text = ""
            binding.tvEndSa.text = ""
        }

        binding.btDateSa.setOnClickListener {
            mode = 1
            binding.btMonthSa.setBackgroundResource(R.drawable.bg_btn_outline)
            binding.btMonthSa.setTextColor(ContextCompat.getColor(this, R.color.geek_blue_6))

            binding.btDateSa.setBackgroundResource(R.drawable.bg_btn_filter)
            binding.btDateSa.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.tvStartSa.text = ""
            binding.tvEndSa.text = ""
        }
        binding.ivStartSa.setOnClickListener {
            if(mode==0){
                showMonthPickerDialog(0)
            }else{
                showDatePickerDialog(0)
            }
        }
        binding.ivEndSa.setOnClickListener {
            if(mode==0){
                showMonthPickerDialog(1)
            }else{
                showDatePickerDialog(1)
            }
        }
    }

    private fun showDatePickerDialog(check: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)

                val formattedDate =
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate.time)
                if (check == 0) {
                    binding.tvStartSa.text = formattedDate
                } else {
                    binding.tvEndSa.text = formattedDate
                }
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun showMonthPickerDialog(check: Int) {
        MonthYearPickerDialog.Builder(
            context = this,
            themeResId = R.style.Style_MonthYearPickerDialog,
            onDateSetListener = { year, month ->
                val formattedMonth = String.format("%02d-%04d", month,year)
                if (check == 0) {
                    binding.tvStartSa.text = formattedMonth
                } else {
                    binding.tvEndSa.text = formattedMonth
                }
            }
        )
            .build().show()
    }
}