package com.jesus.pokemaker.ui.adapter // Asegúrate de que este paquete sea correcto

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
import com.jesus.pokemaker.R // Importa R para acceder a recursos como IDs de vistas. ¡CRUCIAL!
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
    private val json = Json { ignoreUnknownKeys = true }

    /**
     * ViewHolder para cada elemento de la lista.
     * Contiene las vistas del layout 'item_pokemon_card.xml'.
     */
    inner class PokemonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Asegúrate de que estos IDs existan y coincidan en item_pokemon_card.xml
        val cardPokemon: CardView = itemView.findViewById(R.id.cardPokemon)
        val ivPokemon: ImageView = itemView.findViewById(R.id.ivPokemon)
        val tvPokemonName: TextView = itemView.findViewById(R.id.tvPokemonName)
        val tvPokemonTypes: TextView = itemView.findViewById(R.id.tvPokemonTypes)
    }

    /**
     * Crea nuevas vistas de ViewHolder (infla el layout del item).
     * Se llama cuando el RecyclerView necesita un nuevo ViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon_card, parent, false) // Asegúrate que item_pokemon_card.xml exista
        return PokemonViewHolder(view)
    }

    /**
     * Vincula los datos de un Pokémon a las vistas de un ViewHolder.
     * Se llama para mostrar los datos en una posición específica.
     */
    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemonList[position] // Obtiene el Pokémon actual de la lista

        // Cargar la imagen del Pokémon usando Coil.
        holder.ivPokemon.load(pokemon.imageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.sym_def_app_icon)
            error(android.R.drawable.ic_menu_close_clear_cancel)
        }

        // Mostrar el nombre del Pokémon (primera letra en mayúscula).
        holder.tvPokemonName.text = pokemon.name.replaceFirstChar { it.uppercase() }

        // Deserializar y mostrar los tipos del Pokémon.
        try {
            val types: List<Type> = json.decodeFromString(pokemon.types)
            val typeNames = types.joinToString(separator = ", ") { it.type.name.replaceFirstChar { char -> char.uppercase() } }
            holder.tvPokemonTypes.text = "Tipos: $typeNames"
        } catch (e: Exception) {
            holder.tvPokemonTypes.text = "Tipos: Error al cargar"
            e.printStackTrace()
        }

        // Establecer un listener de clic en la CardView.
        holder.cardPokemon.setOnClickListener {
            onItemClick(pokemon)
        }

        // Opcional: Cambiar el color de fondo de la tarjeta basado en el primer tipo
        try {
            val types: List<Type> = json.decodeFromString(pokemon.types)
            if (types.isNotEmpty()) {
                val firstType = types.first().type.name
                val color = when (firstType) {
                    "grass" -> Color.parseColor("#7AC74C")
                    "fire" -> Color.parseColor("#EE8130")
                    "water" -> Color.parseColor("#6390F0")
                    "electric" -> Color.parseColor("#F7D02C")
                    "psychic" -> Color.parseColor("#F95587")
                    "normal" -> Color.parseColor("#A8A77A")
                    "fighting" -> Color.parseColor("#C22E28")
                    "flying" -> Color.parseColor("#A98FF3")
                    "poison" -> Color.parseColor("#A33EA1")
                    "ground" -> Color.parseColor("#E2BF65")
                    "rock" -> Color.parseColor("#B6A136")
                    "bug" -> Color.parseColor("#A6B91A")
                    "ghost" -> Color.parseColor("#735797")
                    "steel" -> Color.parseColor("#B7B7CE")
                    "dragon" -> Color.parseColor("#6F35FC")
                    "dark" -> Color.parseColor("#705746")
                    "fairy" -> Color.parseColor("#D685AD")
                    "ice" -> Color.parseColor("#96D9D6")
                    else -> Color.WHITE
                }
                holder.cardPokemon.setCardBackgroundColor(color)
            }
        } catch (e: Exception) {
            // Ignorar errores de color
        }
    }

    /**
     * Devuelve el número total de elementos en la lista.
     */
    override fun getItemCount(): Int = pokemonList.size

    /**
     * Actualiza la lista de Pokémon en el adaptador y notifica al RecyclerView.
     */
    fun submitList(newList: List<PokemonEntity>) {
        pokemonList = newList
        notifyDataSetChanged()
    }
}