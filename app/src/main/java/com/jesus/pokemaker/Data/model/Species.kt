package com.jesus.pokemaker.Data.model

import kotlinx.serialization.Serializable

@Serializable
data class Species(
    val name: String,
    val url: String
)