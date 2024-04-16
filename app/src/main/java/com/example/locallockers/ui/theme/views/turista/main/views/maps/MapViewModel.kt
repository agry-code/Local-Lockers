package com.example.locallockers.ui.theme.views.turista.main.views.maps

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MapViewModel : ViewModel(){

    private val auth: FirebaseAuth = Firebase.auth
    fun signOut(){
     auth.signOut()
    }
}