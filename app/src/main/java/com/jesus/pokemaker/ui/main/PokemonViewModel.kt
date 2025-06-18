package com.jesus.pokemaker.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jesus.pokemaker.Data.db.PokemonEntity
import com.jesus.pokemaker.Data.model.Pokemon
import com.jesus.pokemaker.Data.repository.PokemonRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la lógica de negocio relacionada con Pokémon.
 * Actúa como intermediario entre la UI y el repositorio.
 *
 * @param repository Repositorio de Pokémon para acceso a datos
 */
class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {

    // LiveData para observar todos los Pokémon guardados localmente
    val allPokemons: LiveData<List<PokemonEntity>> = repository.getLocalPokemons()

    // Estados para manejo de UI
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _searchedPokemon = MutableLiveData<PokemonEntity?>()
    val searchedPokemon: LiveData<PokemonEntity?> = _searchedPokemon

    private val _pokemonCount = MutableLiveData<Int>()
    val pokemonCount: LiveData<Int> = _pokemonCount

    // Estado para indicar si una operación fue exitosa
    private val _operationSuccess = MutableLiveData<Boolean>()
    val operationSuccess: LiveData<Boolean> = _operationSuccess

    init {
        // Cargar el conteo inicial de Pokémon
        loadPokemonCount()
    }

    /**
     * Busca un Pokémon por nombre en la API y lo guarda localmente.
     *
     * @param pokemonName Nombre del Pokémon a buscar
     */
    fun searchAndSavePokemon(pokemonName: String) {
        if (pokemonName.isBlank()) {
            _errorMessage.value = "Por favor ingresa un nombre de Pokémon válido"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                // Buscar el Pokémon (primero local, luego API si no existe)
                val pokemon = repository.getPokemon(pokemonName.trim())

                _searchedPokemon.value = pokemon
                _operationSuccess.value = true

                // Actualizar el conteo
                loadPokemonCount()

            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar el Pokémon: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Obtiene un Pokémon específico de la base de datos local.
     *
     * @param pokemonName Nombre del Pokémon a buscar
     */
    fun getLocalPokemon(pokemonName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val pokemon = repository.getLocalPokemon(pokemonName.trim())
                _searchedPokemon.value = pokemon

                if (pokemon == null) {
                    _errorMessage.value = "Pokémon no encontrado en la base de datos local"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar el Pokémon local: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Verifica si un Pokémon existe en la base de datos local.
     *
     * @param pokemonName Nombre del Pokémon
     * @param onResult Callback con el resultado
     */
    fun checkIfPokemonExists(pokemonName: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val exists = repository.isPokemonSavedLocally(pokemonName.trim())
                onResult(exists)
            } catch (e: Exception) {
                _errorMessage.value = "Error al verificar el Pokémon: ${e.message}"
                onResult(false)
            }
        }
    }

    /**
     * Elimina un Pokémon de la base de datos local.
     *
     * @param pokemonName Nombre del Pokémon a eliminar
     */
    fun deletePokemon(pokemonName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteLocalPokemon(pokemonName.trim())
                _operationSuccess.value = true

                // Actualizar el conteo después de eliminar
                loadPokemonCount()

                // Limpiar el Pokémon buscado si era el que se eliminó
                if (_searchedPokemon.value?.name?.equals(pokemonName.trim(), ignoreCase = true) == true) {
                    _searchedPokemon.value = null
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al eliminar el Pokémon: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Busca y guarda múltiples Pokémon.
     *
     * @param pokemonNames Lista de nombres de Pokémon
     */
    fun searchAndSaveMultiplePokemons(pokemonNames: List<String>) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null

                val validNames = pokemonNames.filter { it.isNotBlank() }
                if (validNames.isEmpty()) {
                    _errorMessage.value = "No hay nombres válidos de Pokémon"
                    return@launch
                }

                val savedPokemons = repository.fetchAndSaveMultiplePokemons(validNames)

                if (savedPokemons.isNotEmpty()) {
                    _operationSuccess.value = true
                    loadPokemonCount()
                } else {
                    _errorMessage.value = "No se pudo guardar ningún Pokémon"
                }

            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar múltiples Pokémon: ${e.message}"
                _operationSuccess.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Convierte un PokemonEntity a Pokemon (modelo de API) para uso en UI.
     *
     * @param pokemonEntity Entidad de la base de datos
     * @return Pokemon convertido o null si hay error
     */
    fun convertToApiPokemon(pokemonEntity: PokemonEntity): Pokemon? {
        return try {
            repository.convertPokemonEntityToApiPokemon(pokemonEntity)
        } catch (e: Exception) {
            _errorMessage.value = "Error al convertir el Pokémon: ${e.message}"
            null
        }
    }

    /**
     * Carga el conteo actual de Pokémon guardados.
     */
    private fun loadPokemonCount() {
        viewModelScope.launch {
            try {
                // Necesitarás agregar este método a tu repositorio y DAO
                // val count = repository.getPokemonCount()
                // _pokemonCount.value = count
            } catch (e: Exception) {
                // Error silencioso para el conteo
            }
        }
    }

    /**
     * Limpia el mensaje de error.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Limpia el estado del Pokémon buscado.
     */
    fun clearSearchedPokemon() {
        _searchedPokemon.value = null
    }

    /**
     * Limpia el estado de éxito de operación.
     */
    fun clearOperationSuccess() {
        _operationSuccess.value = false
    }

    /**
     * Refresca todos los datos.
     */
    fun refreshData() {
        loadPokemonCount()
        // Aquí podrías agregar más operaciones de refresco si es necesario
    }

    /**
     * Factory para crear instancias del ViewModel con el repositorio.
     */
    class PokemonViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
                return PokemonViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}