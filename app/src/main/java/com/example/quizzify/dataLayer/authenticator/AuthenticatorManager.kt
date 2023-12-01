package com.example.quizzify.dataLayer.authenticator

import android.util.Log
import com.example.quizzify.dataLayer.database.DatabaseRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

object AuthenticatorManager {

    var firebaseAuth: FirebaseAuth? = null
    var database: DatabaseRepositoryImpl? = null

    fun setup(db: DatabaseRepositoryImpl, fAuth: FirebaseAuth){
        firebaseAuth = fAuth
        database = db
    }

    fun reset(){
        firebaseAuth = null
        database = null
    }

    /**
     * Logs in or creates a new user using Spotify credentials.
     *
     * @param id The Spotify user ID.
     * @param password The password associated with the Spotify account.
     * @throws FirebaseAuthException if there is an error with Firebase authentication.
     * @throws Exception if there is an error during user creation.
     */
    suspend fun logInFromSpotify(id: String, password: String) {
        Log.d("AUTHENTICATOR", "signInWithEmail:password $password")
        try {
            if (firebaseAuth == null || database == null)
                setup(DatabaseRepositoryImpl(Firebase.firestore), FirebaseAuth.getInstance())
            firebaseAuth!!.signInWithEmailAndPassword("$id@quizzify.com", password).await()
            UserData.spotifyId = id
            UserData.uid = firebaseAuth!!.currentUser!!.uid
            Log.d("AUTHENTICATOR", "signInWithEmail:success")
        } catch (e: Exception) {
            Log.d("AUTHENTICATOR", "signInWithEmail:failure")
            try {
                firebaseAuth!!.createUserWithEmailAndPassword("$id@quizzify.com", password).await()
                if (firebaseAuth!!.currentUser == null) {
                    Log.d("AUTHENTICATOR", "createUserWithEmail:failure")
                    throw RuntimeException("Error in User Creation")
                } else {
                    runBlocking {
                        UserData.spotifyId = id
                        UserData.uid = firebaseAuth!!.currentUser!!.uid
                        database!!.createUser()
                    }
                    Log.d("AUTHENTICATOR", "createUserWithEmail:success")
                }
            } catch (e: Exception) {
                Log.d("AUTHENTICATOR", "createUserWithEmail:failure")
                throw RuntimeException("Error in User Creation")
            }
        }
    }
}