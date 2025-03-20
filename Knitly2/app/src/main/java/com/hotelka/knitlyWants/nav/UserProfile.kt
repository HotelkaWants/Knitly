package com.hotelka.knitlyWants.nav

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.hotelka.knitlyWants.Cards.ExpandedProjectCard
import com.hotelka.knitlyWants.Data.Project
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refProjects
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refUsers
import com.hotelka.knitlyWants.FirebaseUtils.PROJECTS
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.userData

@Preview
@Composable
fun UserProfileScreen(logOut: () -> Unit = {}) {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var usersProjects = remember { mutableStateListOf<Project?>() }
    refUsers.child(userData.value.userId).child(PROJECTS)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersProjects.clear()
                snapshot.children.forEach { project ->
                    var id = project.getValue()
                    refProjects.child(id.toString()).get().addOnSuccessListener {
                        usersProjects.add(it.getValue(Project::class.java))
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            imageUri = uri
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
                    model = if (userData.value.profilePictureUrl?.contains("supabase") == true) userData.value.profilePictureUrl
                    else R.drawable.baseline_account_circle_24,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .clip(CircleShape)
                        .clickable(onClick = {
                            launcher.launch("image/*")
                        })
                        .border(
                            2.dp,
                            Color.Gray,
                            CircleShape
                        ),

                    )


                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${userData.value.username.toString()}, ${userData.value.name.toString()} ${userData.value.lastName.toString()}",
                    fontSize = 24.sp,
                    color = textColor, // Dark text color
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = userData.value.bio.toString(),
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
                        onClick = { /* Handle edit profile */ },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement)
                    ) {
                        Text(stringResource(R.string.edit_profile))
                    }

                    Button(
                        onClick = {


                        },
                        colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement),
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Text(stringResource(R.string.friends_and_fans))
                    }
                }

            }
            item {
                if (usersProjects.isEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                        text = stringResource(R.string.noProject),
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
                    horizontalArrangement = Arrangement.SpaceEvenly

                ) {
                    items(usersProjects) { project ->
                        ExpandedProjectCard(project, userData.value)

                    }
                }
            }

        }
    }
}

