package com.kt_media.ui.statistical

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kt_media.R
import com.kt_media.databinding.ActivityStatisticalBinding
import com.kt_media.domain.constant.CHILD_DAY_OF_USE
import com.kt_media.domain.constant.CHILD_USER_ID
import com.kt_media.domain.entities.DayOfUse
import com.kt_media.domain.entities.MonthOfUse
import com.kt_media.ui.dialog.MonthYearPickerDialog
import com.mymusic.ui.adapters.DayOfUseAdapter
import com.mymusic.ui.adapters.MonthOfUseAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StatisticalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticalBinding
    private lateinit var dbRefDayOfUse: DatabaseReference
    private lateinit var dayOfUseAdapter: DayOfUseAdapter
    private lateinit var monthOfUseAdapter: MonthOfUseAdapter
    private var mode = 0
    private var dayOfUseList = arrayListOf<DayOfUse>()
    private var monthOfUseList = arrayListOf<MonthOfUse>()
    private var userId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRefDayOfUse = FirebaseDatabase.getInstance().getReference(CHILD_DAY_OF_USE)
        userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
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
                    getDayOfUse("01-$start", "31-$end")
                    binding.recStatisticalSa.adapter=monthOfUseAdapter
                }else {
                    dayOfUseAdapter= DayOfUseAdapter()
                    getDayOfUse(start, end)
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
            setListener { view, year, month, dayOfMonth ->
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

    private fun getDayOfUse(startDate: String, endDate: String) {
        val query = dbRefDayOfUse.orderByChild(CHILD_USER_ID).equalTo(userId)
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val startMillis = dateFormat.parse(startDate)?.time ?: 0
        val endMillis = dateFormat.parse(endDate)?.time ?: Long.MAX_VALUE

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dayOfUseList.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val dayOfUse = data.getValue(DayOfUse::class.java)
                    val date = dayOfUse?.date
                    val dateToMillis = date?.let { dateFormat.parse(it)?.time } ?: 0
                    if (dateToMillis in startMillis..endMillis) {
                        dayOfUse?.let { dayOfUseList.add(it) }
                    }
                }
                if(dayOfUseList.isNotEmpty()){
                    dayOfUseList.sortBy { dateFormat.parse(it.date)?.time }
                    if(mode==0){
                        monthOfUseList= dayOfUseList.groupByMonth()
                        monthOfUseAdapter.submit(monthOfUseList)
                    }else{
                        dayOfUseAdapter.submit(dayOfUseList)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
    fun ArrayList<DayOfUse>.groupByMonth(): ArrayList<MonthOfUse> {
        val monthList = ArrayList<MonthOfUse>()
        val monthMap = mutableMapOf<String, MonthOfUse>()

        for (dayOfUse in this) {
            val monthKey = dayOfUse.date.substring(3, 10)
            val existingMonth = monthMap[monthKey]

            if (existingMonth == null) {
                monthMap[monthKey] = MonthOfUse(month = monthKey, usedMinute = dayOfUse.usedMinute, playSongTime = dayOfUse.playSongTime)
            } else {
                monthMap[monthKey] = existingMonth.copy(
                    usedMinute = existingMonth.usedMinute + dayOfUse.usedMinute,
                    playSongTime = existingMonth.playSongTime + dayOfUse.playSongTime
                )
            }
        }
        monthList.addAll(monthMap.values.sortedBy { it.month })
        return monthList
    }
}