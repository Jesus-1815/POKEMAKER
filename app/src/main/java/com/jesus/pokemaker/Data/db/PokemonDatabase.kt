package com.jesus.pokemaker.Data.db // Define el paquete donde se encuentra este archivo.

import android.content.Context // Necesario para obtener el contexto de la aplicación.
import androidx.room.Database // Importa la anotación @Database.
import androidx.room.Room // Importa la clase Room para construir la base de datos.
import androidx.room.RoomDatabase // Importa la clase base RoomDatabase.

/**
 * Clase abstracta que representa la base de datos de Room para la aplicación.
 *
 * @Database(entities = [PokemonEntity::class], version = 1, exportSchema = false):
 * - entities: Un array de las clases de entidad que pertenecen a esta base de datos.
 * Aquí solo tenemos PokemonEntity.
 * - version: La versión de la base de datos. Se incrementa cuando cambias el esquema
 * (añadir/quitar columnas, tablas, etc.).
 * - exportSchema: Si es 'true', exporta el esquema de la base de datos a un archivo
 * JSON para control de versiones. Se suele establecer en 'false' en desarrollo.
 */
@Database(entities = [PokemonEntity::class], version = 1, exportSchema = false)
abstract class PokemonDatabase : RoomDatabase() {

    /**
     * Método abstracto que Room implementará para proporcionar una instancia del DAO.
     * @return Una instancia de PokemonDao.
     */
    abstract fun pokemonDao(): PokemonDao

    /**
     * Objeto complementario (Companion object) que contiene la lógica para obtener
     * una instancia única (Singleton) de la base de datos.
     * Esto asegura que solo haya un objeto PokemonDatabase en toda la aplicación,
     * lo cual es eficiente y evita problemas de concurrencia.
     */
    companion object {
        // @Volatile: Garantiza que el valor de INSTANCE sea siempre el más actualizado
        // y que todos los hilos vean la misma instancia.
        @Volatile
        private var INSTANCE: PokemonDatabase? = null // La única instancia de la base de datos.

        /**
         * Obtiene la instancia de la base de datos. Si no existe, la crea de forma segura.
         *
         * @param context El contexto de la aplicación. Se usa applicationContext para
         * evitar fugas de memoria, ya que la base de datos vive tanto como la app.
         * @return La instancia única de PokemonDatabase.
         */
        fun getDatabase(context: Context): PokemonDatabase {
            // ?: significa "Elvis operator". Si INSTANCE no es null, lo devuelve.
            // Si es null, ejecuta el bloque 'synchronized'.
            return INSTANCE ?: synchronized(this) {
                // Bloque 'synchronized(this)' asegura que solo un hilo pueda ejecutar
                // este código a la vez, previniendo que múltiples hilos creen múltiples
                // instancias de la base de datos.
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Contexto de la aplicación
                    PokemonDatabase::class.java, // La clase de la base de datos
                    "pokemon_database" // El nombre del archivo de la base de datos en el dispositivo
                )
                    // .fallbackToDestructiveMigration(): Una opción (comentada aquí) que puedes añadir
                    //   si cambias la versión de la base de datos y no quieres escribir un
                    //   código de migración complejo. Borrará todos los datos al actualizar el esquema.
                    .build() // Construye la instancia de la base de datos.
                INSTANCE = instance // Asigna la instancia creada a la variable INSTANCE.
                instance // Devuelve la instancia.
            }
        }
    }
}