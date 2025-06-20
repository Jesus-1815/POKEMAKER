package com.jesus.pokemaker.ui.detail // Asegúrate de que el paquete sea correcto

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.jesus.pokemaker.Data.model.Chain
import com.jesus.pokemaker.Data.model.Evolution
import com.jesus.pokemaker.Data.model.Stat
import com.jesus.pokemaker.Data.model.Species // Asegúrate de importar Species si no está ya
import com.jesus.pokemaker.Data.model.Type
import com.jesus.pokemaker.R
import com.jesus.pokemaker.ui.viewmodel.PokemonViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import android.util.Log // Importar Log para depuración

class PokemonDetailActivity : AppCompatActivity() {

    private lateinit var pokemonViewModel: PokemonViewModel

    // Referencias a las vistas del layout
    private lateinit var ivDetailPokemon: ImageView
    private lateinit var tvDetailPokemonName: TextView
    private lateinit var tvDetailPokemonTypes: TextView
    private lateinit var tvDetailPokemonId: TextView
    private lateinit var llStatsContainer: LinearLayout
    private lateinit var tvDescription: TextView
    private lateinit var tvEvolutionChain: TextView
    private lateinit var cardHeader: androidx.cardview.widget.CardView

    // Instancia de Json para deserializar los Strings JSON de los extras.
    private val json = Json { ignoreUnknownKeys = true }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pokemon_detail)

        // Inicializar ViewModel
        pokemonViewModel = ViewModelProvider(this).get(PokemonViewModel::class.java)

        // Obtener referencias a las vistas por ID
        ivDetailPokemon = findViewById(R.id.ivDetailPokemon)
        tvDetailPokemonName = findViewById(R.id.tvDetailPokemonName)
        tvDetailPokemonTypes = findViewById(R.id.tvDetailPokemonTypes)
        tvDetailPokemonId = findViewById(R.id.tvDetailPokemonId)
        llStatsContainer = findViewById(R.id.llStatsContainer)
        tvDescription = findViewById(R.id.tvDescription)
        tvEvolutionChain = findViewById(R.id.tvEvolutionChain)
        cardHeader = findViewById(R.id.cardHeader)

        // Obtener datos pasados desde MainActivity a través del Intent
        val pokemonId = intent.getIntExtra("pokemonId", -1)
        val pokemonName = intent.getStringExtra("pokemonName") ?: "Unknown"
        val pokemonImageUrl = intent.getStringExtra("pokemonImageUrl") ?: ""
        val pokemonTypesJson = intent.getStringExtra("pokemonTypes") ?: "[]"
        val pokemonStatsJson = intent.getStringExtra("pokemonStats") ?: "[]"

        // --- Mostrar datos básicos del Pokémon ---
        tvDetailPokemonName.text = pokemonName.replaceFirstChar { it.uppercase() }
        tvDetailPokemonId.text = "#%03d".format(pokemonId) // Formato como #025

        // Cargar imagen con Coil
        ivDetailPokemon.load(pokemonImageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.sym_def_app_icon)
            error(android.R.drawable.ic_menu_close_clear_cancel)
        }

        // Deserializar y mostrar tipos. También cambia el color del encabezado.
        try {
            val types: List<Type> = json.decodeFromString(pokemonTypesJson)
            val typeNames = types.joinToString(separator = ", ") { it.type.name.replaceFirstChar { char -> char.uppercase() } }
            tvDetailPokemonTypes.text = "Tipos: $typeNames"

            // Cambiar color del CardView del encabezado basado en el primer tipo
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
                    else -> Color.parseColor("#FFFFFF") // Blanco por defecto
                }
                cardHeader.setCardBackgroundColor(color)
            }
        } catch (e: Exception) {
            tvDetailPokemonTypes.text = "Tipos: Error al cargar"
            Log.e("PokemonDetail", "Error deserializando tipos: ${e.message}", e)
        }

        // Deserializar y mostrar estadísticas
        try {
            val stats: List<Stat> = json.decodeFromString(pokemonStatsJson)
            llStatsContainer.removeAllViews() // Limpia cualquier vista de ejemplo del layout
            for (stat in stats) {
                addStatView(stat) // Añade una vista por cada estadística
            }
        } catch (e: Exception) {
            llStatsContainer.removeAllViews()
            val errorTextView = TextView(this).apply { text = "Error al cargar estadísticas." ; setTextColor(Color.RED) }
            llStatsContainer.addView(errorTextView)
            Log.e("PokemonDetail", "Error deserializando stats: ${e.message}", e)
        }

        // --- Cargar datos adicionales (Descripción, Evolución) desde la API usando Coroutines ---
        // Se ejecuta en CoroutineScope(Dispatchers.Main) para no bloquear el hilo principal y para tareas UI
        CoroutineScope(Dispatchers.Main).launch {
            // Obtener descripción de la especie
            try {
                // Obtiene la especie del Pokémon por su nombre.
                // Asegúrate que tu modelo PokemonSpecies tenga el campo 'flavorTextEntries'.
                val species = pokemonViewModel.getSpeciesDetails(pokemonName.lowercase())
                val description = species?.flavorTextEntries?.firstOrNull { it.language.name == "en" }?.flavorText?.replace("\n", " ") ?: "Descripción no disponible."
                tvDescription.text = description
            } catch (e: Exception) {
                tvDescription.text = "Descripción no disponible."
                Log.e("PokemonDetail", "Error obteniendo descripción de especie: ${e.message}", e)
            }

            // Obtener cadena de evolución
            try {
                val species = pokemonViewModel.getSpeciesDetails(pokemonName.lowercase())
                val evolutionChainUrl = species?.evolutionChain?.url // URL de la cadena de evolución

                if (evolutionChainUrl != null) {
                    // Extraer el ID de la URL de la cadena de evolución (ej. de ".../evolution-chain/1/" obtener "1")
                    val chainId = evolutionChainUrl.split("/").dropLast(1).last().toIntOrNull()

                    if (chainId != null) {
                        val evolutionChain = pokemonViewModel.getEvolutionChainDetails(chainId)
                        displayEvolutionChain(evolutionChain) // Función para mostrar la cadena
                    } else {
                        tvEvolutionChain.text = "Cadena de evolución no disponible (ID no válido)."
                    }
                } else {
                    tvEvolutionChain.text = "Cadena de evolución no disponible (URL no encontrada)."
                }
            } catch (e: Exception) {
                tvEvolutionChain.text = "Error al cargar cadena de evolución: ${e.message}"
                Log.e("PokemonDetail", "Error obteniendo cadena de evolución: ${e.message}", e)
            }
        }
    }

    /**
     * Función auxiliar para añadir una vista de estadística al LinearLayout de estadísticas.
     */
    private fun addStatView(stat: Stat) {
        val statLayout = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 4, 0, 4)
        }

        val statNameTextView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Peso para distribuir el espacio
            )
            text = "${stat.stat.name.replaceFirstChar { it.uppercase() }}:"
            textSize = 16sp
                    setTextColor(Color.BLACK)
        }

        val statValueTextView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f // Peso para distribuir el espacio
            )
            text = stat.baseStat.toString()
            textSize = 16sp
                    textStyle = android.graphics.Typeface.BOLD
            setTextColor(Color.BLACK)
        }

        statLayout.addView(statNameTextView)
        statLayout.addView(statValueTextView)
        llStatsContainer.addView(statLayout)
    }

    /**
     * Función auxiliar para mostrar la cadena de evolución de forma simplificada y recursiva.
     */
    private fun displayEvolutionChain(evolution: Evolution?) {
        if (evolution == null) {
            tvEvolutionChain.text = "Cadena de evolución no disponible."
            return
        }

        val chainText = StringBuilder()

        // Función recursiva para construir la cadena
        fun appendEvolution(chainNode: Chain, level: Int) {
            val prefix = "  ".repeat(level) // Indentación para las fases
            chainText.append("${prefix}Fase ${level + 1}: ${chainNode.species.name.replaceFirstChar { it.uppercase() }}\n")

            // Si hay evoluciones a esta fase, recorrerlas
            chainNode.evolvesTo.forEach { nextEvolution ->
                appendEvolution(nextEvolution, level + 1)
            }
        }

        // Inicia la construcción desde el nodo raíz de la cadena
        appendEvolution(evolution.chain, 0)

        tvEvolutionChain.text = chainText.toString()
    }
}