package com.hotelka.knitlyWants.nav

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.database.ServerValue
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.Data.Message
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.sendMessage
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.getFileFromUri
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.extractUrls
import com.hotelka.knitlyWants.toTimeString
import com.hotelka.knitlyWants.ui.theme.KnitlyTheme
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.String
import kotlin.toString

@Composable
fun ChatScreen(modifier: Modifier = Modifier, chat: Chat = Chat()) {
//    val launcher =
//        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
//            uri?.let { additionalImagesComments.add(it.toString()) }
//        }
    var context = LocalContext.current
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("") }
    var messageImages = remember { mutableStateListOf<String?>() }
    val state = rememberLazyListState()
    val listOfMessages = remember { mutableStateListOf<Message>() }
    FirebaseDB.getChatMessages(chat.id.toString()){
        var exist = false
        listOfMessages.forEach { message -> if (message.id == it.id) exist = true}
        if (!exist) listOfMessages.add(it)
    }

    LaunchedEffect(listOfMessages.size) { if (listOfMessages.isNotEmpty()) state.scrollToItem(listOfMessages.lastIndex) }
    var isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current)>0
    Surface(modifier, color = basic) {
        Box(contentAlignment = Alignment.BottomStart, modifier = modifier.fillMaxSize().padding(bottom = if (isKeyboardOpen) 198.dp else 0.dp)) {
            LazyColumn(Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp), state = state) {
                itemsIndexed(listOfMessages.sortedBy { message -> message.time }) { index, message ->
                    FirebaseDB.chatChecked(chat.id.toString(), message){ message.isChecked = true}
                    FirebaseDB.getIfMessageChecked(chat.id.toString(), message.id.toString()){
                        message.isChecked = it
                    }

                    MessageCard(
                        leftRight = if (message.user.userId == userData.value.userId) "right"
                        else "left",
                        message = message,
                        divide = index == 0 || listOfMessages.sortedBy { message -> message.time }.getOrNull(index-1)?.user?.userId != message.user.userId
                    )
                }
            }
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
                textStyle = TextStyle(fontSize = 16.sp),
                label = { Text(text = stringResource(R.string.sendMessage)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = white,
                    unfocusedContainerColor = white,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedLabelColor = textColor,
                    unfocusedLabelColor = textColor
                ),
                trailingIcon = {
                    Row {
                        IconButton(onClick = {
                            scope.launch {
                                if (message.isNotEmpty()) {
                                    var images = mutableListOf<String>()

                                    var messageData = Message(
                                        id = UUID.randomUUID().toString(),
                                        user = userData.value,
                                        text = message,
                                        isChecked = false,
                                        additionalImages = images.apply {
                                            messageImages.forEachIndexed { index, uri ->
                                                var file: File? = null
                                                file = getFileFromUri(context, Uri.parse(uri))
                                                file?.let { file ->
                                                    images.add(
                                                        getData(
                                                            "messages",
                                                            uploadFile(
                                                                "messages",
                                                                userData.value.username!!,
                                                                file.readBytes()
                                                            ).toString()
                                                        )
                                                    )
                                                }
                                            }
                                        },
                                        time = System.currentTimeMillis()
                                    )
                                    sendMessage(messageData, chat.id.toString()) {
                                        message = ""
                                        messageImages.clear()
                                    }
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = null,
                                tint = if (message.isNotEmpty()) headers_activeElement else Color.Gray
                            )
                        }

                        IconButton(onClick = {
//                                launcher.launch("image/*")
                        }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.attach),
                                contentDescription = null,
                                tint = headers_activeElement
                            )
                        }
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KnitlyTheme {
        ChatScreen()
    }
}

@Preview
@Composable
fun MessageCard(
    leftRight: String = "left",
    message: Message = Message(),
    divide: Boolean = false
) {

    val time = message.time.toTimeString()
    var links by remember { mutableStateOf(extractUrls(message.text.toString())) }
    val text = buildAnnotatedString {
        links.forEach {
            withStyle(
                style = SpanStyle(
                    color = textColor,
                )
            ) { append(message.text?.split(it.url)[0]) }

            withLink(LinkAnnotation.Url(url = it.url)) {
                addStyle(
                    style = SpanStyle(
                        color = headers_activeElement,
                        textDecoration = TextDecoration.Underline
                    ),
                    start = it.start,
                    end = it.end
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = it.url,
                    start = it.start,
                    end = it.end
                )
                append(it.url)

            }

        }
        if (links.isNotEmpty()) append(message.text?.split(links.last().url)?.get(1))
        else withStyle(
            style = SpanStyle(
                color = textColor,
            )
        ) { append(message.text) }
    }
    Column(
        modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth(),
        horizontalAlignment = if (leftRight == "right") Alignment.End else Alignment.Start
    ) {
        if (divide && message.user.userId != userData.value.userId) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                AsyncImage(
                    model = message.user.profilePictureUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(50.dp)
                        .border(width = 1.dp, Color(248, 242, 242), CircleShape),
                )
                Text(
                    text = message.user.name + " " + message.user.lastName,
                    fontSize = 16.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (leftRight == "right") white
                else accent_secondary
            ),
            shape = RoundedCornerShape(
                bottomEnd = 20.dp,
                bottomStart = 20.dp,
                topEnd = if (leftRight == "right" && divide) 0.dp else 20.dp,
                topStart = if (leftRight == "left" && divide) 0.dp else 20.dp,

                ),
            modifier = Modifier
                .padding(5.dp)
                .wrapContentSize()
        ) {
            Column(
                Modifier
                    .padding(10.dp)
                    .padding(end = if (leftRight == "left" && divide) 30.dp else 0.dp)
            ) {
                ClickableText(
                    text = text,
                    modifier = Modifier
                        .widthIn(80.dp, 400.dp)
                        .wrapContentHeight()
                        .padding(10.dp)
                        .padding(start = 5.dp),
                    style = LocalTextStyle.current.copy(
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start
                    ),
                    onClick = { offset ->
                        text
                            .getStringAnnotations("URL", offset, offset)
                            .firstOrNull()?.let { stringAnnotation ->
                                Log.d("url", stringAnnotation.item)
                            }

                    },
                )
                Row(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(end = 10.dp)
                        .align(Alignment.End),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = time,
                        fontSize = 12.sp,
                        color = textColor,
                    )
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (!message.isChecked) R.drawable.baseline_done_24
                            else R.drawable.baseline_done_all_24
                        ),
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(20.dp)
                    )

                }
            }

        }
    }
}
