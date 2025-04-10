package com.hotelka.knitlyWants.FirebaseUtils

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.exceptions.ClearCredentialException
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.hotelka.knitlyWants.Auth.RegisterActivity
import com.hotelka.knitlyWants.Auth.UpdateUI
import kotlinx.coroutines.tasks.await

class FirebaseAuthenticationHelper {

    companion object {

        suspend fun getFirebaseIdToken(): String? {
            val user = FirebaseAuth.getInstance().currentUser
            return user?.getIdToken(true)?.await()?.token
        }

        fun sendPasswordReset(email: String, onComplete: () -> Unit) {
            val auth = FirebaseAuth.getInstance()
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    onComplete()
                }
            }
        }

        fun createUserWithEmailAndPassword(
            email: String,
            password: String,
            activity: ComponentActivity,
            navController: NavHostController
        ): Task<AuthResult?> {
            val auth = FirebaseAuth.getInstance()
            return auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseDB.checkExistUser(
                            task.result.user?.uid.toString(),
                            onExist = { UpdateUI(task.result.user!!, activity) },
                            onNotExist = {
                                FirebaseDB.createUser(task.result.user!!)
                                navController.navigate("infoScreen")
                            }) { id ->
                            var prefs = activity.getSharedPreferences("Prefs", Context.MODE_PRIVATE)
                            val editor = prefs.edit()
                            editor.putString("idSelected", id)
                            editor.apply()
                        }
                    }
                }
        }

        fun signInUserWithEmailAndPassword(
            email: String,
            password: String,
            activity: RegisterActivity,
        ): Task<AuthResult?> {
            val auth = FirebaseAuth.getInstance()
            return auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    UpdateUI(it.result.user, activity)
                }
            }
        }

        suspend fun signOutGoogle(
            credentialManager: CredentialManager,
            startActivity: ComponentActivity
        ) {
            try {
                FirebaseAuth.getInstance().signOut()
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                UpdateUI(null, startActivity)
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }
}