package com.example.bearvbull.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreRepository {

    private val fireStoreInstance: FirebaseFirestore = Firebase.firestore

    fun getListOfActiveMarkets() {
        fireStoreInstance.firestoreSettings
    }
}