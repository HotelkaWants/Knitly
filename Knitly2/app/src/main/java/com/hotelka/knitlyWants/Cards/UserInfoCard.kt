package com.hotelka.knitlyWants.Cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refUsers
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.SupportingDatabase.SupportingDatabase
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData
import com.hotelka.knitlyWants.userWatching
import com.hotelka.knitlyWants.users

@Preview
@Composable
fun UserInfoCard(user: UserData = UserData()) {
    var context = LocalContext.current

    var isFollowing by remember { mutableStateOf(user.subscribers?.contains(userData.value.userId)!!) }
    var isFollowed by remember { mutableStateOf(userData.value.subscribers?.contains(user.userId)!!) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(basic)
                .padding(16.dp)
                .clickable{
                    userWatching = user
                    navController.navigate("userProfile")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = if (user.profilePictureUrl != "") user.profilePictureUrl
                else R.drawable.baseline_account_circle_24,
                contentDescription = "Author Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = if (user.name!!.isNotEmpty()) user.name + " " + user.lastName
                    else stringResource(R.string.guest),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = user.username.toString(),
                    fontWeight = FontWeight.Light,
                    fontSize = 16.sp,
                    color = textColor
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (user.userId != userData.value.userId) {
                Button(
                    onClick = {
                        FirebaseDB.subscribe(user.userId , isFollowing) {
                            isFollowing = !isFollowing
                            val db = SupportingDatabase(context)
                            refUsers.get().addOnSuccessListener { snapshot ->
                                for (user in snapshot.children) {
                                    user.getValue(UserData::class.java)?.let {
                                        db.addUser(it)
                                    }
                                }
                                users.value = db.getAllUsers()
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFollowing == true) Color.LightGray else headers_activeElement,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .wrapContentWidth()
                ) {
                    Text(
                        text = if (isFollowed && !isFollowing) stringResource(R.string.befriend) else if (isFollowing == true) stringResource(R.string.unsubscribe) else stringResource(
                            R.string.subscribe
                        ),
                        fontSize = 12.sp
                    )
                }
            }

    }

}