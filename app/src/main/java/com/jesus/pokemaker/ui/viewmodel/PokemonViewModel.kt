package com.jesus.pokemaker.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jesus.pokemaker.Data.api.RetrofitInstance
import com.jesus.pokemaker.Data.db.PokemonDatabase
import com.jesus.pokemaker.Data.db.PokemonEntity
import com.jesus.pokemaker.Data.repository.PokemonRepository
import kotlinx.coroutines.launch
import android.util.Log
import com.jesus.pokemaker.Data.model.Evolution
import com.jesus.pokemaker.Data.model.PokemonSpecies

class PokemonViewModel(application: Application) : AndroidViewModel(application) {

    private val pokemonDao = PokemonDatabase.getDatabase(application).pokemonDao()
    private val repository = PokemonRepository(RetrofitInstance.apiService, pokemonDao)

    val pokemonList: LiveData<List<PokemonEntity>> = repository.getLocalPokemons()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _searchResults = MutableLiveData<List<PokemonEntity>>()
    val searchResults: LiveData<List<PokemonEntity>> = _searchResults

    fun searchAndSavePokemon(pokemonName: String) {
        if (pokemonName.isBlank()) {
            _error.value = "Por favor ingresa un nombre de Pokémon"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pokemon = repository.getPokemon(pokemonName.trim().lowercase())
                _error.value = "¡${pokemon.name.replaceFirstChar { it.uppercase() }} guardado exitosamente!"
                Log.d("PokemonViewModel", "Pokémon '${pokemonName}' guardado/obtenido exitosamente.")
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("PokemonViewModel", "Error al buscar y guardar Pokémon '${pokemonName}': ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPokemon(pokemonName: String, callback: (PokemonEntity?) -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val pokemon = repository.getPokemon(pokemonName.trim().lowercase())
                callback(pokemon)
                Log.d("PokemonViewModel", "Pokémon '${pokemonName}' obtenido a través de callback.")
            } catch (e: Exception) {
                callback(null)
                _error.value = "Error al obtener el Pokémon: ${e.message}"
                Log.e("PokemonViewModel", "Error al obtener el Pokémon '${pokemonName}': ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchLocalPokemons(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val currentList = pokemonList.value ?: emptyList()
                val filtered = currentList.filter {
                    it.name.contains(query.lowercase())
                }
                _searchResults.value = filtered
                Log.d("PokemonViewModel", "Búsqueda local para '${query}' encontró ${filtered.size} resultados.")
            } catch (e: Exception) {
                _error.value = "Error en la búsqueda local: ${e.message}"
                Log.e("PokemonViewModel", "Error en searchLocalPokemons: ${e.message}", e)
            }
        }
    }

    fun clearError() {
        _error.value = ""
    }

    fun loadInitialPokemons() {
        val popularPokemons = listOf(
            "bulbasaur", "ivysaur", "venusaur", "charmander",
            "charmeleon", "charizard", "squirtle", "wartortle",
            "blastoise", "caterpie"
        )

        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.fetchAndSaveMultiplePokemons(popularPokemons)
                _error.value = "Carga inicial de Pokémon completada."
                Log.d("PokemonViewModel", "Carga inicial de Pokémon completada.")
            } catch (e: Exception) {
                _error.value = "Error al cargar Pokémon iniciales: ${e.message}"
                Log.e("PokemonViewModel", "Error en loadInitialPokemons: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // MÉTODOS CORREGIDOS - MOVIDOS FUERA DE loadInitialPokemons()
    suspend fun getSpeciesDetails(pokemonName: String): PokemonSpecies? {
        return try {
            Log.d("PokemonViewModel", "Obteniendo species details para: $pokemonName")
            repository.getSpeciesDetails(pokemonName)
        } catch (e: Exception) {
            Log.e("PokemonViewModel", "Error obteniendo species details para '$pokemonName': ${e.message}", e)
            null
        }
    }

    suspend fun getEvolutionChainDetails(chainId: Int): Evolution? {
        return try {
            Log.d("PokemonViewModel", "Obteniendo evolution chain para ID: $chainId")
            repository.getEvolutionChainDetails(chainId)
        } catch (e: Exception) {
            Log.e("PokemonViewModel", "Error obteniendo evolution chain para ID '$chainId': ${e.message}", e)
            null
        }
    }
}