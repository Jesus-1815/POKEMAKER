package com.jesus.pokemaker.Data.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Pokemon(
    val abilities: List<Ability>,

    @SerialName("base_experience")
    val baseExperience: Long,

    val cries: Cries,
    val forms: List<Species>,

    @SerialName("game_indices")
    val gameIndices: List<GameIndex>,

    val height: Long,

    @SerialName("held_items")
    val heldItems: List<HeldItem>,

    val id: Long,

    @SerialName("is_default")
    val isDefault: Boolean,

    @SerialName("location_area_encounters")
    val locationAreaEncounters: String,

    val moves: List<Move>,
    val name: String,
    val order: Long,

    @SerialName("past_abilities")
    val pastAbilities: List<PastAbility>,

    @SerialName("past_types")
    val pastTypes: JsonArray,

    val species: Species,
    val sprites: PokemonSprites,
    val stats: List<Stat>,
    val types: List<Type>,
    val weight: Long
)

@Serializable
data class Ability(
    val ability: Species? = null,

    @SerialName("is_hidden")
    val isHidden: Boolean,

    val slot: Long
)

@Serializable
data class Cries(
    val latest: String,
    val legacy: String
)

@Serializable
data class GameIndex(
    @SerialName("game_index")
    val gameIndex: Long,

    val version: Species
)

@Serializable
data class HeldItem(
    val item: Species,

    @SerialName("version_details")
    val versionDetails: List<VersionDetail>
)

@Serializable
data class VersionDetail(
    val rarity: Long,
    val version: Species
)

@Serializable
data class Move(
    val move: Species,

    @SerialName("version_group_details")
    val versionGroupDetails: List<VersionGroupDetail>
)

@Serializable
data class VersionGroupDetail(
    @SerialName("level_learned_at")
    val levelLearnedAt: Long,

    @SerialName("move_learn_method")
    val moveLearnMethod: Species,

    val order: Long? = null,

    @SerialName("version_group")
    val versionGroup: Species
)

@Serializable
data class PastAbility(
    val abilities: List<Ability>,
    val generation: Species
)

@Serializable
data class Stat(
    @SerialName("base_stat")
    val baseStat: Long,

    val effort: Long,
    val stat: Species
)

@Serializable
data class Type(
    val slot: Long,
    val type: Species
)