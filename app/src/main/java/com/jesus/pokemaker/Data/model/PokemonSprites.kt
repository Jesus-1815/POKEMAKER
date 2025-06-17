package com.jesus.pokemaker.Data.model

import kotlinx.serialization.*

@Serializable
data class PokemonSprites(
    @SerialName("back_default")
    val backDefault: String,

    @SerialName("back_female")
    val backFemale: String? = null,

    @SerialName("back_shiny")
    val backShiny: String,

    @SerialName("back_shiny_female")
    val backShinyFemale: String? = null,

    @SerialName("front_default")
    val frontDefault: String,

    @SerialName("front_female")
    val frontFemale: String? = null,

    @SerialName("front_shiny")
    val frontShiny: String,

    @SerialName("front_shiny_female")
    val frontShinyFemale: String? = null,

    val other: Other? = null,
    val versions: Versions? = null,
    val animated: PokemonSprites? = null
)

@Serializable
data class Other(
    @SerialName("dream_world")
    val dreamWorld: DreamWorld,

    val home: Home,

    @SerialName("official-artwork")
    val officialArtwork: OfficialArtwork,

    val showdown: PokemonSprites? = null
)

@Serializable
data class Versions(
    @SerialName("generation-i")
    val generationI: GenerationI,

    @SerialName("generation-ii")
    val generationIi: GenerationIi,

    @SerialName("generation-iii")
    val generationIii: GenerationIii,

    @SerialName("generation-iv")
    val generationIv: GenerationIv,

    @SerialName("generation-v")
    val generationV: GenerationV,

    @SerialName("generation-vi")
    val generationVi: Map<String, Home>,

    @SerialName("generation-vii")
    val generationVii: GenerationVii,

    @SerialName("generation-viii")
    val generationViii: GenerationViii
)

@Serializable
data class GenerationI(
    @SerialName("red-blue")
    val redBlue: RedBlue,

    val yellow: RedBlue
)

@Serializable
data class RedBlue(
    @SerialName("back_default")
    val backDefault: String,

    @SerialName("back_gray")
    val backGray: String,

    @SerialName("back_transparent")
    val backTransparent: String,

    @SerialName("front_default")
    val frontDefault: String,

    @SerialName("front_gray")
    val frontGray: String,

    @SerialName("front_transparent")
    val frontTransparent: String
)

@Serializable
data class GenerationIi(
    val crystal: Crystal,
    val gold: Gold,
    val silver: Gold
)

@Serializable
data class Crystal(
    @SerialName("back_default")
    val backDefault: String,

    @SerialName("back_shiny")
    val backShiny: String,

    @SerialName("back_shiny_transparent")
    val backShinyTransparent: String,

    @SerialName("back_transparent")
    val backTransparent: String,

    @SerialName("front_default")
    val frontDefault: String,

    @SerialName("front_shiny")
    val frontShiny: String,

    @SerialName("front_shiny_transparent")
    val frontShinyTransparent: String,

    @SerialName("front_transparent")
    val frontTransparent: String
)

@Serializable
data class Gold(
    @SerialName("back_default")
    val backDefault: String,

    @SerialName("back_shiny")
    val backShiny: String,

    @SerialName("front_default")
    val frontDefault: String,

    @SerialName("front_shiny")
    val frontShiny: String,

    @SerialName("front_transparent")
    val frontTransparent: String? = null
)

@Serializable
data class GenerationIii(
    val emerald: OfficialArtwork,

    @SerialName("firered-leafgreen")
    val fireredLeafgreen: Gold,

    @SerialName("ruby-sapphire")
    val rubySapphire: Gold
)

@Serializable
data class OfficialArtwork(
    @SerialName("front_default")
    val frontDefault: String,

    @SerialName("front_shiny")
    val frontShiny: String
)

@Serializable
data class Home(
    @SerialName("front_default")
    val frontDefault: String,

    @SerialName("front_female")
    val frontFemale: String? = null,

    @SerialName("front_shiny")
    val frontShiny: String,

    @SerialName("front_shiny_female")
    val frontShinyFemale: String? = null
)

@Serializable
data class GenerationIv(
    @SerialName("diamond-pearl")
    val diamondPearl: PokemonSprites,

    @SerialName("heartgold-soulsilver")
    val heartgoldSoulsilver: PokemonSprites,

    val platinum: PokemonSprites
)

@Serializable
data class GenerationV(
    @SerialName("black-white")
    val blackWhite: PokemonSprites
)

@Serializable
data class GenerationVii(
    val icons: DreamWorld,

    @SerialName("ultra-sun-ultra-moon")
    val ultraSunUltraMoon: Home
)

@Serializable
data class DreamWorld(
    @SerialName("front_default")
    val frontDefault: String,

    @SerialName("front_female")
    val frontFemale: String? = null
)

@Serializable
data class GenerationViii(
    val icons: DreamWorld
)