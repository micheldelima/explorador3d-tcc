package com.example.explorador3d_hiro_projeto_final.api

import android.util.Log
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class MyMemoryResponse(
    val responseData: ResponseData
)

data class ResponseData(
    val translatedText: String
)

// Interface Retrofit
interface MyMemoryService {
    @GET("get")
    fun translate(
        @Query("q") text: String,
        @Query("langpair") langPair: String
    ): Call<MyMemoryResponse>
}

// Repositório Singleton
object LibreTranslateRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.mymemory.translated.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(MyMemoryService::class.java)

    fun traduzir(
        texto: String,
        origem: String = "en",
        destino: String = "pt-BR",
        callback: (String) -> Unit
    ) {
        val langPair = "$origem|$destino"

        service.translate(texto, langPair).enqueue(object : Callback<MyMemoryResponse> {
            override fun onResponse(
                call: Call<MyMemoryResponse>,
                response: Response<MyMemoryResponse>
            ) {
                if (response.isSuccessful) {
                    val traducao = response.body()?.responseData?.translatedText ?: texto
                    Log.d("MyMemoryAPI", "Traduzido: $texto → $traducao")
                    callback(traducao)
                } else {
                    Log.e("MyMemoryAPI", "Erro HTTP ${response.code()}")
                    callback(texto)
                }
            }

            override fun onFailure(call: Call<MyMemoryResponse>, t: Throwable) {
                Log.e("MyMemoryAPI", "Erro: ${t.message}")
                callback(texto)
            }
        })
    }
}
