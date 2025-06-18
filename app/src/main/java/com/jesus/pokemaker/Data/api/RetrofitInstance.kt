package com.jesus.pokemaker.Data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

/**
 * Objeto singleton para manejar la instancia de Retrofit
 */
object RetrofitInstance {

    // Configuración de JSON con kotlinx.serialization
    private val json = Json {
        ignoreUnknownKeys = true // Ignora campos desconocidos en el JSON
        coerceInputValues = true // Convierte valores null a valores por defecto
    }

    // URL base de la PokeAPI
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    // Instancia de Retrofit configurada con kotlinx.serialization
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    // Instancia del servicio de la API
    val apiService: PokeApiService = retrofit.create(PokeApiService::class.java)
}
