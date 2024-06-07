package com.example.locallockers.model

// Data class que representa un modelo de taquilla.
data class LockerModel(
    var id: String = "",  // Identificador único de la taquilla.
    val name: String = "",  // Nombre de la taquilla.
    val latitude: Double? = 0.0,  // Latitud de la ubicación de la taquilla.
    val longitude: Double? = 0.0,  // Longitud de la ubicación de la taquilla.
    val openHours: String = "",  // Horas de apertura de la taquilla.
    val owner: String = ""  // Propietario de la taquilla.
) {
    // Función para convertir el modelo de taquilla a un mapa mutable.
    fun toMap(): MutableMap<String, Any?> {
        // Crear un mapa mutable con las propiedades de la taquilla.
        val map: MutableMap<String, Any?> = mutableMapOf(
            "name" to name,  // Añadir el nombre al mapa.
            "latitude" to latitude,  // Añadir la latitud al mapa.
            "longitude" to longitude,  // Añadir la longitud al mapa.
            "openHours" to openHours,  // Añadir las horas de apertura al mapa.
            "owner" to owner  // Añadir el propietario al mapa.
        )
        // Añadir el identificador si no está vacío.
        if (id.isNotEmpty()) {
            map["id"] = id
        }
        // Devolver el mapa resultante.
        return map
    }
}
