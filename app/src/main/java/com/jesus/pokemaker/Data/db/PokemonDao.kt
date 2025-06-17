package com.jesus.pokemaker.Data.db // Define el paquete donde se encuentra este archivo.

import androidx.lifecycle.LiveData // Importa LiveData para observar cambios en los datos.
import androidx.room.Dao // Importa la anotación @Dao.
import androidx.room.Insert // Importa la anotación @Insert para insertar datos.
import androidx.room.OnConflictStrategy // Importa OnConflictStrategy para definir cómo manejar conflictos.
import androidx.room.Query // Importa la anotación @Query para definir consultas SQL personalizadas.

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
     * @Insert(onConflict = OnConflictStrategy.REPLACE): Indica a Room que inserte el objeto.
     * 'OnConflictStrategy.REPLACE' significa que si el ID ya existe, la fila antigua se borrará
     * y la nueva se insertará.
     * suspend fun: Indica que esta es una función suspendida de Kotlin Coroutines,
     * lo que significa que puede ejecutarse de forma asíncrona y no bloqueará el hilo principal.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPokemon(pokemon: PokemonEntity)

    /**
     * Obtiene un Pokémon específico de la base de datos por su nombre.
     *
     * @param name El nombre del Pokémon a buscar.
     * @return La instancia de PokemonEntity si se encuentra, o null si no existe.
     * @Query("SELECT * FROM pokemons WHERE name = :name"): Define una consulta SQL personalizada.
     * Room validará esta consulta en tiempo de compilación. ':name' es un marcador de posición
     * para el parámetro 'name' de la función.
     * suspend fun: También es una función suspendida para ejecución asíncrona.
     */
    @Query("SELECT * FROM pokemons WHERE name = :name")
    suspend fun getPokemon(name: String): PokemonEntity?

    /**
     * Obtiene todos los Pokémon de la base de datos.
     *
     * @return LiveData<List<PokemonEntity>>: Un objeto LiveData que emitirá automáticamente
     * nuevas listas de Pokémon cada vez que la tabla 'pokemons' cambie.
     * Esto es ideal para mantener la UI actualizada en tiempo real sin recargar manualmente.
     * A diferencia de las anteriores, esta NO es una función 'suspend' porque LiveData ya maneja
     * la ejecución en un hilo de fondo.
     */
    @Query("SELECT * FROM pokemons")
    fun getAllPokemons(): LiveData<List<PokemonEntity>>
}