package com.jesus.pokemaker.Data.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Evolution(
    val chain: Chain,
    val id: Long
)

@Serializable
data class Chain(
    @SerialName("evolution_details")
    val evolutionDetails: List<EvolutionDetail>,

    @SerialName("evolves_to")
    val evolvesTo: List<Chain>,

    @SerialName("is_baby")
    val isBaby: Boolean,

    val species: Species
)

@Serializable
data class EvolutionDetail(
    val gender: Long? = null,

    @SerialName("held_item")
    val heldItem: Species? = null,

    val item: Species? = null,

    @SerialName("known_move")
    val knownMove: Species? = null,

    @SerialName("known_move_type")
    val knownMoveType: Species? = null,

    val location: Species? = null,

    @SerialName("min_affection")
    val minAffection: Long? = null,

    @SerialName("min_beauty")
    val minBeauty: Long? = null,

    @SerialName("min_happiness")
    val minHappiness: Long? = null,

    @SerialName("min_level")
    val minLevel: Long? = null,

    @SerialName("needs_overworld_rain")
    val needsOverworldRain: Boolean,

    @SerialName("party_species")
    val partySpecies: Species? = null,

    @SerialName("party_type")
    val partyType: Species? = null,

    @SerialName("relative_physical_stats")
    val relativePhysicalStats: Long? = null,

    @SerialName("time_of_day")
    val timeOfDay: String,

    @SerialName("trade_species")
    val tradeSpecies: Species? = null,

    val trigger: Species,

    @SerialName("turn_upside_down")
    val turnUpsideDown: Boolean
)