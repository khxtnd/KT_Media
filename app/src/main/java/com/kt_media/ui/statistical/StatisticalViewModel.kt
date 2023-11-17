package com.kt_media.ui.statistical

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kt_media.domain.entities.DayOfUse
import com.kt_media.domain.entities.MonthOfUse
import com.kt_media.domain.usecase.GetDayOfUseListUseCase
import kotlinx.coroutines.launch

class StatisticalViewModel(
    private val getDayOfUseListUseCase: GetDayOfUseListUseCase
): ViewModel() {
    private val _dayOfUseList = MutableLiveData<List<DayOfUse>>()
    val dayOfUseList: LiveData<List<DayOfUse>> get() = _dayOfUseList

    fun getDayOfUseList(startDate: String, endDate: String) {
        viewModelScope.launch {
            _dayOfUseList.value = getDayOfUseListUseCase.execute(startDate, endDate)
        }
    }
    fun groupByMonth(list: List<DayOfUse>): ArrayList<MonthOfUse> {
        val monthList = ArrayList<MonthOfUse>()
        val monthMap = mutableMapOf<String, MonthOfUse>()

        for (dayOfUse in list) {
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