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

class SearchViewModel : ViewModel(){
    private val _lat = MutableLiveData<Double>()
    val lat: LiveData<Double> = _lat

    private val _long = MutableLiveData<Double>()
    val long: LiveData<Double> = _long
    var address by mutableStateOf("")
        private set
    var show by mutableStateOf(false)
        private set

    // Nuevos estados para Huesped
    private val _location = MutableLiveData<String>()
    val location: LiveData<String> = _location

    fun onLocationChanged(location: String) {
        _location.value = location
    }

    fun getLocation(search:String){
        viewModelScope.launch {
            val apiKey = "AIzaSyBTA_bquHKAeJfOOLKNX-RxaA4_Cr7iPao"

            val url = "https://maps.googleapis.com/maps/api/geocode/json?address=$search&key=$apiKey"
            val response = withContext(Dispatchers.IO){
                URL(url).readText()
            }

            val results = Gson().fromJson(response,GoogleGeoResults::class.java)

            if(results.results.isNotEmpty()){
                show = true
                _lat.value = results.results[0].geometry.location.lat
                _long.value = results.results[0].geometry.location.lng
                address = results.results[0].formatted_address
            }else{
                Log.d("SearchViewModel","No funciona")
            }
        }
    }
}