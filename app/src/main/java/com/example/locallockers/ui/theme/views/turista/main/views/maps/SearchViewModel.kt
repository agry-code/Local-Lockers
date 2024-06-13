package com.example.locallockers.ui.theme.views.turista.main.views.maps

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.locallockers.model.GoogleGeoResults
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

// ViewModel para la búsqueda de ubicaciones
class SearchViewModel : ViewModel() {
    // LiveData para almacenar la latitud de la ubicación
    private val _lat = MutableLiveData<Double>()
    val lat: LiveData<Double> = _lat

    // LiveData para almacenar la longitud de la ubicación
    private val _long = MutableLiveData<Double>()
    val long: LiveData<Double> = _long

    // Estado mutable para almacenar la dirección de la ubicación
    var address by mutableStateOf("")
        private set

    // Estado mutable para controlar la visibilidad de la dirección
    var show by mutableStateOf(false)
        private set

    // Nuevos estados para Huésped
    // LiveData para almacenar la ubicación en formato de texto
    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    // Método llamado cuando la ubicación cambia
    fun onLocationChanged(location: String) {
        _location.value = location
        // Llama a getLocation para obtener la latitud y longitud de la ubicación
        getLocation(location)
    }

    // Método para obtener la latitud y longitud de una ubicación mediante la API de Google Geocoding
    fun getLocation(search: String) {
        viewModelScope.launch {
            try {
                // Clave de la API de Google Maps Geocoding
                val apiKey = "AIzaSyBTA_bquHKAeJfOOLKNX-RxaA4_Cr7iPao"

                // Construye la URL de la solicitud de geocodificación
                val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$search&key=$apiKey"
                // Realiza la solicitud HTTP y obtiene la respuesta como texto
                val response = withContext(Dispatchers.IO) {
                    URL(url).readText()
                }

                // Convierte la respuesta JSON en un objeto GoogleGeoResults utilizando Gson
                val results = Gson().fromJson(response, GoogleGeoResults::class.java)

                // Si se encontraron resultados de geocodificación
                if (results.results.isNotEmpty()) {
                    show = true
                    // Actualiza la latitud y longitud con los valores obtenidos
                    _lat.value = results.results[0].geometry.location.lat
                    _long.value = results.results[0].geometry.location.lng
                    // Almacena la dirección formateada
                    address = results.results[0].formatted_address
                } else {
                    Log.d("SearchViewModel", "No se encontraron resultados para la búsqueda")
                }
            } catch (e: Exception) {
                // Registra cualquier error que ocurra durante la solicitud de geocodificación
                Log.e("SearchViewModel", "Error al obtener la ubicación", e)
            }
        }
    }
}