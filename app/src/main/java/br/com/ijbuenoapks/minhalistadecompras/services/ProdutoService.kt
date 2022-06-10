package br.com.ijbuenoapks.minhalistadecompras.services

import br.com.ijbuenoapks.minhalistadecompras.models.Produto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProdutoService {

    @GET("{codigoBarras}/json")
    fun buscarCodigoDeBarras(@Path("codigoBarras") codigoDeBarras : String?) : Call<Produto?>?
}