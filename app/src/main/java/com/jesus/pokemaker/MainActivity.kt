package com.jesus.pokemaker

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jesus.pokemaker.Data.api.RetrofitInstance
import com.jesus.pokemaker.Data.db.PokemonDatabase
import com.jesus.pokemaker.Data.db.PokemonEntity
import com.jesus.pokemaker.Data.repository.PokemonRepository
import com.jesus.pokemaker.ui.main.PokemonViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: PokemonViewModel
    private lateinit var repository: PokemonRepository
    private lateinit var database: PokemonDatabase

    // UI Components
    private lateinit var editTextPokemonName: EditText
    private lateinit var buttonSearch: Button
    private lateinit var buttonSearchMultiple: Button
    private lateinit var buttonShowAll: Button
    private lateinit var buttonClearAll: Button
    private lateinit var textViewResult: TextView
    private lateinit var textViewCount: TextView
    private lateinit var recyclerView: RecyclerView

    // Adapter para mostrar la lista de Pokémon
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar base de datos
        database = PokemonDatabase.getDatabase(this)

        // Inicializar repositorio
        repository = PokemonRepository(
            apiService = RetrofitInstance.apiService,
            pokemonDao = database.pokemonDao()
        )

        // Inicializar ViewModel
        val viewModelFactory = PokemonViewModel.PokemonViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[PokemonViewModel::class.java]

        // Inicializar UI
        initializeUI()
        setupObservers()
        setupClickListeners()

        Log.d("MainActivity", "Aplicación iniciada correctamente")
    }

    private fun initializeUI() {
        // Aquí debes reemplazar con los IDs reales de tu layout
        editTextPokemonName = findViewById(R.id.editTextPokemonName)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonSearchMultiple = findViewById(R.id.buttonSearchMultiple)
        buttonShowAll = findViewById(R.id.buttonShowAll)
        buttonClearAll = findViewById(R.id.buttonClearAll)
        textViewResult = findViewById(R.id.textViewResult)
        textViewCount = findViewById(R.id.textViewCount)
        recyclerView = findViewById(R.id.recyclerView)

        // Configurar RecyclerView
        pokemonAdapter = PokemonAdapter { pokemon ->
            // Callback cuando se hace clic en un Pokémon
            showPokemonDetails(pokemon)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pokemonAdapter
        }
    }

    private fun setupObservers() {
        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                textViewResult.text = "Cargando..."
                disableButtons()
            } else {
                enableButtons()
            }
        }

        // Observar mensajes de error
        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                textViewResult.text = "Error: $it"
                viewModel.clearErrorMessage()
            }
        }

        // Observar Pokémon buscado
        viewModel.searchedPokemon.observe(this) { pokemon ->
            pokemon?.let {
                showPokemonInfo(it)
            }
        }

        // Observar todos los Pokémon
        viewModel.allPokemons.observe(this) { pokemonList ->
            pokemonAdapter.updatePokemons(pokemonList)
            textViewCount.text = "Pokémon guardados: ${pokemonList.size}"
        }

        // Observar éxito de operaciones
        viewModel.operationSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Operación completada exitosamente", Toast.LENGTH_SHORT).show()
                viewModel.clearOperationSuccess()
            }
        }
    }

    private fun setupClickListeners() {
        buttonSearch.setOnClickListener {
            val pokemonName = editTextPokemonName.text.toString().trim()
            if (pokemonName.isNotEmpty()) {
                searchPokemon(pokemonName)
            } else {
                Toast.makeText(this, "Por favor ingresa un nombre de Pokémon", Toast.LENGTH_SHORT).show()
            }
        }

        buttonSearchMultiple.setOnClickListener {
            searchMultiplePokemons()
        }

        buttonShowAll.setOnClickListener {
            showAllPokemons()
        }

        buttonClearAll.setOnClickListener {
            clearAllPokemons()
        }
    }

    private fun searchPokemon(pokemonName: String) {
        Log.d("MainActivity", "Buscando Pokémon: $pokemonName")
        viewModel.searchAndSavePokemon(pokemonName)
    }

    private fun searchMultiplePokemons() {
        // Lista de ejemplo de Pokémon para probar
        val pokemonNames = listOf("pikachu", "charizard", "blastoise", "venusaur", "gengar")

        Log.d("MainActivity", "Buscando múltiples Pokémon: $pokemonNames")
        textViewResult.text = "Buscando múltiples Pokémon..."

        viewModel.searchAndSaveMultiplePokemons(pokemonNames)
    }

    private fun showAllPokemons() {
        textViewResult.text = "Mostrando todos los Pokémon guardados en la lista"
        // Los Pokémon se muestran automáticamente en el RecyclerView gracias al Observer
    }

    private fun clearAllPokemons() {
        lifecycleScope.launch {
            try {
                repository.deleteAllPokemons()
                textViewResult.text = "Todos los Pokémon han sido eliminados"
                Toast.makeText(this@MainActivity, "Base de datos limpiada", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                textViewResult.text = "Error al limpiar la base de datos: ${e.message}"
                Log.e("MainActivity", "Error al limpiar base de datos", e)
            }
        }
    }

    private fun showPokemonInfo(pokemon: PokemonEntity) {
        val info = """
            ID: ${pokemon.id}
            Nombre: ${pokemon.name.capitalize()}
            Imagen: ${pokemon.imageUrl}
            Tipos: ${pokemon.types}
            Stats: ${pokemon.stats}
        """.trimIndent()

        textViewResult.text = info
        Log.d("MainActivity", "Pokémon encontrado: ${pokemon.name}")
    }

    private fun showPokemonDetails(pokemon: PokemonEntity) {
        // Convertir a formato de API para mostrar detalles completos
        try {
            val apiPokemon = viewModel.convertToApiPokemon(pokemon)
            apiPokemon?.let {
                val details = """
                    ${pokemon.name.capitalize()}
                    ID: ${it.id}
                    Tipos: ${it.types.joinToString(", ") { type -> type.type.name }}
                    Stats: ${it.stats.joinToString(", ") { stat -> "${stat.stat.name}: ${stat.baseStat}" }}
                """.trimIndent()

                textViewResult.text = details
                Toast.makeText(this, "Detalles de ${pokemon.name}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al mostrar detalles: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("MainActivity", "Error al convertir Pokémon", e)
        }
    }

    private fun disableButtons() {
        buttonSearch.isEnabled = false
        buttonSearchMultiple.isEnabled = false
        buttonShowAll.isEnabled = false
        buttonClearAll.isEnabled = false
    }

    private fun enableButtons() {
        buttonSearch.isEnabled = true
        buttonSearchMultiple.isEnabled = true
        buttonShowAll.isEnabled = true
        buttonClearAll.isEnabled = true
    }

    // Funciones de prueba adicionales para verificar la funcionalidad
    private fun testDatabaseOperations() {
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Iniciando pruebas de base de datos...")

                // Probar si existe un Pokémon
                viewModel.checkIfPokemonExists("pikachu") { exists ->
                    Log.d("MainActivity", "¿Pikachu existe? $exists")
                }

                // Obtener conteo
                val count = repository.getPokemonCount()
                Log.d("MainActivity", "Conteo de Pokémon: $count")

                Log.d("MainActivity", "Pruebas de base de datos completadas")

            } catch (e: Exception) {
                Log.e("MainActivity", "Error en pruebas de base de datos", e)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Ejecutar pruebas cuando la actividad se reanuda
        testDatabaseOperations()
    }
}

// Adapter simple para el RecyclerView
class PokemonAdapter(
    private val onPokemonClick: (PokemonEntity) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    private var pokemons = emptyList<PokemonEntity>()

    class PokemonViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): PokemonViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val pokemon = pokemons[position]
        holder.textViewName.text = "${pokemon.name.capitalize()} (ID: ${pokemon.id})"
        holder.itemView.setOnClickListener {
            onPokemonClick(pokemon)
        }
    }

    override fun getItemCount() = pokemons.size

    fun updatePokemons(newPokemons: List<PokemonEntity>) {
        pokemons = newPokemons
        notifyDataSetChanged()
    }
}