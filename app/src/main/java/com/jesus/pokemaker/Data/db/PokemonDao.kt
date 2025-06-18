package com.jesus.pokemaker.Data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Delete

/**
 * Interfaz Data Access Object (DAO) para la entidad PokemonEntity.
 *
 * @Dao: Anotación que marca la interfaz como un DAO de Room.
 * Room implementará automáticamente esta interfaz en tiempo de compilación.
 */
@Dao
interface PokemonDao {

    /**
     * Inserta un nuevo Pokémon en la base de datos.
     * Si un Pokémon con el mismo @PrimaryKey (ID) ya existe, lo reemplaza.
     *
     * @param pokemon La instancia de PokemonEntity a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    /**
     * Inserta múltiples Pokémon en la base de datos.
     *
     * @param pokemons Lista de PokemonEntity a insertar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemons(pokemons: List<PokemonEntity>)

    /**
     * Obtiene un Pokémon específico de la base de datos por su nombre.
     *
     * @param name El nombre del Pokémon a buscar.
     * @return La instancia de PokemonEntity si se encuentra, o null si no existe.
     */
    @Query("SELECT * FROM pokemons WHERE name = :name")
    suspend fun getPokemon(name: String): PokemonEntity?

    /**
     * Obtiene un Pokémon específico de la base de datos por su ID.
     *
     * @param id El ID del Pokémon a buscar.
     * @return La instancia de PokemonEntity si se encuentra, o null si no existe.
     */
    @Query("SELECT * FROM pokemons WHERE id = :id")
    suspend fun getPokemonById(id: Int): PokemonEntity?

    /**
     * Obtiene todos los Pokémon de la base de datos.
     *
     * @return LiveData<List<PokemonEntity>>: Un objeto LiveData que emitirá automáticamente
     * nuevas listas de Pokémon cada vez que la tabla 'pokemons' cambie.
     */
    @Query("SELECT * FROM pokemons")
    fun getAllPokemons(): LiveData<List<PokemonEntity>>

    /**
     * Obtiene todos los Pokémon de la base de datos ordenados por nombre.
     *
     * @return LiveData con la lista ordenada de Pokémon.
     */
    @Query("SELECT * FROM pokemons ORDER BY name ASC")
    fun getAllPokemonsOrderedByName(): LiveData<List<PokemonEntity>>

    /**
     * Obtiene todos los Pokémon de la base de datos ordenados por ID.
     *
     * @return LiveData con la lista ordenada de Pokémon.
     */
    @Query("SELECT * FROM pokemons ORDER BY id ASC")
    fun getAllPokemonsOrderedById(): LiveData<List<PokemonEntity>>

    /**
     * Busca Pokémon por nombre (búsqueda parcial).
     *
     * @param searchQuery Término de búsqueda.
     * @return LiveData con la lista de Pokémon que coinciden con la búsqueda.
     */
    @Query("SELECT * FROM pokemons WHERE name LIKE '%' || :searchQuery || '%'")
    fun searchPokemons(searchQuery: String): LiveData<List<PokemonEntity>>

    /**
     * Elimina un Pokémon específico por su nombre.
     *
     * @param name Nombre del Pokémon a eliminar.
     */
    @Query("DELETE FROM pokemons WHERE name = :name")
    suspend fun deletePokemonByName(name: String)

    /**
     * Elimina un Pokémon específico por su ID.
     *
     * @param id ID del Pokémon a eliminar.
     */
    @Query("DELETE FROM pokemons WHERE id = :id")
    suspend fun deletePokemonById(id: Int)

    /**
     * Elimina todos los Pokémon de la base de datos.
     */
    @Query("DELETE FROM pokemons")
    suspend fun deleteAllPokemons()

    /**
     * Cuenta el total de Pokémon en la base de datos.
     *
     * @return Número total de Pokémon guardados.
     */
    @Query("SELECT COUNT(*) FROM pokemons")
    suspend fun getPokemonCount(): Int

    /**
     * Verifica si un Pokémon existe en la base de datos por nombre.
     *
     * @param name Nombre del Pokémon.
     * @return true si existe, false si no.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM pokemons WHERE name = :name)")
    suspend fun pokemonExists(name: String): Boolean

    /**
     * Obtiene los IDs de todos los Pokémon guardados.
     *
     * @return Lista de IDs de Pokémon.
     */
    @Query("SELECT id FROM pokemons")
    suspend fun getAllPokemonIds(): List<Int>
}