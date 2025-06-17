package com.jesus.pokemaker.Data.model

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class PokemonType(
    @SerialName("damage_relations")
    val damageRelations: DamageRelations,

    @SerialName("game_indices")
    val gameIndices: List<TypeGameIndex>,

    val generation: Generation,
    val id: Long,

    @SerialName("move_damage_class")
    val moveDamageClass: Generation,
    val moves: List<Generation>,
    val name: String,
    val names: List<TypeName>,

    @SerialName("past_damage_relations")
    val pastDamageRelations: JsonArray,

    val pokemon: List<TypePokemon>,
    val sprites: TypeSprites
)

@Serializable
data class DamageRelations(
    @SerialName("double_damage_from")
    val doubleDamageFrom: List<Generation>,

    @SerialName("double_damage_to")
    val doubleDamageTo: JsonArray,

    @SerialName("half_damage_from")
    val halfDamageFrom: JsonArray,

    @SerialName("half_damage_to")
    val halfDamageTo: List<Generation>,

    @SerialName("no_damage_from")
    val noDamageFrom: List<Generation>,

    @SerialName("no_damage_to")
    val noDamageTo: List<Generation>
)

@Serializable
data class Generation(
    val name: String,
    val url: String
)

@Serializable
data class TypeGameIndex(
    @SerialName("game_index")
    val gameIndex: Long,

    val generation: Generation
)

@Serializable
data class TypeName(
    val language: Generation,
    val name: String
)

@Serializable
data class TypePokemon(
    val pokemon: Generation,
    val slot: Long
)

@Serializable
data class TypeSprites(
    @SerialName("generation-iii")
    val generationIii: TypeGenerationIii,

    @SerialName("generation-iv")
    val generationIv: TypeGenerationIv,

    @SerialName("generation-ix")
    val generationIx: TypeGenerationIx,

    @SerialName("generation-v")
    val generationV: TypeGenerationV,

    @SerialName("generation-vi")
    val generationVi: Map<String, TypeColosseum>,

    @SerialName("generation-vii")
    val generationVii: TypeGenerationVii,

    @SerialName("generation-viii")
    val generationViii: TypeGenerationViii
)

@Serializable
data class TypeGenerationIii(
    val colosseum: TypeColosseum,
    val emerald: TypeColosseum,

    @SerialName("firered-leafgreen")
    val fireredLeafgreen: TypeColosseum,

    @SerialName("ruby-saphire")
    val rubySaphire: TypeColosseum,

    val xd: TypeColosseum
)

@Serializable
data class TypeColosseum(
    @SerialName("name_icon")
    val nameIcon: String
)

@Serializable
data class TypeGenerationIv(
    @SerialName("diamond-pearl")
    val diamondPearl: TypeColosseum,

    @SerialName("heartgold-soulsilver")
    val heartgoldSoulsilver: TypeColosseum,

    val platinum: TypeColosseum
)

@Serializable
data class TypeGenerationIx(
    @SerialName("scarlet-violet")
    val scarletViolet: TypeColosseum
)

@Serializable
data class TypeGenerationV(
    @SerialName("black-2-white-2")
    val black2White2: TypeColosseum,

    @SerialName("black-white")
    val blackWhite: TypeColosseum
)

@Serializable
data class TypeGenerationVii(
    @SerialName("lets-go-pikachu-lets-go-eevee")
    val letsGoPikachuLetsGoEevee: TypeColosseum,

    @SerialName("sun-moon")
    val sunMoon: TypeColosseum,

    @SerialName("ultra-sun-ultra-moon")
    val ultraSunUltraMoon: TypeColosseum
)

@Serializable
data class TypeGenerationViii(
    @SerialName("brilliant-diamond-and-shining-pearl")
    val brilliantDiamondAndShiningPearl: TypeColosseum,

    @SerialName("legends-arceus")
    val legendsArceus: TypeColosseum,

    @SerialName("sword-shield")
    val swordShield: TypeColosseum
)