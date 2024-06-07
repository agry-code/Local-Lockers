package com.example.locallockers.model

// Clase principal que encapsula los resultados de la API de Google Geocoding.
data class GoogleGeoResults(
    val results: List<Results>// Lista de resultados devueltos por la API.
)

// Clase que representa cada resultado de la API de Google Geocoding.
data class Results(
    val geometry: Geometry, // Información de geometría que incluye la ubicación.
    val formatted_address: String // Dirección formateada según lo devuelto por la API.
)
// Clase que encapsula la información de la geometría de un resultado.
data class Geometry(
    val location: Location // Ubicación específica con latitud y longitud.
)

// Clase que representa una ubicación con latitud y longitud.
data class Location (
    val lat: Double, // Latitud de la ubicación.
    val lng: Double  // Longitud de la ubicación.
)