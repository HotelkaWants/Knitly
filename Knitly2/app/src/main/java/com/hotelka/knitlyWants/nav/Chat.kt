package com.hotelka.knitlyWants.nav

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.Data.Message
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.sendMessage
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.getFileFromUri
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.Tools.ImageViewer
import com.hotelka.knitlyWants.chatOpened
import com.hotelka.knitlyWants.extractUrls
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.toDateString
import com.hotelka.knitlyWants.toDateTimeString
import com.hotelka.knitlyWants.toTimeString
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.secondary
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import kotlin.String
import kotlin.toString

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(modifier: Modifier = Modifier, chat: Chat = Chat()) {
    var messageImages = remember { mutableStateListOf<String?>() }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let { messageImages.add(it.toString()) }
        }
    var context = LocalContext.current
    val scope = rememberCoroutineScope()
    var message by remember { mutableStateOf("") }
    val state = rememberLazyListState()
    var listOfMessages = remember { mutableStateListOf<Message>() }
    FirebaseDB.getChatMessages(chat.id.toString()) {
        var exist = false
        listOfMessages.forEach { message -> if (message.id == it.id) exist = true }
        if (!exist) listOfMessages.add(it)
    }

    LaunchedEffect(listOfMessages.size) {
        if (listOfMessages.isNotEmpty()) state.scrollToItem(
            listOfMessages.sortedBy { message -> message.time }.lastIndex
        )
    }

    var imageViewer by remember { mutableStateOf(false) }
    var images = remember { mutableStateListOf<String>() }
    var startIndex by remember { mutableIntStateOf(0) }

    BackHandler {
        if (imageViewer) {
            imageViewer = false
        } else {
            chatOpened = null
            navController.popBackStack()
        }
    }
    listOfMessages.forEachIndexed { index, message ->
        FirebaseDB.getIfMessageChecked(chat.id.toString(), message.id.toString()) {
            listOfMessages[index] = listOfMessages[index].copy(isChecked = it)
        }
    }
    var reply by remember { mutableStateOf(false) }
    var messageToReply by remember { mutableStateOf<Message?>(null) }

    var delete by remember { mutableStateOf(false) }
    var messageToDelete by remember { mutableStateOf(Message()) }

    var edit by remember { mutableStateOf(false) }
    var editableMessage by remember { mutableStateOf<Message?>(null) }
    var editableMessageIndex by remember { mutableStateOf(0) }

    var isKeyboardOpen = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    Surface(modifier, color = basic) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = if (isKeyboardOpen) 198.dp else 0.dp)
        ) {
            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 80.dp),
                state = state,
                verticalArrangement = Arrangement.Bottom
            ) {
                itemsIndexed(listOfMessages.sortedBy { message -> message.time }) { index, chatMessage ->
                    FirebaseDB.chatChecked(
                        chat.id.toString(),
                        chatMessage
                    ) { chatMessage.isChecked = true }

                    images.addAll(chatMessage.additionalImages)
                    var date = chatMessage.time.toDateString()
                    var prevDate = listOfMessages.sortedBy { message -> message.time }.getOrNull(index - 1)?.time?.toDateString()
                    if (date != prevDate){
                        Box(
                           modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = date,
                                color = white,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(5.dp)
                                    .wrapContentWidth()
                                    .background(accent_secondary, RoundedCornerShape(25.dp))
                                    .padding(10.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                    MessageCard(
                        leftRight = if (chatMessage.user == userData.value.userId) "right"
                        else "left",
                        message = chatMessage,
                        onEdit = {
                            edit = true
                            editableMessage = chatMessage
                            message = chatMessage.text.toString()
                            messageImages.apply {
                                clear(); addAll(
                                chatMessage.additionalImages
                            )
                                editableMessageIndex = index
                            }
                        },
                        onDelete = {
                            delete = true
                            messageToDelete = chatMessage
                        },
                        onReply = {
                            reply = true
                            messageToReply = chatMessage
                        },
                        onReplyClick = {
                            scope.launch {

                                for (it in listOfMessages.sortedBy { message -> message.time }) {
                                    if (it.id == chatMessage.replyTo) {
                                        state.animateScrollToItem(listOfMessages.sortedBy { message -> message.time }
                                            .indexOf(it))
                                        break
                                    }
                                }
                            }

                        },
                        divide = index == 0 || listOfMessages.sortedBy { message -> message.time }
                            .getOrNull(index - 1)?.user != chatMessage.user
                    ) { it ->
                        startIndex = it
                        imageViewer = true
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomEnd),
            ) {
                if (reply) {
                    Row(
                        Modifier
                            .height(50.dp)
                            .fillMaxWidth()
                            .clip(
                                RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                            )
                            .background(white)
                            .padding(start = 5.dp, end = 5.dp, top = 5.dp)
                            .background(
                                color = secondary.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                    ) {
                        Text(
                            text = messageToReply!!.text.toString(),
                            color = textColor,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(end = 5.dp),
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 14.sp,
                        )

                        IconButton(
                            onClick = {
                                messageToReply = null
                                reply = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = textColor
                            )
                        }
                    }
                }
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth(),
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
                                    if (message.isNotEmpty() || messageImages.isNotEmpty()) {
                                        var images = mutableListOf<String>()

                                        var messageData = Message(
                                            messageReplyTo = if (messageToReply != null) messageToReply!!.text else "",
                                            replyTo = if (messageToReply != null) messageToReply!!.id else "",
                                            id = if (editableMessage == null) UUID.randomUUID()
                                                .toString() else editableMessage!!.id,
                                            user = userData.value.userId,
                                            text = message,
                                            isChecked = if (editableMessage == null) false else editableMessage!!.isChecked,
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
                                                                    file.readBytes()
                                                                ).toString()
                                                            )
                                                        )
                                                    }
                                                }
                                            },
                                            edited = editableMessage != null,
                                            time = if (editableMessage == null) System.currentTimeMillis() else editableMessage!!.time
                                        )
                                        if (edit) {
                                            FirebaseDB.updateMessage(
                                                messageData,
                                                chat.id.toString()
                                            ) {
                                                message = ""
                                                messageImages.clear()
                                                listOfMessages.apply {
                                                    removeAt(editableMessageIndex)
                                                    add(editableMessageIndex, messageData)
                                                }
                                            }
                                        } else {
                                            sendMessage(messageData, chat.id.toString()) {
                                                message = ""
                                                messageImages.clear()
                                            }
                                        }
                                        messageToReply = null
                                        reply = false
                                        editableMessage = null
                                        edit = false
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = null,
                                    tint = if (message.isNotEmpty() || messageImages.isNotEmpty()) headers_activeElement else Color.Gray
                                )
                            }

                            IconButton(onClick = {
                                if (messageImages.size <= 6)
                                    launcher.launch("image/*")
                            }) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.attach),
                                    contentDescription = null,
                                    tint =
                                    if (messageImages.size <= 6)
                                        headers_activeElement else Color.Gray
                                )
                            }
                        }
                    }
                )
                if (messageImages.isNotEmpty()) {

                    LazyRow(
                        modifier = Modifier
                            .background(white)
                            .fillMaxWidth()
                            .heightIn(0.dp, 200.dp)
                    ) {

                        itemsIndexed(messageImages) { indexI, uri ->

                            AsyncImage(
                                model = uri,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            20.dp
                                        )
                                    )
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = {
                                            messageImages.removeAt(
                                                indexI
                                            )
                                        }
                                    ),
                                contentScale = ContentScale.FillWidth,
                                contentDescription = "Blog Image"
                            )
                        }

                    }
                }
            }
        }
        AnimatedVisibility(
            visible = imageViewer,
            enter = slideInVertically(
                initialOffsetY = {
                    it / 2
                },
            ),
            exit = slideOutVertically(
                targetOffsetY = {
                    it / 2
                },
            ),
        ) {
            ImageViewer(images.toList(), startIndex = startIndex) { imageViewer = false }
        }
        if (delete) {
            DeleteChatMessage(messageToDelete, onDismiss = { delete = false }) {
                FirebaseDB.deleteMessage(messageToDelete.id.toString(), chat.id.toString()) {
                    listOfMessages.remove(messageToDelete)
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteChatMessage(message: Message, onDismiss: () -> Unit, onDelete: () -> Unit) {
    BasicAlertDialog(
        onDismissRequest = {
            onDismiss()
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.deleteMessage) + message.time.toDateTimeString() + "?",
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    color = textColor

                )


                Row(
                    Modifier.wrapContentSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.cancel))

                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        onClick = {
                            onDelete()
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(R.string.delete))

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageCard(
    leftRight: String = "left",
    message: Message = Message(),
    divide: Boolean = false,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onReply: () -> Unit,
    onReplyClick: () -> Unit,
    callImageViewer: (Int) -> Unit,
) {
    var user by remember { mutableStateOf(UserData()) }
    FirebaseDB.getUser(message.user!!){user = it}
    var popUp by remember { mutableStateOf(false) }
    val time = message.time.toTimeString()
    var links by remember { mutableStateOf(extractUrls(message.text.toString())) }
    val text = buildAnnotatedString {

        withStyle(
            style = SpanStyle(
                color = textColor,
            ),
        ) { append(message.text.toString()) }
        links.forEach { urlData ->
            addLink(
                url = LinkAnnotation.Url(url = urlData.url),
                start = urlData.start,
                end = urlData.end
            )
            addStyle(
                style = SpanStyle(
                    color = headers_activeElement,
                    textDecoration = TextDecoration.Underline
                ),
                start = urlData.start,
                end = urlData.end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = urlData.url,
                start = urlData.start,
                end = urlData.end
            )

        }
    }
    Row(Modifier.wrapContentWidth()) {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth()
                .padding(
                    start = if (leftRight == "right") 100.dp else 0.dp,
                    end = if (leftRight == "left") 100.dp else 0.dp
                ),
            horizontalAlignment = if (leftRight == "right") Alignment.End else Alignment.Start
        ) {
            if (divide && message.user != userData.value.userId) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    AsyncImage(
                        model = user.profilePictureUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(50.dp)
                            .border(width = 1.dp, Color(248, 242, 242), CircleShape),
                    )
                    Text(
                        text = user.name + " " + user.lastName,
                        fontSize = 16.sp,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box {
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
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                popUp = true
                            }
                        )

                ) {
                    Column(
                        Modifier
                            .padding(10.dp)
                    ) {
                        if (message.edited == true) {
                            Text(
                                text = stringResource(R.string.edited),
                                fontSize = 12.sp,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 15.dp)
                            )
                        }
                        if (message.replyTo?.isNotEmpty() == true) {
                            Text(
                                text = message.messageReplyTo.toString(),
                                color = textColor,
                                modifier = Modifier
                                    .height(50.dp)
                                    .fillMaxWidth()
                                    .clip(
                                        RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)
                                    )
                                    .background(if (leftRight == "right") white else accent_secondary)
                                    .clickable {
                                        onReplyClick()
                                    }
                                    .padding(start = 5.dp, end = 5.dp, top = 5.dp)
                                    .background(
                                        color = (if (leftRight == "right") secondary else textFieldColor).copy(
                                            alpha = 0.8f
                                        ),
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .padding(start = 15.dp, end = 15.dp, top = 15.dp),
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 14.sp,
                            )
                        }
                        if (message.additionalImages.isNotEmpty()) {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(100.dp),
                                userScrollEnabled = false,
                                modifier = Modifier
                                    .heightIn(0.dp, 1000.dp)
                                    .widthIn(max = 320.dp)
                            ) {
                                itemsIndexed(message.additionalImages) { index, image ->
                                    AsyncImage(
                                        model = image,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp)
                                            .clip(
                                                RoundedCornerShape(
                                                    10.dp
                                                )
                                            )
                                            .combinedClickable(
                                                onClick = { callImageViewer(index) },
                                                onLongClick = { popUp = true }
                                            ),
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                        if (text.isNotEmpty()) {
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
                        }
                        Row(
                            modifier = Modifier
                                .wrapContentWidth()
                                .padding(end = 10.dp)
                                .align(Alignment.End)
                                .combinedClickable(
                                    onClick = {},
                                    onLongClick = { popUp = true }
                                ),
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
                if (popUp) {
                    Popup(
                        alignment = if (leftRight == "right") Alignment.TopStart else Alignment.TopEnd,
                        onDismissRequest = { popUp = false }) {
                        Column(
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.9F),
                                    RoundedCornerShape(10.dp)
                                )
                                .border(1.dp, textColor, RoundedCornerShape(10.dp))
                                .padding(16.dp)

                        ) {
                            if (message.user == userData.value.userId) {
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            popUp = false
                                            onDelete()
                                        }
                                        .padding(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape),
                                        tint = textColor
                                    )

                                    Text(
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .align(Alignment.CenterVertically)
                                            .padding(start = 10.dp),
                                        text = stringResource(R.string.delete),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )

                                }
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            popUp = false
                                            onEdit()
                                        }
                                        .padding(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape),
                                        tint = textColor
                                    )

                                    Text(
                                        modifier = Modifier
                                            .wrapContentWidth()
                                            .align(Alignment.CenterVertically)
                                            .padding(start = 10.dp),
                                        text = stringResource(R.string.edit),
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )

                                }
                            }
                            Row(
                                modifier = Modifier
                                    .clickable {
                                        popUp = false
                                        onReply()
                                    }
                                    .padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.baseline_reply_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape),
                                    tint = textColor
                                )

                                Text(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .align(Alignment.CenterVertically)
                                        .padding(start = 10.dp),
                                    text = stringResource(R.string.reply),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )

                            }
                        }
                    }
                }
            }

        }

    }
}