package com.jesus.pokemaker.Data.db // Define el paquete donde se encuentra este archivo. Asegúrate que coincida con la ruta de tu proyecto.

import androidx.room.Entity // Importa la anotación @Entity de Room.
import androidx.room.PrimaryKey // Importa la anotación @PrimaryKey de Room.

/**
 * Clase de entidad que representa una fila en la tabla 'pokemons' de la base de datos SQLite.
 *
 * @Entity(tableName = "pokemons"): Anotación que indica que esta clase es una entidad de Room
 * y que se mapeará a una tabla de la base de datos con el nombre "pokemons".
 */
@Entity(tableName = "pokemons")
data class PokemonEntity(
    // @PrimaryKey: Anotación que designa 'id' como la clave primaria de la tabla.
    // Cada Pokémon en la tabla debe tener un 'id' único.
    @PrimaryKey
    val id: Int,

    // 'name' será una columna de texto en la tabla.
    val name: String,

    // 'imageUrl' será una columna de texto en la tabla, guardando la URL de la imagen.
    val imageUrl: String,

    // 'types' se guarda como una cadena de texto (String).
    // Esto se debe a que Room no puede almacenar directamente una 'List<Type>' (objeto complejo)
    // en una sola columna. Por lo tanto, convertiremos la lista de tipos a una cadena JSON
    // (usando kotlinx.serialization) antes de guardarla y la deserializaremos al leer.
    val types: String,

    // 'stats' también se guarda como una cadena de texto (String) por la misma razón que 'types'.
    // Convertiremos la lista de estadísticas a una cadena JSON.
    val stats: String
)
// En resumen, PokemonEntity es el esquema de tu tabla 'pokemons'.