package com.example.lplapp.network.internal

import com.example.lplapp.network.MessagesRepository
import com.example.lplapp.network.model.Message
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class MessageRepositoryImpl(
    private val messagesService: MessagesService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MessagesRepository {
    override suspend fun getMessages(): List<Message> = withContext(ioDispatcher) {
        messagesService.getMessages()
    }
}