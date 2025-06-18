package com.jesus.pokemaker.ui.viewmodel // Asegúrate que el paquete sea correcto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jesus.pokemaker.Data.api.RetrofitInstance // Importa tu RetrofitInstance
import com.jesus.pokemaker.Data.db.PokemonDatabase
import com.jesus.pokemaker.Data.db.PokemonEntity
import com.jesus.pokemaker.Data.repository.PokemonRepository
import kotlinx.coroutines.launch
import android.util.Log // Importa Log para depuración, si no lo tienes

/**
 * ViewModel que maneja la lógica de la UI para los Pokémon
 */
class PokemonViewModel(application: Application) : AndroidViewModel(application) {

    // Instancias del DAO y Repositorio
    private val pokemonDao = PokemonDatabase.getDatabase(application).pokemonDao()
    // ¡CORRECCIÓN AQUÍ! Pasa RetrofitInstance.api (el nombre correcto)
    private val repository = PokemonRepository(RetrofitInstance.apiService, pokemonDao)

    // LiveData para observar la lista de Pokémon
    val pokemonList: LiveData<List<PokemonEntity>> = repository.getLocalPokemons()

    // LiveData para manejar estados de carga y errores
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _searchResults = MutableLiveData<List<PokemonEntity>>()
    val searchResults: LiveData<List<PokemonEntity>> = _searchResults

    /**
     * Busca un Pokémon por nombre y lo guarda en la base de datos
     */
    fun searchAndSavePokemon(pokemonName: String) {
        if (pokemonName.isBlank()) {
            _error.value = "Por favor ingresa un nombre de Pokémon"
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            try {
                // Aquí el repository.fetchAndSavePokemon() ya usa apiService que viene del constructor
                val pokemon = repository.fetchAndSavePokemon(pokemonName.trim().lowercase()) // Convertir a minúsculas para API
                _error.value = "¡${pokemon.name.replaceFirstChar { it.uppercase() }} guardado exitosamente!"
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("PokemonViewModel", "Error al buscar y guardar Pokémon: ${e.message}", e) // Log para depuración
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene un Pokémon específico (local o remoto)
     */
    fun getPokemon(pokemonName: String, callback: (PokemonEntity?) -> Unit) {
        _isLoading.value = true // Opcional: mostrar carga para esta operación
        viewModelScope.launch {
            try {
                // Aquí el repository.getPokemon() ya usa apiService que viene del constructor
                val pokemon = repository.getPokemon(pokemonName.trim().lowercase()) // Convertir a minúsculas
                callback(pokemon)
            } catch (e: Exception) {
                callback(null)
                _error.value = "Error al obtener el Pokémon: ${e.message}"
                Log.e("PokemonViewModel", "Error al obtener Pokémon: ${e.message}", e) // Log para depuración
            } finally {
                _isLoading.value = false // Opcional: ocultar carga
            }
        }
    }

    /**
     * Busca Pokémon por nombre (búsqueda local)
     */
    fun searchLocalPokemons(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                // Para una búsqueda local eficiente, lo ideal sería agregar un @Query en PokemonDao
                // que filtre por nombre. Por ahora, si 'pokemonList.value' ya contiene todos los pokemones
                // locales, puedes filtrarlos en memoria.
                // Ejemplo de @Query en DAO (lo añadirías a PokemonDao):
                // @Query("SELECT * FROM pokemons WHERE name LIKE '%' || :query || '%'")
                // fun searchPokemonsByName(query: String): LiveData<List<PokemonEntity>>

                val currentList = pokemonList.value ?: emptyList() // Obtiene la lista actual de LiveData
                val filtered = currentList.filter {
                    it.name.contains(query.lowercase()) // Filtra por nombre (insensible a mayúsculas/minúsculas)
                }
                _searchResults.value = filtered
                Log.d("PokemonViewModel", "Búsqueda local para '${query}' encontró ${filtered.size} resultados.")
            } catch (e: Exception) {
                _error.value = "Error en la búsqueda local: ${e.message}"
                Log.e("PokemonViewModel", "Error en searchLocalPokemons: ${e.message}", e) // Log para depuración
            }
        }
    }

    /**
     * Limpia los mensajes de error
     */
    fun clearError() {
        _error.value = ""
    }

    /**
     * Precarga algunos Pokémon populares para demostración
     */
    fun loadInitialPokemons() {
        val popularPokemons = listOf(
            "bulbasaur", "ivysaur", "venusaur", "charmander",
            "charmeleon", "charizard", "squirtle", "wartortle",
            "blastoise", "caterpie"
        )

        _isLoading.value = true // Indicar que la carga inicial está en progreso
        viewModelScope.launch {
            try {
                repository.fetchAndSaveMultiplePokemons(popularPokemons)
                _error.value = "Carga inicial de Pokémon completada."
                Log.d("PokemonViewModel", "Carga inicial de Pokémon completada.")
            } catch (e: Exception) {
                _error.value = "Error al cargar Pokémon iniciales: ${e.message}"
                Log.e("PokemonViewModel", "Error en loadInitialPokemons: ${e.message}", e) // Log para depuración
            } finally {
                _isLoading.value = false // Carga inicial terminada
            }
        }
    }
}