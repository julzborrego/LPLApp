package com.example.lplapp.network.internal

import com.example.lplapp.network.model.Message
import retrofit2.http.GET

internal interface MessagesService {
    @GET("posts/1/comments/")
    suspend fun getMessages() : List<Message>
}