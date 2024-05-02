package com.example.locallockers.model

data class LockerModel(
    val id: String,
    val name: String,
    val location: String,
    val capacity: Int,
    val openHours: String,
    val openDays: String  // Corregido para usar la sintaxis correcta de Kotlin para listas
)
