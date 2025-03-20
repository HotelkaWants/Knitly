package com.hotelka.knitlyWants.Supbase

import android.util.Log
import com.hotelka.knitlyWants.supabase
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseAuthenticationHelper.Companion.getFirebaseIdToken
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.user.UserInfo
import okhttp3.internal.wait

suspend fun signInWithFirebase(){
    val idToken = getFirebaseIdToken()
    if (idToken != null){
        try {
            supabase.auth.signInWith(IDToken){
                provider = Google
                this.idToken = idToken
            }
            Log.e("Succes", "Check Auth")

        } catch (e: Exception){
            Log.e("SupAuth", e.message.toString())
            Log.e("SupAuth", idToken)

        }
    } else {
        Log.e("SupAuth", "Firebase auth failed")

    }
}
fun checkAuthState(): UserInfo? {
    val session = supabase.auth.currentSessionOrNull()
    return session?.user
}
suspend fun signInWithGoogle(): UserInfo? {
    try {
        supabase.auth.signInWith(Google)
    } catch (e: Exception){
        Log.e("SupAuth", e.message.toString())
    }.wait()
    return checkAuthState()
}
suspend fun signUp(email: String, password: String): UserInfo? {
    try {
        val result = supabase.auth.signUpWith(Email){
            this.email = email
            this.password = password
        }
        return result
    } catch (e: Exception){
        Log.e("SupAuth", e.message.toString())
    }
    return null
}

suspend fun signIn(email: String, password: String): UserInfo? {
    try {
        val result = supabase.auth.signInWith(Email){
            this.email = email
            this.password = password
        }
    } catch (e: Exception){
        Log.e("SupAuth", e.message.toString())
    }.wait()
    return checkAuthState()
}

suspend fun signOut() {
    supabase.auth.signOut()
}