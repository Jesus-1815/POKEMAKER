package com.jesus.pokemaker.ui.viewmodel // Asegúrate de que este paquete sea correcto

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.jesus.pokemaker.Data.api.RetrofitInstance // Importa tu RetrofitInstance
import com.jesus.pokemaker.Data.db.PokemonDatabase // Importa tu PokemonDatabase
import com.jesus.pokemaker.Data.db.PokemonEntity // Importa tu PokemonEntity
import com.jesus.pokemaker.Data.model.Pokemon // Importa tu modelo Pokemon de la API si lo usas en la UI
import com.jesus.pokemaker.Data.repository.PokemonRepository // Importa tu PokemonRepository
import kotlinx.coroutines.launch
import android.util.Log // Importa Log para depuración

/**
 * ViewModel que maneja la lógica de la UI para los Pokémon
 * Actúa como intermediario entre la UI (Activity/Fragment) y la capa de datos (Repository).
 */
class PokemonViewModel(application: Application) : AndroidViewModel(application) {

    // Inicializa el DAO de Room y el Repositorio al inicio del ViewModel.
    // El 'application' context se usa para obtener la base de datos.
    private val pokemonDao = PokemonDatabase.getDatabase(application).pokemonDao()
    // ¡CORRECCIÓN APLICADA AQUÍ! Se pasa RetrofitInstance.apiService al Repositorio.
    private val repository = PokemonRepository(RetrofitInstance.apiService, pokemonDao)

    // LiveData que expone la lista de todos los Pokémon guardados localmente.
    // La UI puede observar este LiveData para actualizar la lista automáticamente.
    val pokemonList: LiveData<List<PokemonEntity>> = repository.getLocalPokemons()

    // MutableLiveData para indicar el estado de carga (true/false para mostrar/ocultar un spinner).
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // MutableLiveData para exponer mensajes de error a la UI.
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // MutableLiveData para exponer los resultados de búsquedas locales a la UI.
    private val _searchResults = MutableLiveData<List<PokemonEntity>>()
    val searchResults: LiveData<List<PokemonEntity>> = _searchResults

    /**
     * Busca un Pokémon por nombre (en la API si no está localmente) y lo guarda en la base de datos.
     * Los mensajes de éxito o error se manejan a través del LiveData '_error'.
     *
     * @param pokemonName El nombre del Pokémon a buscar.
     */
    fun searchAndSavePokemon(pokemonName: String) {
        if (pokemonName.isBlank()) {
            _error.value = "Por favor ingresa un nombre de Pokémon"
            return
        }

        _isLoading.value = true // Indica que la operación está en progreso
        viewModelScope.launch { // Inicia una corrutina en el ámbito del ViewModel
            try {
                // Llama al repositorio para obtener y guardar el Pokémon.
                // Convierte el nombre a minúsculas para la API.
                val pokemon = repository.getPokemon(pokemonName.trim().lowercase())
                _error.value = "¡${pokemon.name.replaceFirstChar { it.uppercase() }} guardado exitosamente!"
                Log.d("PokemonViewModel", "Pokémon '${pokemonName}' guardado/obtenido exitosamente.")
            } catch (e: Exception) {
                // Captura y maneja cualquier excepción durante la operación.
                _error.value = "Error: ${e.message}"
                Log.e("PokemonViewModel", "Error al buscar y guardar Pokémon '${pokemonName}': ${e.message}", e)
            } finally {
                _isLoading.value = false // Finaliza la operación de carga
            }
        }
    }

    /**
     * Obtiene un Pokémon específico (primero localmente, luego de la API si no se encuentra).
     * El resultado se entrega a través de un callback.
     *
     * @param pokemonName El nombre del Pokémon a obtener.
     * @param callback Una función de callback que recibirá el PokemonEntity encontrado (o null si falla).
     */
    fun getPokemon(pokemonName: String, callback: (PokemonEntity?) -> Unit) {
        _isLoading.value = true // Opcional: mostrar carga para esta operación
        viewModelScope.launch {
            try {
                // Llama al repositorio para obtener el Pokémon.
                val pokemon = repository.getPokemon(pokemonName.trim().lowercase())
                callback(pokemon) // Devuelve el Pokémon a través del callback
                Log.d("PokemonViewModel", "Pokémon '${pokemonName}' obtenido a través de callback.")
            } catch (e: Exception) {
                callback(null) // Devuelve null si hay un error
                _error.value = "Error al obtener el Pokémon: ${e.message}"
                Log.e("PokemonViewModel", "Error al obtener el Pokémon '${pokemonName}': ${e.message}", e)
            } finally {
                _isLoading.value = false // Opcional: ocultar carga
            }
        }
    }

    /**
     * Realiza una búsqueda local de Pokémon basándose en una consulta.
     * Los resultados se actualizan en el LiveData '_searchResults'.
     *
     * @param query La cadena de texto para buscar en los nombres de los Pokémon.
     */
    fun searchLocalPokemons(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList() // Si la consulta está vacía, muestra una lista vacía
            return
        }

        viewModelScope.launch {
            try {
                // Actualmente, filtra la lista existente en memoria.
                // Para grandes conjuntos de datos, sería más eficiente una @Query específica en el DAO.
                val currentList = pokemonList.value ?: emptyList() // Obtiene la lista actual de Pokémon locales
                val filtered = currentList.filter {
                    it.name.contains(query.lowercase()) // Filtra por nombre, ignorando mayúsculas/minúsculas
                }
                _searchResults.value = filtered // Actualiza los resultados de búsqueda
                Log.d("PokemonViewModel", "Búsqueda local para '${query}' encontró ${filtered.size} resultados.")
            } catch (e: Exception) {
                _error.value = "Error en la búsqueda local: ${e.message}"
                Log.e("PokemonViewModel", "Error en searchLocalPokemons: ${e.message}", e)
            }
        }
    }

    /**
     * Limpia el mensaje de error actual.
     */
    fun clearError() {
        _error.value = ""
    }

    /**
     * Precarga una lista de Pokémon populares en la base de datos local para demostración.
     */
    fun loadInitialPokemons() {
        val popularPokemons = listOf(
            "bulbasaur", "ivysaur", "venusaur", "charmander",
            "charmeleon", "charizard", "squirtle", "wartortle",
            "blastoise", "caterpie"
        )

        _isLoading.value = true // Indica que la carga inicial está en progreso
        viewModelScope.launch {
            try {
                // Llama al repositorio para buscar y guardar múltiples Pokémon.
                repository.fetchAndSaveMultiplePokemons(popularPokemons)
                _error.value = "Carga inicial de Pokémon completada."
                Log.d("PokemonViewModel", "Carga inicial de Pokémon completada.")
            } catch (e: Exception) {
                _error.value = "Error al cargar Pokémon iniciales: ${e.message}"
                Log.e("PokemonViewModel", "Error en loadInitialPokemons: ${e.message}", e)
            } finally {
                _isLoading.value = false // Finaliza la carga inicial
            }
        }
    }
}