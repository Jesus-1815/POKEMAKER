package com.jesus.pokemaker.ui.detail

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.jesus.pokemaker.Data.model.Chain
import com.jesus.pokemaker.Data.model.Evolution
import com.jesus.pokemaker.Data.model.PokemonSpecies
import com.jesus.pokemaker.Data.model.Stat
import com.jesus.pokemaker.Data.model.Type
import com.jesus.pokemaker.R
import com.jesus.pokemaker.ui.viewmodel.PokemonViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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

        // Inicializar ViewModel - CORREGIDO: usando ViewModelProvider.AndroidViewModelFactory
        pokemonViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[PokemonViewModel::class.java]

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

        // Mostrar datos básicos del Pokémon
        tvDetailPokemonName.text = pokemonName.replaceFirstChar { it.uppercase() }
        tvDetailPokemonId.text = "#%03d".format(pokemonId)

        // Cargar imagen con Coil
        ivDetailPokemon.load(pokemonImageUrl) {
            crossfade(true)
            placeholder(android.R.drawable.sym_def_app_icon)
            error(android.R.drawable.ic_menu_close_clear_cancel)
        }

        // Deserializar y mostrar tipos. También cambia el color del encabezado.
        try {
            val types: List<Type> = json.decodeFromString(pokemonTypesJson)
            val typeNames = types.joinToString(separator = ", ") { type ->
                type.type.name.replaceFirstChar { char -> char.uppercase() }
            }
            tvDetailPokemonTypes.text = "Tipos: $typeNames"

            // Cambiar color del CardView del encabezado basado en el primer tipo
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
                    else -> Color.parseColor("#FFFFFF")
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
            llStatsContainer.removeAllViews()
            for (stat in stats) {
                addStatView(stat)
            }
        } catch (e: Exception) {
            llStatsContainer.removeAllViews()
            val errorTextView = TextView(this).apply {
                text = "Error al cargar estadísticas."
                setTextColor(Color.RED)
            }
            llStatsContainer.addView(errorTextView)
            Log.e("PokemonDetail", "Error deserializando stats: ${e.message}", e)
        }

        // Cargar datos adicionales (Descripción, Evolución) desde la API usando Coroutines
        CoroutineScope(Dispatchers.Main).launch {
            // Obtener descripción de la especie
            try {
                val species: PokemonSpecies? = pokemonViewModel.getSpeciesDetails(pokemonName.lowercase())
                val description = species?.flavorTextEntries?.firstOrNull { entry ->
                    entry.language.name == "en"
                }?.flavorText?.replace("\n", " ") ?: "Descripción no disponible."

                tvDescription.text = description
            } catch (e: Exception) {
                tvDescription.text = "Descripción no disponible."
                Log.e("PokemonDetail", "Error obteniendo descripción de especie: ${e.message}", e)
            }

            // Obtener cadena de evolución
            try {
                val species: PokemonSpecies? = pokemonViewModel.getSpeciesDetails(pokemonName.lowercase())
                val evolutionChainUrl = species?.evolutionChain?.url

                if (evolutionChainUrl != null) {
                    val chainId = evolutionChainUrl.split("/").dropLast(1).last().toIntOrNull()

                    if (chainId != null) {
                        val evolutionChain: Evolution? = pokemonViewModel.getEvolutionChainDetails(chainId)
                        displayEvolutionChain(evolutionChain)
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
        } // CORREGIDO: Agregada la llave de cierre faltante
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
                1f
            )
            text = "${stat.stat.name.replaceFirstChar { it.uppercase() }}:"
            textSize = 16f
            setTextColor(Color.BLACK)
        }

        val statValueTextView = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            text = stat.baseStat.toString()
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
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
            val prefix = "  ".repeat(level)
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