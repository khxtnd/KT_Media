package com.kt_media.ui.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.kt_media.R
import com.kt_media.databinding.DialogMonthYearPickerBinding
import com.kt_media.domain.constant.TITLE_SELECT_MONTH
import java.util.Calendar
import java.util.Date

class MonthYearPickerDialog(private val date: Date = Date()) : DialogFragment() {

    companion object {
        private const val MAX_YEAR = 2050
        private const val MIN_YEAR = 2020
    }

    private lateinit var binding: DialogMonthYearPickerBinding

    private var listener: DatePickerDialog.OnDateSetListener? = null

    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogMonthYearPickerBinding.inflate(requireActivity().layoutInflater)
        val cal: Calendar = Calendar.getInstance().apply { time = date }

        binding.pickerMonth.run {
            minValue = 0
            maxValue = 11
            value = cal.get(Calendar.MONTH)
            displayedValues = arrayOf(
                "01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"
            )
        }

        binding.pickerYear.run {
            val year = cal.get(Calendar.YEAR)
            minValue = MIN_YEAR
            maxValue = MAX_YEAR
            value = year
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(TITLE_SELECT_MONTH)
            .setView(binding.root)
            .setPositiveButton(R.string.ok) { _, _ ->
                listener?.onDateSet(
                    null,
                    binding.pickerYear.value,
                    binding.pickerMonth.value,
                    1
                )
            }
            .setNegativeButton(R.string.back) { _, _ -> dialog?.cancel() }
            .create()
    }
}