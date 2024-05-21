package com.example.mytodoapp.features.ui.datetime

import android.app.Dialog
import android.content.DialogInterface
import android.icu.util.Calendar
import android.icu.util.GregorianCalendar
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.example.mytodoapp.databinding.DialogDateTimePickerBinding
import com.example.mytodoapp.features.database.converters.DateTimeConverter
import com.example.mytodoapp.features.database.entities.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import java.time.LocalDate
import java.time.LocalTime

private const val TAG_TIME_PICKER = "TAG_TIME_PICKER"
private const val ARG_DATE = "ARG_DATE"
private const val ARG_TIME = "ARG_TIME"

class DateTimePickerDialog : DialogFragment() {
    private var date: LocalDate? = null
    private var time: LocalTime? = null

    private var _binding: DialogDateTimePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            takeIf { it.containsKey(ARG_DATE) }?.apply {
                date = DateTimeConverter.toLocalDate(getString(ARG_DATE))
            }
            takeIf { it.containsKey(ARG_TIME) }?.apply {
                time = DateTimeConverter.toLocalTime(getString(ARG_TIME))
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater

        _binding = DialogDateTimePickerBinding.inflate(inflater)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = GregorianCalendar.getInstance().timeInMillis

        var selectedDate = date

        binding.calendarView.date = dateToMillisToCalendarView(date ?: LocalDate.now())
        binding.calendarView.setOnDateChangeListener { _, year, month, day ->
            selectedDate = LocalDate.of(year, month + 1, day)
        }

        with(binding.setTimeButton) {
            updateSetTimeButtonText(this, time)
            setOnClickListener { showMaterialTimePicker(time) }
        }

        return MaterialAlertDialogBuilder(requireContext()).apply {
            setView(binding.root)
            setPositiveButton(/*R.string.confirm*/"Confirm") { dialog, _ ->
                date = selectedDate
                setFragmentResult(
                    KEY_RESULT_FROM_DATETIME, bundleOf(
                        KEY_DATE to DateTimeConverter.fromLocalDate(date ?: LocalDate.now()),
                        KEY_TIME to DateTimeConverter.fromLocalTime(time)
                    )
                )
                dialog.dismiss()
            }
            setNegativeButton(/*R.string.cancel*/"Cancel") { dialog, _ ->
                dialog.cancel()
            }

        }.create()

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setFragmentResult(
            KEY_RESULT_FROM_DATETIME, bundleOf(
                KEY_DATE to DateTimeConverter.fromLocalDate(date),
                KEY_TIME to DateTimeConverter.fromLocalTime(time)
            )
        )
    }

    private fun showMaterialTimePicker(localTime: LocalTime?) {
        var hour = localTime?.hour ?: (LocalTime.now().hour + 1)
        var minute = localTime?.minute ?: 0

        val picker =
            MaterialTimePicker.Builder()
                .setInputMode(INPUT_MODE_CLOCK)
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute)
                .build()
        picker.show(parentFragmentManager, TAG_TIME_PICKER)

        picker.addOnPositiveButtonClickListener {
            hour = picker.hour
            minute = picker.minute
            setTimeFromTimePicker(LocalTime.of(hour, minute))
            picker.dismiss()
        }

        picker.addOnNegativeButtonClickListener {
            picker.dismiss()
        }
    }

    private fun setTimeFromTimePicker(localTime: LocalTime?) {
        time = localTime
        updateSetTimeButtonText(binding.setTimeButton, time)
    }

    private fun updateSetTimeButtonText(button: MaterialButton, time: LocalTime?) {
        // FIXME: text from res
        button.text =
            if (time == null) "Set time" else Task(dueTime = time).formatDueTime(requireContext())
    }

    private fun dateToMillisToCalendarView(date: LocalDate): Long {
        val calendar = Calendar.getInstance()
        calendar[Calendar.YEAR] = date.year
        // In CalendarView months counted from 0, in LocalDate - from 1
        calendar[Calendar.MONTH] = date.monthValue - 1
        calendar[Calendar.DAY_OF_MONTH] = date.dayOfMonth
        return calendar.timeInMillis
    }

    companion object {
        val TAG: String = this::class.java.name
        const val KEY_RESULT_FROM_DATETIME = "KEY_RESULT_FROM_DATETIME"
        const val KEY_DATE = "KEY_DATE"
        const val KEY_TIME = "KEY_TIME"

        @JvmStatic
        fun newInstance(date: LocalDate?, time: LocalTime?) =
            DateTimePickerDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_DATE, DateTimeConverter.fromLocalDate(date))
                    putString(ARG_TIME, DateTimeConverter.fromLocalTime(time))
                }
            }
    }
}