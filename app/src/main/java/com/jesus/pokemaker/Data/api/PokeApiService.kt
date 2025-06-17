package com.jesus.pokemaker.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {

    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): Pokemon

    @GET("pokemon-species/{name}")
    suspend fun getSpecies(@Path("name") name: String): Species

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChain(@Path("id") id: Int): EvolutionChain

    @GET("type/{id}")
    suspend fun getTypeInfo(@Path("id") id: Int): TypeResponse
}
