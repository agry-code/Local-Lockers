package com.example.locallockers.model
data class LockerModel(
    val id: String = "",
    val name: String,
    val location: String,
    val capacity: Int,
    val openHours: String,
    val owner: String
) {
    fun toMap(): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = mutableMapOf(
            "name" to name,
            "location" to location,
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
