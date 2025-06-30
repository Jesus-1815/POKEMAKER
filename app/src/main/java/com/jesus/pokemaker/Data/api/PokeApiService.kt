package com.jesus.pokemaker.Data.api

import com.jesus.pokemaker.Data.model.Evolution
import com.jesus.pokemaker.Data.model.Pokemon
import com.jesus.pokemaker.Data.model.PokemonSpecies
import com.jesus.pokemaker.Data.model.PokemonType
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz del servicio de la PokeAPI usando Retrofit
 */
interface PokeApiService {

    /**
     * Obtiene información básica de un Pokémon por su nombre o ID
     */
    @GET("pokemon/{name}")
    suspend fun getPokemon(@Path("name") name: String): Pokemon

    /**
     * Obtiene información de especies de un Pokémon
     */
    @GET("pokemon-species/{name}")
    suspend fun getPokemonSpecies(@Path("name") name: String): PokemonSpecies

    /**
     * Obtiene información de un tipo de Pokémon
     */
    @GET("type/{name}")
    suspend fun getPokemonType(@Path("name") name: String): PokemonType
    @GET("pokemon-species/{name}")
    suspend fun getSpeciesDetails(@Path("name") name: String): PokemonSpecies

    @GET("evolution-chain/{id}")
    suspend fun getEvolutionChainDetails(@Path("id") id: Int): Evolution
}
