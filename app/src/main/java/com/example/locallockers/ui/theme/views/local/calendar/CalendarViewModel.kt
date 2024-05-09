package com.example.locallockers.ui.theme.views.local.calendar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {
    private val _selectedDate = MutableLiveData<Long>(System.currentTimeMillis())
    val selectedDate: LiveData<Long> = _selectedDate

    fun onDateSelected(newDate: Long) {
        _selectedDate.value = newDate
    }
}