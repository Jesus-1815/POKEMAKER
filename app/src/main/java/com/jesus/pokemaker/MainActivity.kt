package com.jesus.pokemaker // Asegúrate de que el paquete sea correcto

import android.content.Intent // Para iniciar nuevas actividades
import android.os.Bundle // Para guardar y restaurar el estado de la actividad
import android.view.View // Para manipular la visibilidad de las vistas
import android.widget.Button // Importa Button
import android.widget.EditText // Importa EditText
import android.widget.ProgressBar // Importa ProgressBar
import android.widget.Toast // Para mostrar mensajes cortos en pantalla
import androidx.activity.viewModels // Para obtener una instancia del ViewModel de forma declarativa
import androidx.appcompat.app.AppCompatActivity // Clase base para actividades de soporte
import androidx.lifecycle.Observer // Para observar cambios en LiveData
import androidx.recyclerview.widget.GridLayoutManager // Usaremos GridLayoutManager para la vista de cuadrícula
import androidx.recyclerview.widget.RecyclerView // Importa RecyclerView
import com.jesus.pokemaker.Data.db.PokemonEntity // Importa la entidad de Pokémon de la base de datos
import com.jesus.pokemaker.ui.adapter.PokemonAdapter // Importa tu adaptador de Pokémon
import com.jesus.pokemaker.ui.detail.PokemonDetailActivity // Importa la actividad de detalle, la crearemos en el siguiente paso
import com.jesus.pokemaker.ui.viewmodel.PokemonViewModel // Importa tu ViewModel

class MainActivity : AppCompatActivity() {

    // Instancia del ViewModel, obtenida mediante 'viewModels()' para inyección automática.
    private val pokemonViewModel: PokemonViewModel by viewModels()

    // Referencias a las vistas del layout activity_main.xml
    private lateinit var etPokemonName: EditText
    private lateinit var btnSearch: Button
    private lateinit var rvPokemons: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnLoadInitial: Button

    // Adaptador para el RecyclerView. Le pasamos una lambda para manejar clics en los ítems.
    private lateinit var pokemonAdapter: PokemonAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Establece el layout de la actividad

        // Inicializar las vistas
        etPokemonName = findViewById(R.id.etPokemonName)
        btnSearch = findViewById(R.id.btnSearch)
        rvPokemons = findViewById(R.id.rvPokemons)
        progressBar = findViewById(R.id.progressBar)
        btnLoadInitial = findViewById(R.id.btnLoadInitial)

        // Configurar el RecyclerView
        pokemonAdapter = PokemonAdapter { pokemon -> // Define el comportamiento al hacer clic en un Pokémon
            // Cuando se hace clic en un Pokémon, inicia la actividad de detalle
            val intent = Intent(this, PokemonDetailActivity::class.java).apply {
                // Pasa todos los datos necesarios para la pantalla de detalle
                putExtra("pokemonId", pokemon.id)
                putExtra("pokemonName", pokemon.name)
                putExtra("pokemonImageUrl", pokemon.imageUrl)
                putExtra("pokemonTypes", pokemon.types) // Pasa los tipos serializados
                putExtra("pokemonStats", pokemon.stats) // Pasa los stats serializados
            }
            startActivity(intent)
        }
        // Usamos GridLayoutManager para que se vea como en la imagen (cuadrícula de 2 columnas)
        rvPokemons.layoutManager = GridLayoutManager(this, 2)
        rvPokemons.adapter = pokemonAdapter

        // Configurar listeners de los botones
        btnSearch.setOnClickListener {
            val name = etPokemonName.text.toString()
            pokemonViewModel.searchAndSavePokemon(name) // Llama a la función de búsqueda en el ViewModel
        }

        btnLoadInitial.setOnClickListener {
            pokemonViewModel.loadInitialPokemons() // Llama a la función de precarga de Pokémon
        }

        // --- Observar LiveData del ViewModel ---

        // Observa la lista de Pokémon locales. Cada vez que cambie, se actualizará el adaptador.
        pokemonViewModel.pokemonList.observe(this, Observer { pokemons ->
            pokemonAdapter.submitList(pokemons) // Actualiza los datos en el adaptador

            // Muestra el botón de cargar iniciales solo si no hay Pokémon y la carga no está activa
            if (pokemons.isEmpty() && pokemonViewModel.isLoading.value == false) {
                btnLoadInitial.visibility = View.VISIBLE
            } else {
                btnLoadInitial.visibility = View.GONE
            }
        })

        // Observa el estado de carga (para mostrar/ocultar el ProgressBar).
        pokemonViewModel.isLoading.observe(this, Observer { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Si está cargando y no hay pokémones, oculta el botón de cargar iniciales
            if (isLoading && pokemonViewModel.pokemonList.value.isNullOrEmpty()) {
                btnLoadInitial.visibility = View.GONE
            }
        })

        // Observa los mensajes de error/éxito del ViewModel y los muestra en un Toast.
        pokemonViewModel.error.observe(this, Observer { message ->
            if (message.isNotBlank()) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                pokemonViewModel.clearError() // Limpia el mensaje de error después de mostrarlo
            }
        })

        // Carga inicial al empezar: Si la lista está vacía, intenta precargar
        pokemonViewModel.pokemonList.observe(this, Observer { pokemons ->
            if (pokemons.isEmpty() && pokemonViewModel.isLoading.value == false) {
                // Descomenta la siguiente línea si quieres que cargue automáticamente al iniciar la app
                // pokemonViewModel.loadInitialPokemons()
            }
        })
    }
}