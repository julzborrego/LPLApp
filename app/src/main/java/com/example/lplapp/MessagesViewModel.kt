package com.example.lplapp

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lplapp.network.MessagesRepository
import com.example.lplapp.network.NetworkModule
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.InputStream


class MessagesViewModel(
    private val messagesRepo: MessagesRepository = NetworkModule.messagesRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _messagesUIModel = MutableStateFlow<MessagesUIModel>(MessagesUIModel.Loading)
    val messagesUIModel = _messagesUIModel.asStateFlow()

    private var messageToUpdate: MessageUIItem? = null

    init {
        getMessages()
    }

    fun getMessages() {
        _messagesUIModel.value = MessagesUIModel.Loading
        viewModelScope.launch {
            runCatching {
                val messages = messagesRepo.getMessages().map { MessageUIItem(it) }
                _messagesUIModel.value =
                    if (messages.isEmpty()) {
                        MessagesUIModel.Empty
                    } else {
                        MessagesUIModel.Messages(messages)
                    }
            }.onFailure {
                _messagesUIModel.value = MessagesUIModel.Error(it.message)
            }
        }
    }

    fun messageImageSelected(message: MessageUIItem) {
        messageToUpdate = message
    }

    fun updateMessageImage(uri: Uri?, contentResolver: ContentResolver) {
        messageToUpdate?.let {
            if (uri == null) {
                messageToUpdate = null
            } else {
                viewModelScope.launch(ioDispatcher) {
                    runCatching {
                        val inputStream: InputStream? = contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        it.image.value = bitmap.asImageBitmap()
                    }
                }
            }
        }
    }
}