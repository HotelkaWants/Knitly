package com.hotelka.knitlyWants.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.hotelka.knitlyWants.Cards.BlogCard
import com.hotelka.knitlyWants.Cards.ProjectContainer
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.BLOGS
import com.hotelka.knitlyWants.FirebaseUtils.PROJECTS
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.editableProject
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.accent_secondary
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.users


@Composable
fun HomeScreen() {
    var projects = remember { mutableStateListOf<Project>() }
    FirebaseDatabase.getInstance().getReference().child(PROJECTS).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            projects.clear()
            for (childSnapshot in snapshot.children) {
                val item = childSnapshot.getValue(Project::class.java)
                projects.add(item!!)
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    })
    var blogs = remember { mutableStateListOf<Blog>() }
    FirebaseDatabase.getInstance().getReference().child(BLOGS).addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            blogs.clear()
            for (childSnapshot in snapshot.children) {
                val item = childSnapshot.getValue(Blog::class.java)
                blogs.add(item!!)
            }
        }

        override fun onCancelled(error: DatabaseError) {
        }
    })

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            item {
                LazyRow(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    items(users.value) {
                        UserItem(it)
                    }
                }

            }

            item {
                Text(
                    text = stringResource(R.string.news),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                topEnd = 20.dp,
                                topStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        )
                        .background(accent_secondary)
                        .padding(10.dp),
                    fontSize = 28.sp,
                    color = white
                )
            }
            item{
                LazyRow(modifier = Modifier.padding(10.dp)) {
                    itemsIndexed(blogs){index, blog ->
                        BlogCard(blog)
                    }
                }
            }
            item {
                Text(
                    text = stringResource(R.string.inspiration),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(
                            RoundedCornerShape(
                                topEnd = 20.dp,
                                topStart = 20.dp,
                                bottomEnd = 20.dp
                            )
                        )
                        .background(accent_secondary)
                        .padding(10.dp),
                    fontSize = 28.sp,
                    color = white
                )
            }
            itemsIndexed(projects) { index, project ->
                ProjectContainer(project)
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .padding(20.dp)
                .wrapContentSize()
                .align(Alignment.BottomEnd),
            onClick = {
                editableProject = null
                navController.navigate("createProject")
            },
            containerColor = accent_secondary,
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomStart = 30.dp
            ),
            elevation = FloatingActionButtonDefaults.elevation(10.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "add", tint = white)
        }
    }
}

@Composable
fun UserItem(user: UserData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(10.dp)
            .background(Color.Transparent),
        content = {
            if (user.profilePictureUrl != null) {
                AsyncImage(
                    model = user.profilePictureUrl,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    contentDescription = "SchemePreview"
                )
            } else {
                AsyncImage(
                    model = R.drawable.baseline_account_circle_24,
                    modifier = Modifier.size(70.dp),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = "SchemePreview"
                )
            }

            Text(
                text = user.username!!,
                modifier = Modifier
                    .fillMaxWidth(),
                fontSize = 14.sp,
                color = textColor
            )

        }
    )
}

