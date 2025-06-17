package com.jesus.pokemaker.Data.repository // Asegúrate de que el paquete sea correcto

import androidx.lifecycle.LiveData
import com.jesus.pokemaker.Data.api.PokeApiService // Importa tu servicio de API
import com.jesus.pokemaker.Data.db.PokemonDao // Importa tu DAO de Room
import com.jesus.pokemaker.Data.db.PokemonEntity // Importa tu entidad de Room
import com.jesus.pokemaker.Data.model.* // Importa todas tus clases modelo de la API (Pokemon, Species, etc.)
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json // Importa Json para serialización/deserialización
import kotlinx.serialization.decodeFromString // Para deserializar

/**
 * Repositorio de datos para la aplicación de Pokémon.
 * Actúa como una única fuente de verdad para los datos, abstrayendo si provienen de la red o de la base de datos local.
 *
 * @param apiService La interfaz de servicio de la API (Retrofit).
 * @param pokemonDao El Data Access Object de Room para operaciones de base de datos.
 */
class PokemonRepository(private val apiService: PokeApiService, private val pokemonDao: PokemonDao) {

    // Instancia de Json para serializar/deserializar objetos complejos a/desde String.
    // Es la misma configuración que usas en RetrofitInstance.
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * Obtiene un Pokémon por su nombre. Primero intenta obtenerlo de la red,
     * lo guarda en la base de datos local y luego devuelve la entidad.
     * Si no hay conexión o falla la API, puedes añadir lógica para buscarlo localmente.
     *
     * @param name El nombre del Pokémon a buscar.
     * @return El PokemonEntity guardado o recuperado.
     */
    suspend fun fetchAndSavePokemon(name: String): PokemonEntity {
        // Lógica para obtener de la API:
        // En un proyecto real, aquí agregarías un 'try-catch' y lógica de conectividad
        // para decidir si llamar a la API o buscar directamente en la DB local.
        // Por simplicidad, esta implementación primero intenta la red.

        val apiPokemon = apiService.getPokemon(name)

        // Serializar las listas complejas de la API a String (JSON) para Room
        val typesJson = json.encodeToString(apiPokemon.types)
        val statsJson = json.encodeToString(apiPokemon.stats)

        // Crear una instancia de PokemonEntity a partir del objeto de la API
        val pokemonEntity = PokemonEntity(
            id = apiPokemon.id.toInt(), // Asegúrate de que el ID de la API (Long) se convierta a Int si es necesario
            name = apiPokemon.name,
            imageUrl = apiPokemon.sprites.frontDefault ?: "", // Usa el sprite frontal por defecto, o cadena vacía si es nulo
            types = typesJson,
            stats = statsJson
        )

        // Insertar el Pokémon en la base de datos local
        pokemonDao.insertPokemon(pokemonEntity)

        return pokemonEntity // Devolvemos la entidad guardada/actualizada
    }

    /**
     * Obtiene una especie de Pokémon por su nombre desde la API y la devuelve.
     * (Nota: esta función no guarda en DB, es solo un ejemplo de cómo podrías usarla)
     *
     * @param name El nombre de la especie a buscar.
     * @return El objeto PokemonSpecies de la API.
     */
    suspend fun getPokemonSpeciesFromApi(name: String): PokemonSpecies {
        return apiService.getSpecies(name)
    }

    /**
     * Obtiene una cadena de evolución por su ID desde la API y la devuelve.
     *
     * @param id El ID de la cadena de evolución.
     * @return El objeto EvolutionChain de la API.
     */
    suspend fun getEvolutionChainFromApi(id: Int): Evolution { // Evolution es el nombre de tu clase principal de EvolutionChain
        return apiService.getEvolutionChain(id)
    }

    /**
     * Obtiene información de un tipo por su ID desde la API y la devuelve.
     *
     * @param id El ID del tipo.
     * @return El objeto TypeResponse de la API.
     */
    suspend fun getTypeInfoFromApi(id: Int): TypeResponse {
        return apiService.getTypeInfo(id)
    }


    /**
     * Obtiene todos los Pokémon guardados localmente en la base de datos.
     *
     * @return LiveData de una lista de PokemonEntity.
     */
    fun getLocalPokemons(): LiveData<List<PokemonEntity>> = pokemonDao.getAllPokemons()

    /**
     * Obtiene un Pokémon específico de la base de datos local por su nombre.
     *
     * @param name El nombre del Pokémon a buscar.
     * @return El PokemonEntity encontrado localmente, o null.
     */
    suspend fun getLocalPokemon(name: String): PokemonEntity? = pokemonDao.getPokemon(name)

    /**
     * Función de utilidad para convertir un PokemonEntity (desde la DB) de nuevo a un objeto Pokemon (similar al de la API).
     * Esto es útil si tu ViewModel o UI esperan el formato de la API.
     */
    fun convertPokemonEntityToApiPokemon(entity: PokemonEntity): Pokemon {
        // Deserializar las cadenas JSON a sus objetos originales
        val typesList: List<Type> = json.decodeFromString(entity.types)
        val statsList: List<Stat> = json.decodeFromString(entity.stats)

        // Crear una instancia de Pokemon (la clase de tu modelo de la API)
        // Nota: Tendrás que reconstruir también los objetos Sprites si la clase Pokemon
        // los requiere. En este ejemplo, estamos usando una imagen simple para PokemonEntity.
        // Si tu modelo Pokemon API tiene más campos de sprites que quieres mantener,
        // necesitarías más lógica aquí o ajustar PokemonEntity.
        val sprites = Sprites(frontDefault = entity.imageUrl) // Simplicado para este ejemplo

        return Pokemon(
            id = entity.id.toLong(), // Asegúrate de que el ID se convierta a Long si es necesario
            name = entity.name,
            height = 0, // Placeholder si no está en Entity, o agrega a Entity si es importante
            weight = 0, // Placeholder
            sprites = sprites,
            types = typesList,
            stats = statsList,
            // Agrega aquí el resto de campos de Pokemon (API) si son necesarios
            // Asegúrate de que tu modelo Pokemon (API) no tenga más campos obligatorios que no puedas llenar.
            baseExperience = 0, cries = Cries("", ""), forms = emptyList(), gameIndices = emptyList(),
            heldItems = emptyList(), isDefault = false, locationAreaEncounters = ""
        )
    }
}