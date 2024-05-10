package com.example.locallockers.model

data class LockerModel(
    var id: String = "",
    val name: String = "",
    val location: String = "",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val capacity: Int = 0,
    val openHours: String = "",
    val owner: String = ""
)
 {
    fun toMap(): MutableMap<String, Any?> {
        val map: MutableMap<String, Any?> = mutableMapOf(
            "name" to name,
            "location" to location, //a lo mejor hay qe quitarlo
            "latitude" to latitude,  // Incluir latitude
            "longitude" to longitude,  // Incluir longitude
            "capacity" to capacity as Any,  // Forzar como Any
            "openHours" to openHours,
            "owner" to owner
        )
        if (id.isNotEmpty()) {
            map["id"] = id
        }
        return map
    }
}
