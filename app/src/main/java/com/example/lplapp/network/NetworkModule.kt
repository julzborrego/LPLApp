package com.example.lplapp.network

import com.example.lplapp.network.internal.MessagesService
import com.example.lplapp.network.internal.MessageRepositoryImpl
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


object NetworkModule {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
    private val messagesService: MessagesService = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create())
        .build().create(MessagesService::class.java)

    val messagesRepository : MessagesRepository = MessageRepositoryImpl(messagesService)
}


