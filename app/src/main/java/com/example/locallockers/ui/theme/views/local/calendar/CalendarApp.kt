package com.example.locallockers.ui.theme.views.local.calendar

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.locallockers.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarApp(
    viewModel: CalendarViewModel,
    lockerId: String,
) {

    val dataSource = CalendarDataSource()
    var data by remember { mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) }
    Log.d("LockerId", " Desde CalendarApp ${lockerId}")
    Column(modifier = Modifier.fillMaxSize()) {
        Header(
            data = data,
            onPrevClickListener = { startDate ->
                val finalStartDate = startDate.minusDays(1)
                data = dataSource.getData(
                    startDate = finalStartDate,
                    lastSelectedDate = data.selectedDate.date
                )
            },
            onNextClickListener = { endDate ->
                val finalStartDate = endDate.plusDays(2)
                data = dataSource.getData(
                    startDate = finalStartDate,
                    lastSelectedDate = data.selectedDate.date
                )
            }
        )
        Content(viewModel, data, lockerId) { date ->
            data = data.copy(
                selectedDate = date,
                visibleDates = data.visibleDates.map {
                    it.copy(
                        isSelected = it.date.isEqual(date.date)
                    )
                }
            )
        }
        Form(viewModel, lockerId, data)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Form(viewModel: CalendarViewModel, lockerId: String, data: CalendarUiModel) {
    val context = LocalContext.current
    val reservaDetails by viewModel.reservaDetails.observeAsState(emptyMap())
    val snackbarMessage by viewModel.snackbarMessage.observeAsState()

    var capacidad by remember { mutableStateOf(reservaDetails["capacidad"]?.toString() ?: "") }
    var precio by remember { mutableStateOf(reservaDetails["precio"]?.toString() ?: "") }

    LaunchedEffect(reservaDetails) {
        capacidad = reservaDetails["capacidad"]?.toString() ?: ""
        precio = reservaDetails["precio"]?.toString() ?: ""
    }

    // Mostrar Snackbar cuando el mensaje cambia
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Detalles de la Reserva",
            style = MaterialTheme.typography.titleMedium,
            color = colorResource(id = R.color.primary) // Ajuste del color del texto

        )
        OutlinedTextField(
            value = capacidad,
            onValueChange = { capacidad = it },
            label = { Text("Capacidad", color = colorResource(id = R.color.primary)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.primary),
                unfocusedBorderColor = colorResource(id = R.color.secundary),
                cursorColor = colorResource(id = R.color.primary)
            )
        )
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio (€)", color = colorResource(id = R.color.primary)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.primary),
                unfocusedBorderColor = colorResource(id = R.color.secundary),
                cursorColor = colorResource(id = R.color.primary)
            )
        )
        Button(
            onClick = { viewModel.saveLockerDetails(lockerId, data.selectedDate.date, capacidad, precio) },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.primary),
                contentColor = colorResource(id = R.color.white)
            )
        ) {
            Text("Actualizar")
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Header(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
) {
    Row {
        Text(
            text = if (data.selectedDate.isToday) {
                "Today"
            } else {
                data.selectedDate.date.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                )
            },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = colorResource(id = R.color.primary) // Ajuste del color del texto

        )
        IconButton(onClick = {
            onPrevClickListener(data.startDate.date)
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = colorResource(id = R.color.primary) // Ajuste del color del texto

            )
        }
        IconButton(onClick = {
            onNextClickListener(data.endDate.date)
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Next",
                tint = colorResource(id = R.color.primary) // Ajuste del color del ícono

            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Content(
    viewModel: CalendarViewModel,
    data: CalendarUiModel,
    lockerId: String,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
) {
    LazyRow(Modifier.fillMaxWidth()) {
        items(items = data.visibleDates) { date ->
            ContentItem(
                date = date,
                onClickListener = { selectedDate ->
                    onDateClickListener(selectedDate)
                    viewModel.loadReservaDetails(lockerId, selectedDate.date.toString())
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ContentItem(
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 4.dp)
            .clickable {
                onClickListener(date)
            }
            .width(50.dp)
            .height(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (date.isSelected) {
                colorResource(id = R.color.item) // Ajuste del color cuando está seleccionado
            } else {
                colorResource(id = R.color.secundary) // Ajuste del color cuando está seleccionado
            }
        ),
    ) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .height(48.dp)
                .padding(4.dp)
        ) {
            Text(
                text = date.day,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(id = R.color.primary) // Ajuste del color del texto

            )
            Text(
                text = date.date.dayOfMonth.toString(),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.primary) // Ajuste del color del texto

            )
        }
    }
}