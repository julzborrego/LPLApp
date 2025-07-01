package com.example.lplapp

import androidx.compose.ui.graphics.ImageBitmap
import com.example.lplapp.network.model.Message
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface MessagesUIModel {
    object Loading : MessagesUIModel
    class Error(val errorMessage: String?) : MessagesUIModel
    object Empty : MessagesUIModel
    class Messages(val list: List<MessageUIItem>) : MessagesUIModel
}

data class MessageUIItem(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String,
    val image: MutableStateFlow<ImageBitmap?>
) {
    constructor(message: Message) : this(
        postId = message.postId,
        id = message.id,
        name = message.name,
        email = message.email,
        body = message.body,
        image = MutableStateFlow(null),
    )
}