package com.example.locallockers.ui.theme.views.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeleteViewModel : ViewModel(){
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    fun deleteGuestAndLocker(guestId: String, lockerId: String?) {
        val guestRef = db.collection("Users").document(guestId)

        db.runBatch { batch ->
            batch.delete(guestRef)
            if (lockerId != null) {
                val lockerRef = db.collection("Lockers").document(lockerId)
                batch.delete(lockerRef)
            }
        }.addOnSuccessListener {
            Log.d("DeleteViewModel", "Guest and locker deleted successfully")
        }.addOnFailureListener { e ->
            Log.e("DeleteViewModel", "Error deleting guest and locker", e)
        }
    }
}

