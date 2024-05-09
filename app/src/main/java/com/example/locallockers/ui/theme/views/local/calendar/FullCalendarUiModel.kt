package com.example.locallockers.ui.theme.views.local.calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Stable
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
@Stable
class FullCalendarUiModel(
    val yearMonth: YearMonth,
    val visibleDates: List<LocalDate>
) {

    val startDate: LocalDate = visibleDates.first()
    val endDate: LocalDate = visibleDates.last()
}
