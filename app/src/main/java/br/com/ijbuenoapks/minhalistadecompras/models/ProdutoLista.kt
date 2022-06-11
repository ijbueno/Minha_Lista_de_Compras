package br.com.ijbuenoapks.minhalistadecompras.models

data class ProdutoLista (
    val quantidade : Int,
    val lixeira : Int,
    var produto :  Produto
)
