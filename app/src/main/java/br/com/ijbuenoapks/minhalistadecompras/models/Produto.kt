package br.com.ijbuenoapks.minhalistadecompras.models

data class Produto (
    val id : Long,
    val produto : String,
    val descricao : String,
    val valor : Float,
    val codigoBarras : String
        )