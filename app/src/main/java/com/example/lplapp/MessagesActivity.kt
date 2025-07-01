package com.example.lplapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lplapp.network.model.Message
import com.example.lplapp.ui.theme.LPLAppTheme

class MessagesActivity : ComponentActivity() {
    private val viewModel: MessagesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val messagesUIModel by viewModel.messagesUIModel.collectAsStateWithLifecycle()
            val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
                viewModel.updateMessageImage(uri, contentResolver)
            }
            val onProfileImageClicked: (MessageUIItem) -> Unit = { message ->
                viewModel.messageImageSelected(message)
                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
            }
            val onErrorButtonClicked: () -> Unit = {
                viewModel.getMessages()
            }

            MessagesScreen(messagesUIModel, onProfileImageClicked, onErrorButtonClicked)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(
    messagesUIModel: MessagesUIModel,
    onProfileImageClicked: (MessageUIItem) -> Unit,
    onErrorButtonClicked: () -> Unit
) {
    LPLAppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(R.string.message_screen_title)) }
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                when (messagesUIModel) {
                    is MessagesUIModel.Messages -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
                        ) {
                            items(messagesUIModel.list) { message ->
                                OutlinedCard {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        val image by message.image.collectAsStateWithLifecycle()

                                        image?.let {
                                            Image(
                                                bitmap = it,
                                                contentDescription = "avatar image",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .clickable(
                                                        enabled = true,
                                                        onClick = { onProfileImageClicked(message) }
                                                    )
                                                    .size(60.dp)
                                                    .clip(CircleShape)
                                            )
                                        }
                                        if (image == null) {
                                            Image(
                                                painter = painterResource(R.drawable.default_avatar),
                                                contentDescription = "avatar image",
                                                modifier = Modifier.clickable(
                                                    true,
                                                    onClick = {
                                                        onProfileImageClicked(message)
                                                    }
                                                )
                                            )
                                        }

                                        Column {
                                            Row {
                                                Text(
                                                    text = message.name,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.weight(1f)
                                                )
                                                Text(text = message.email)
                                            }
                                            Text(text = message.id.toString())
                                            Text(
                                                text = message.body,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }

                                    }
                                }

                            }
                        }
                    }

                    MessagesUIModel.Empty -> {
                        Text(stringResource(R.string.no_messages))
                    }

                    is MessagesUIModel.Error -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = messagesUIModel.errorMessage
                                    ?: stringResource(R.string.error_message)
                            )
                            Button(onErrorButtonClicked) {
                                Text(stringResource(R.string.try_again))
                            }
                        }
                    }

                    MessagesUIModel.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp)
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MessagesScreenPreview() {
    MessagesScreen(
        MessagesUIModel.Messages(
            listOf(
                MessageUIItem(
                    Message(
                        postId = 3,
                        id = 11,
                        name = "John",
                        email = "John_Goodwin@aol.net",
                        body = "ut dolorum nostrum id quia aut est\nfuga est vel eligendi explicabo"
                    )
                ),
                MessageUIItem(
                    Message(
                        postId = 2,
                        id = 1,
                        name = "Tim",
                        email = "tim@timmothy.net",
                        body = "ut dolorum nostrum id quia aut est\nfuga est inventore vel eligend"
                    )
                ),
                MessageUIItem(
                    Message(
                        postId = 3,
                        id = 12,
                        name = "Jane",
                        email = "Oswald.Vandervort@leanne.org",
                        body = "expedita maiores dignissimos facilis\nipsum est rem est fugit velit sequi\neum odio dolores dolor totam\noccaecati ratione eius rem velit"
                    )
                ),
            )
        ),
        {}, {}
    )
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenLoadingPreview() {
    MessagesScreen(
        MessagesUIModel.Loading,
        {}, {}
    )
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenErrorPreview() {
    MessagesScreen(
        MessagesUIModel.Error("There was an error"),
        {}, {}
    )
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenEmptyPreview() {
    MessagesScreen(
        MessagesUIModel.Empty,
        {}, {}
    )
}