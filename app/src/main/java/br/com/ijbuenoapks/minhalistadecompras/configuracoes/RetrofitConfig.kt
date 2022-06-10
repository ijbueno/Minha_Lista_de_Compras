package br.com.ijbuenoapks.minhalistadecompras.configuracoes

import br.com.ijbuenoapks.minhalistadecompras.services.ProdutoService
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class RetrofitConfig {

    private var retrofit : Retrofit? = null

    fun RetrofitConfig(){
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.0.148:8080/api/produtos/codigoBarras/")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
    }

    fun getProdutoService() : ProdutoService {
        if(retrofit == null){
            RetrofitConfig()
        }
        return retrofit!!.create(ProdutoService::class.java)
    }
}