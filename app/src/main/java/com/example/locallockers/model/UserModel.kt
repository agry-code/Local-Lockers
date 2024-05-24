package com.example.locallockers.model

data class UserModel(
    val email: String = "",
    val role: String = "",
    val userId: String = "",
    val userName: String = "",
    val lockerId: String = ""
){
    fun toMap() : MutableMap<String,Any>{
        return mutableMapOf(
            "userId" to this.userId,
            "email" to this.email,
            "userName" to this.userName,
            "role" to this.role,
        )
    }

}
