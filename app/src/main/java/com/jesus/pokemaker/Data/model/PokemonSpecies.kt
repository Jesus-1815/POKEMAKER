package com.jesus.pokemaker.Data.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class PokemonSpecies(
    @SerialName("base_happiness")
    val baseHappiness: Long,

    @SerialName("capture_rate")
    val captureRate: Long,

    val color: Color,

    @SerialName("egg_groups")
    val eggGroups: List<Color>,

    @SerialName("evolution_chain")
    val evolutionChain: EvolutionChain,

    @SerialName("evolves_from_species")
    val evolvesFromSpecies: Color? = null,

    @SerialName("flavor_text_entries")
    val flavorTextEntries: List<FlavorTextEntry>,

    @SerialName("form_descriptions")
    val formDescriptions: JsonArray,

    @SerialName("forms_switchable")
    val formsSwitchable: Boolean,

    @SerialName("gender_rate")
    val genderRate: Long,

    val genera: List<Genus>,
    val generation: Color,

    @SerialName("growth_rate")
    val growthRate: Color,

    val habitat: Color? = null,

    @SerialName("has_gender_differences")
    val hasGenderDifferences: Boolean,

    @SerialName("hatch_counter")
    val hatchCounter: Long,

    val id: Long,

    @SerialName("is_baby")
    val isBaby: Boolean,

    @SerialName("is_legendary")
    val isLegendary: Boolean,

    @SerialName("is_mythical")
    val isMythical: Boolean,

    val name: String,
    val names: List<SpeciesName>,
    val order: Long,

    @SerialName("pal_park_encounters")
    val palParkEncounters: List<PalParkEncounter>,

    @SerialName("pokedex_numbers")
    val pokedexNumbers: List<PokedexNumber>,

    val shape: Color? = null,
    val varieties: List<Variety>
)

@Serializable
data class Color(
    val name: String,
    val url: String
)

@Serializable
data class EvolutionChain(
    val url: String
)

@Serializable
data class FlavorTextEntry(
    @SerialName("flavor_text")
    val flavorText: String,

    val language: Color,
    val version: Color
)

@Serializable
data class Genus(
    val genus: String,
    val language: Color
)

@Serializable
data class SpeciesName(
    val language: Color,
    val name: String
)

@Serializable
data class PalParkEncounter(
    val area: Color,

    @SerialName("base_score")
    val baseScore: Long,

    val rate: Long
)

@Serializable
data class PokedexNumber(
    @SerialName("entry_number")
    val entryNumber: Long,

    val pokedex: Color
)

@Serializable
data class Variety(
    @SerialName("is_default")
    val isDefault: Boolean,

    val pokemon: Color
)