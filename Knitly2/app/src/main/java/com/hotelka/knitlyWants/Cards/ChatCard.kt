package com.hotelka.knitlyWants.Cards

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.chatOpened
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.toTimeString
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.userData

@Preview
@Composable
fun ChatCard(chat: Chat = Chat()) {
    Box(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                chatOpened = chat
                navController.navigate("chat")
            }
            .background(darkBasic)
            .padding(vertical = 2.dp)
            .background(basic)
            .padding(5.dp)
    ) {
        var profilePicture by remember { mutableStateOf<String>("") }
        var name by remember { mutableStateOf<String>("") }
        var isOnline by remember { mutableStateOf(false) }
        var lastMessage by remember {
            mutableStateOf(if (chat.messages.isNotEmpty()) chat.messages.values.sortedBy { message -> message.time }
                .last() else null)
        }
        chat.users?.forEach { user ->
            if (user != userData.value.userId) {
                FirebaseDB.getUser(user) { user ->
                    profilePicture = user.profilePictureUrl.toString()
                    name = user.name.toString()
                    user.lastName?.let {
                        if (it.isNotEmpty()) {
                            name += " $it"
                        }
                    }
                    isOnline = user.isOnline
                }
            }
        }

        Row {
            Box {
                AsyncImage(
                    model = if (profilePicture.isNotEmpty()) profilePicture
                    else R.drawable.baseline_account_circle_24,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop

                )
                if (isOnline) {
                    Badge(
                        containerColor = headers_activeElement,
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.TopEnd)
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.height(50.dp)
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 5.dp)
                        .padding(end = 50.dp),
                    text = name,
                    fontSize = 14.sp,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (lastMessage == null) stringResource(R.string.sayHi) else
                        if (lastMessage?.user!!.userId == userData.value.userId) stringResource(R.string.you) + lastMessage?.text!!
                        else "$name: ${lastMessage?.text}",
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 5.dp)
                        .padding(end = 50.dp),
                    overflow = TextOverflow.Ellipsis,
                    color = textColor,
                    style = LocalTextStyle.current.copy(fontSize = 12.sp),
                )
            }
            Row(
                modifier = Modifier
                    .padding(end = 10.dp)
                    .fillMaxWidth()
                    .height(45.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (lastMessage == null) "" else lastMessage!!.time.toTimeString(),
                    fontSize = 12.sp,
                    color = textColor,
                )
                if (lastMessage != null) {
                    Icon(
                        imageVector = ImageVector.vectorResource(
                            if (!lastMessage!!.isChecked) R.drawable.baseline_done_24
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