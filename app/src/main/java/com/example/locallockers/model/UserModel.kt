package com.example.locallockers.model

// Data class que representa un modelo de usuario.
data class UserModel(
    val email: String = "",  // Correo electrónico del usuario.
    val role: String = "",  // Rol del usuario (ejemplo: administrador, turista).
    val userId: String = "",  // Identificador único del usuario.
    val userName: String = "",  // Nombre del usuario.
    val lockerId: String = ""  // Identificador de la taquilla asignada al usuario.
) {
    // Función que convierte el modelo de usuario a un mapa mutable.
    fun toMap(): MutableMap<String, Any> {
        // Retorna un mapa mutable con las propiedades del usuario.
        return mutableMapOf(
            "userId" to this.userId,  // Añade el identificador del usuario al mapa.
            "email" to this.email,  // Añade el correo electrónico del usuario al mapa.
            "userName" to this.userName,  // Añade el nombre del usuario al mapa.
            "role" to this.role  // Añade el rol del usuario al mapa.
        )
    }
}
