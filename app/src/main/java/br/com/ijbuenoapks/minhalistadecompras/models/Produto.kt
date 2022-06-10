package br.com.ijbuenoapks.minhalistadecompras.models

import com.fasterxml.jackson.annotation.JsonProperty

data class Produto (
    @JsonProperty("id") val id : Long,
    @JsonProperty("produto") val produto : String,
    @JsonProperty("descricao") val descricao : String,
    @JsonProperty("valor") val valor : Float,
    @JsonProperty("codigoBarras") val codigoBarras : String
        )