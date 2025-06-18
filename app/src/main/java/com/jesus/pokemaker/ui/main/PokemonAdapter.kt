package com.jesus.pokemaker.ui.adapter // Asegúrate de que el paquete sea correcto

import android.graphics.Color // Para manejar colores, específicamente para los tipos de Pokémon
import android.view.LayoutInflater // Para inflar los layouts XML
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView // Para mostrar imágenes
import android.widget.TextView // Para mostrar texto
import androidx.cardview.widget.CardView // Para la vista de tarjeta de cada Pokémon
import androidx.recyclerview.widget.RecyclerView // Clase base para el adaptador
import coil.load // Extensión de Coil para cargar imágenes desde URL
import com.jesus.pokemaker.Data.db.PokemonEntity // Importa tu entidad de Pokémon de la base de datos
import com.jesus.pokemaker.Data.model.Type // Importa el modelo Type de tu API para deserializar
import com.jesus.pokemaker.R // Importa R para acceder a recursos como IDs de vistas
import kotlinx.serialization.decodeFromString // Para deserializar JSON a objetos Kotlin
import kotlinx.serialization.json.Json // Para la instancia de Json para deserializar

/**
 * Adaptador para RecyclerView que muestra una lista de Pokémon.
 *
 * @param onItemClick Una función lambda que se ejecutará cuando se haga clic en un Pokémon.
 * Recibe el PokemonEntity del Pokémon en el que se hizo clic.
 */
class PokemonAdapter(private val onItemClick: (PokemonEntity) -> Unit) :
    RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    // Lista mutable de Pokémon que el adaptador mostrará.
    private var pokemonList: List<PokemonEntity> = emptyList()

    // Instancia de Json para deserializar la cadena JSON de tipos.
    // Misma configuración que en Repositorio y RetrofitInstance.
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * ViewHolder para cada elemento de la lista.
     * Contiene las vistas del layout 'item_pokemon_card.xml'.
     */
    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardPokemon: CardView = itemView.findViewById(R.id.cardPokemon) // CardView principal
        val ivPokemon: ImageView = itemView.findViewById(R.id.ivPokemon) // ImageView para la imagen del Pokémon
        val tvPokemonName: TextView = itemView.findViewById(R.id.tvPokemonName) // TextView para el nombre
        val tvPokemonTypes: TextView = itemView.findViewById(R.id.tvPokemonTypes) // TextView para los tipos
    }

    /**
     * Crea nuevas vistas de ViewHolder (infla el layout del item).
     * Se llama cuando el RecyclerView necesita un nuevo ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon_card, parent, false) // Infla el layout del item
        return PokemonViewHolder(view)
    }

    /**
     * Vincula los datos de un Pokémon a las vistas de un ViewHolder.
     * Se llama para mostrar los datos en una posición específica.
     */
    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position] // Obtiene el Pokémon actual de la lista

        // Cargar la imagen del Pokémon usando Coil.
        // Maneja automáticamente la descarga, caché y visualización.
        holder.ivPokemon.load(pokemon.imageUrl) {
            crossfade(true) // Efecto de fundido al cargar la imagen
            placeholder(android.R.drawable.sym_def_app_icon) // Imagen de marcador de posición mientras carga
            error(android.R.drawable.ic_menu_close_clear_cancel) // Imagen si hay un error al cargar
        }

        // Mostrar el nombre del Pokémon (primera letra en mayúscula).
        holder.tvPokemonName.text = pokemon.name.replaceFirstChar { it.uppercase() }

        // Deserializar y mostrar los tipos del Pokémon.
        try {
            val types: List<Type> = json.decodeFromString(pokemon.types) // Convierte JSON String a List<Type>
            val typeNames = types.joinToString(separator = ", ") { it.type.name.replaceFirstChar { char -> char.uppercase() } } // Une los nombres de tipo con comas
            holder.tvPokemonTypes.text = "Tipos: $typeNames"
        } catch (e: Exception) {
            // Manejo de error si la deserialización falla (ej. JSON mal formado).
            holder.tvPokemonTypes.text = "Tipos: Error al cargar"
            e.printStackTrace() // Imprime el stack trace para depuración
        }

        // Establecer un listener de clic en la CardView.
        holder.cardPokemon.setOnClickListener {
            onItemClick(pokemon) // Llama al callback cuando se hace clic en el Pokémon
        }

        // Opcional: Cambiar el color de fondo de la tarjeta basado en el primer tipo (ejemplo básico)
        try {
            val types: List<Type> = json.decodeFromString(pokemon.types)
            if (types.isNotEmpty()) {
                val firstType = types.first().type.name
                val color = when (firstType) {
                    "grass" -> Color.parseColor("#7AC74C") // Verde
                    "fire" -> Color.parseColor("#EE8130") // Naranja
                    "water" -> Color.parseColor("#6390F0") // Azul
                    "electric" -> Color.parseColor("#F7D02C") // Amarillo
                    "psychic" -> Color.parseColor("#F95587") // Rosa
                    "normal" -> Color.parseColor("#A8A77A") // Beige
                    "fighting" -> Color.parseColor("#C22E28") // Rojo oscuro
                    "flying" -> Color.parseColor("#A98FF3") // Morado claro
                    "poison" -> Color.parseColor("#A33EA1") // Morado
                    "ground" -> Color.parseColor("#E2BF65") // Marrón claro
                    "rock" -> Color.parseColor("#B6A136") // Marrón
                    "bug" -> Color.parseColor("#A6B91A") // Verde claro
                    "ghost" -> Color.parseColor("#735797") // Morado grisáceo
                    "steel" -> Color.parseColor("#B7B7CE") // Gris
                    "dragon" -> Color.parseColor("#6F35FC") // Morado fuerte
                    "dark" -> Color.parseColor("#705746") // Gris oscuro
                    "fairy" -> Color.parseColor("#D685AD") // Rosa claro
                    "ice" -> Color.parseColor("#96D9D6") // Celeste
                    else -> Color.WHITE // Por defecto
                }
                holder.cardPokemon.setCardBackgroundColor(color)
            }
        } catch (e: Exception) {
            // Ignorar errores de color, usar el color por defecto del CardView
        }
    }

    /**
     * Devuelve el número total de elementos en la lista.
     */
    override fun getItemCount(): Int = pokemonList.size

    /**
     * Actualiza la lista de Pokémon en el adaptador y notifica al RecyclerView.
     *
     * @param newList La nueva lista de PokemonEntity a mostrar.
     */
    fun submitList(newList: List<PokemonEntity>) {
        pokemonList = newList
        notifyDataSetChanged() // Notifica al RecyclerView que los datos han cambiado
    }
}