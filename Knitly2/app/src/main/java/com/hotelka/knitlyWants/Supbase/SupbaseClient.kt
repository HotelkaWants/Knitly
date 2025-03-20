package com.hotelka.knitlyWants.Supbase

import com.hotelka.knitlyWants.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
val url = BuildConfig.SUPABASE_URL
val key = BuildConfig.SUPABASE_ANON_KEY

