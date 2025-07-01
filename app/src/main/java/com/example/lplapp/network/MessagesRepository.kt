package com.example.lplapp.network

import com.example.lplapp.network.model.Message

interface MessagesRepository {
    suspend fun getMessages():List<Message>
}