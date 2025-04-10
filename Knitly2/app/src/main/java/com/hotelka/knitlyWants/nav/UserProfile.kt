package com.hotelka.knitlyWants.nav

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Cards.BlogCard
import com.hotelka.knitlyWants.Cards.ExpandedProjectCard
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Chat
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.BLOGS
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refBlogs
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refChats
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refProjects
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refUsers
import com.hotelka.knitlyWants.FirebaseUtils.PROJECTS
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.chatOpened
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData

@Composable
fun UserProfile(user: UserData) {
    var isFollowing by remember { mutableStateOf(user.subscribers?.contains(userData.value.userId)!!) }

    var usersProjects = remember { mutableStateListOf<Project>() }
    refUsers.child(user.userId).child(PROJECTS).get().addOnSuccessListener { snapshot ->
        snapshot.children.forEach { project ->
            var id = project.value
            refProjects.child(id.toString()).get().addOnSuccessListener {
                var project = it.getValue(Project::class.java)
                if (!usersProjects.contains(project)) {
                    project?.let { element -> usersProjects.add(element) }
                }
            }
        }
    }
    var usersBlogs = remember { mutableStateListOf<Blog>() }
    refUsers.child(user.userId).child(BLOGS).get().addOnSuccessListener { snapshot ->
        usersBlogs.clear()
        snapshot.children.forEach { blog ->
            var id = blog.value
            refBlogs.child(id.toString()).get().addOnSuccessListener {
                val blog = it.getValue(Blog::class.java)
                if (!usersBlogs.contains(blog))
                    blog?.let { element -> usersBlogs.add(element) }
            }

        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = basic // Background color
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                AsyncImage(
                    model = if (user.profilePictureUrl?.isNotEmpty() == true) user.profilePictureUrl
                    else R.drawable.baseline_account_circle_24,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            Color.Gray,
                            CircleShape
                        ),

                    )


                Spacer(modifier = Modifier.height(16.dp))

                var usersCreds =
                    user.username.toString() + (if (user.name.toString()
                            .isNotEmpty()
                    ) ", ${user.name.toString()} " else "") + user.lastName.toString()
                Text(
                    text = usersCreds,
                    fontSize = 24.sp,
                    color = textColor, // Dark text color
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user.bio.toString(),
                    fontSize = 14.sp,
                    color = textColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {

                            var notExist = false
                            refChats.get().addOnSuccessListener {
                                it.children.forEachIndexed { index, child ->
                                    var chat = child.getValue(Chat::class.java)
                                    if (chat?.users!!.contains(userData.value.userId) && chat.users.contains(
                                            user.userId
                                        )
                                    ) {
                                        chatOpened = chat
                                        navController.navigate("chat")
                                        notExist = false
                                    }
                                    if (index == it.children.toList().size - 1 && chatOpened == null){
                                        FirebaseDB.createChat(user.userId) {
                                            userData.value.chats += it.id!!
                                            chatOpened = it
                                            navController.navigate("chat")
                                        }
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement)
                    ) {
                        Icon(
                            tint = white,
                            imageVector = ImageVector.vectorResource(R.drawable.round_message_24),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 5.dp)
                        )
                        Text(stringResource(R.string.chat))
                    }

                    Button(
                        onClick = {
                            FirebaseDB.subscribe(user.userId, isFollowing) {
                                isFollowing = !isFollowing
                            }
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowing == true) Color.LightGray else headers_activeElement,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (isFollowing == true) stringResource(R.string.unsubscribe) else stringResource(
                                R.string.subscribe
                            ),
                            fontSize = 12.sp
                        )
                    }
                }

            }
            item {
                if (usersProjects.isEmpty() && usersBlogs.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                        text = stringResource(R.string.noPosts),
                        textAlign = TextAlign.Center,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .background(Transparent)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.Start

                ) {

                    itemsIndexed(usersProjects) { index, project ->
                        ExpandedProjectCard(project, user)

                    }
                }
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .wrapContentHeight()
                        .background(Transparent)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.Start

                ) {
                    items(usersBlogs) { blog ->
                        BlogCard(blog)
                    }
                }
            }

        }
    }
}