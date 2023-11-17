package com.kt_media.ui.statistical

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kt_media.R
import com.kt_media.databinding.ActivityStatisticalBinding
import com.kt_media.ui.dialog.MonthYearPickerDialog
import com.mymusic.ui.adapters.DayOfUseAdapter
import com.mymusic.ui.adapters.MonthOfUseAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StatisticalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticalBinding
    private lateinit var dayOfUseAdapter: DayOfUseAdapter
    private lateinit var monthOfUseAdapter: MonthOfUseAdapter
    private var mode = 0
    private val statisticalViewModel: StatisticalViewModel by viewModel()
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
        binding.btStatisticalSa.setOnClickListener {
            val start=binding.tvStartSa.text.toString()
            val end=binding.tvEndSa.text.toString()
            if(start.isNotEmpty() && end.isNotEmpty()){
                if(mode==0){
                    monthOfUseAdapter= MonthOfUseAdapter()
                    statisticalViewModel.getDayOfUseList("01-$start", "31-$end")
                    statisticalViewModel.dayOfUseList.observe(this, Observer { dayOfUseList ->
                        if (dayOfUseList.isNotEmpty()) {
                            monthOfUseAdapter.submit(statisticalViewModel.groupByMonth(dayOfUseList))
                        }
                    })
                    binding.recStatisticalSa.adapter=monthOfUseAdapter
                }else {
                    dayOfUseAdapter= DayOfUseAdapter()
                    statisticalViewModel.getDayOfUseList(start,end)
                    statisticalViewModel.dayOfUseList.observe(this, Observer { dayOfUseList ->
                        if (dayOfUseList.isNotEmpty()) {
                            dayOfUseAdapter.submit(dayOfUseList)
                        }
                    })
                    binding.recStatisticalSa.adapter=dayOfUseAdapter
                }
                binding.recStatisticalSa.layoutManager=
                    LinearLayoutManager(this, RecyclerView.VERTICAL,false)
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
        MonthYearPickerDialog().apply {
            setListener { _, year, month, _ ->
                val monthString = if (month < 9) {
                    "0" + (month + 1).toString()
                } else {
                    (month + 1).toString()
                }
                if (check == 0) {
                    binding.tvStartSa.text = "$monthString-$year"
                } else {
                    binding.tvEndSa.text = "$monthString-$year"
                }
            }
            show(supportFragmentManager, "MonthYearPickerDialog")
        }
    }
}