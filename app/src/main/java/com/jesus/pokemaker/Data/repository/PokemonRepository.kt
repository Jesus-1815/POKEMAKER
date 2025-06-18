package com.jesus.pokemaker.Data.repository

import androidx.lifecycle.LiveData
import com.jesus.pokemaker.Data.api.PokeApiService
import com.jesus.pokemaker.Data.db.PokemonDao
import com.jesus.pokemaker.Data.db.PokemonEntity
import com.jesus.pokemaker.Data.model.Pokemon
import com.jesus.pokemaker.Data.model.Type
import com.jesus.pokemaker.Data.model.Stat
import com.jesus.pokemaker.Data.model.Species
import com.jesus.pokemaker.Data.model.PokemonSprites
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

/**
 * Repositorio que maneja la lógica de acceso a datos para Pokémon.
 * Actúa como una única fuente de verdad, coordinando entre la API y la base de datos local.
 *
 * @param apiService Servicio para realizar llamadas a la PokeAPI
 * @param pokemonDao DAO para acceso a la base de datos local
 */
class PokemonRepository(
    private val apiService: PokeApiService,
    private val pokemonDao: PokemonDao
) {

    // Configuración de JSON para serialización/deserialización
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    /**
     * Busca un Pokémon en la API, lo convierte a PokemonEntity y lo guarda en la base de datos.
     *
     * @param name Nombre del Pokémon a buscar
     * @return PokemonEntity guardado en la base de datos
     * @throws Exception si hay error en la llamada a la API
     */
    suspend fun fetchAndSavePokemon(name: String): PokemonEntity {
        try {
            // 1. Llamar a la API para obtener los datos del Pokémon
            val apiPokemon = apiService.getPokemon(name.lowercase())

            // 2. Convertir los datos complejos a JSON strings
            val typesJson = json.encodeToString(apiPokemon.types)
            val statsJson = json.encodeToString(apiPokemon.stats)

            // 3. Mapear de Pokemon (API) a PokemonEntity (Base de datos)
            val pokemonEntity = PokemonEntity(
                id = apiPokemon.id.toInt(),
                name = apiPokemon.name,
                imageUrl = apiPokemon.sprites.frontDefault,
                types = typesJson,
                stats = statsJson
            )

            // 4. Guardar en la base de datos local
            pokemonDao.insertPokemon(pokemonEntity)

            // 5. Retornar la entidad guardada
            return pokemonEntity

        } catch (e: Exception) {
            throw Exception("Error al obtener el Pokémon: ${e.message}")
        }
    }

    /**
     * Obtiene todos los Pokémon guardados localmente.
     *
     * @return LiveData con la lista de todos los Pokémon locales
     */
    fun getLocalPokemons(): LiveData<List<PokemonEntity>> {
        return pokemonDao.getAllPokemons()
    }

    /**
     * Obtiene un Pokémon específico de la base de datos local.
     *
     * @param name Nombre del Pokémon a buscar
     * @return PokemonEntity si existe, null si no se encuentra
     */
    suspend fun getLocalPokemon(name: String): PokemonEntity? {
        return pokemonDao.getPokemon(name.lowercase())
    }

    /**
     * Verifica si un Pokémon existe en la base de datos local.
     *
     * @param name Nombre del Pokémon
     * @return true si existe localmente, false si no
     */
    suspend fun isPokemonSavedLocally(name: String): Boolean {
        return getLocalPokemon(name) != null
    }

    /**
     * Obtiene un Pokémon, primero buscando localmente y luego en la API si no existe.
     *
     * @param name Nombre del Pokémon
     * @return PokemonEntity obtenido local o remotamente
     */
    suspend fun getPokemon(name: String): PokemonEntity {
        // Primero buscar localmente
        val localPokemon = getLocalPokemon(name)

        return if (localPokemon != null) {
            // Si existe localmente, devolverlo
            localPokemon
        } else {
            // Si no existe localmente, buscarlo en la API y guardarlo
            fetchAndSavePokemon(name)
        }
    }

    /**
     * Obtiene el conteo total de Pokémon guardados.
     *
     * @return Número total de Pokémon en la base de datos
     */
    suspend fun getPokemonCount(): Int {
        return pokemonDao.getPokemonCount()
    }

    /**
     * Convierte un PokemonEntity (base de datos) de vuelta a Pokemon (modelo de API).
     * Útil cuando la UI necesita trabajar con el formato completo de la API.
     *
     * @param entity PokemonEntity de la base de datos
     * @return Pokemon reconstruido con datos de la API
     */
    fun convertPokemonEntityToApiPokemon(entity: PokemonEntity): Pokemon {
        try {
            // Deserializar los JSON strings de vuelta a objetos
            val types = json.decodeFromString<List<Type>>(entity.types)
            val stats = json.decodeFromString<List<Stat>>(entity.stats)

            // Crear un objeto Species básico para los campos requeridos
            val species = Species(
                name = entity.name,
                url = "https://pokeapi.co/api/v2/pokemon-species/${entity.id}/"
            )

            // Crear sprites básicos
            val sprites = PokemonSprites(
                frontDefault = entity.imageUrl,
                backDefault = "",
                frontShiny = "",
                backShiny = ""
            )

            // Reconstruir el objeto Pokemon
            return Pokemon(
                id = entity.id.toLong(),
                name = entity.name,
                height = 0L, // Valores por defecto para campos no guardados
                weight = 0L,
                baseExperience = 0L,
                order = 0L,
                isDefault = true,
                locationAreaEncounters = "",
                abilities = emptyList(), // Lista vacía para campos no guardados
                cries = com.jesus.pokemaker.Data.model.Cries("", ""),
                forms = listOf(species),
                gameIndices = emptyList(),
                heldItems = emptyList(),
                moves = emptyList(),
                pastAbilities = emptyList(),
                pastTypes = kotlinx.serialization.json.JsonArray(emptyList()),
                species = species,
                sprites = sprites,
                stats = stats,
                types = types
            )

        } catch (e: Exception) {
            throw Exception("Error al convertir PokemonEntity a Pokemon: ${e.message}")
        }
    }

    /**
     * Busca múltiples Pokémon y los guarda en la base de datos.
     *
     * @param pokemonNames Lista de nombres de Pokémon
     * @return Lista de PokemonEntity guardados exitosamente
     */
    suspend fun fetchAndSaveMultiplePokemons(pokemonNames: List<String>): List<PokemonEntity> {
        val savedPokemons = mutableListOf<PokemonEntity>()

        pokemonNames.forEach { name ->
            try {
                val savedPokemon = fetchAndSavePokemon(name)
                savedPokemons.add(savedPokemon)
            } catch (e: Exception) {
                // Log del error pero continúa con los demás Pokémon
                println("Error al guardar $name: ${e.message}")
            }
        }

        return savedPokemons
    }

    /**
     * Elimina un Pokémon de la base de datos local.
     *
     * @param name Nombre del Pokémon a eliminar
     */
    suspend fun deleteLocalPokemon(name: String) {
        pokemonDao.deletePokemonByName(name.lowercase())
    }

    /**
     * Elimina todos los Pokémon de la base de datos.
     */
    suspend fun deleteAllPokemons() {
        pokemonDao.deleteAllPokemons()
    }
}