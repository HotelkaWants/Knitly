package com.hotelka.knitlyWants.Cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.darkBasic
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userWatching

@Preview
@Composable
fun UserInfoCard(user: UserData = UserData(), lazyOrientation: String = "Horizontal") {
    var imageWidth = 0.dp
    Row(
        modifier = Modifier
            .padding(end = 10.dp)
            .padding(vertical = 10.dp)
            .height(120.dp)
            .wrapContentWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, darkBasic, RoundedCornerShape(10.dp))
            .background(white)
            .clickable{
                userWatching = user
                navController.navigate("userProfile")
            }
    ) {
        AsyncImage(
            modifier = Modifier.wrapContentWidth()
                .fillMaxHeight()
                .onGloballyPositioned {
                    imageWidth = it.size.width.dp
                },
            model = if (user.profilePictureUrl!!.isEmpty()) R.drawable.baseline_account_circle_24
            else user.profilePictureUrl,
            contentDescription = null,
            contentScale = ContentScale.FillHeight
        )
        Column(
            modifier = if(lazyOrientation == "Horizontal") Modifier.width(150.dp + imageWidth)
            else Modifier.fillMaxWidth()
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
                text = user.name + " " + user.lastName,
                style = TextStyle.Default.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = textColor,
                fontSize = 12.sp,
            )
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = user.username!!,
                color = textColor,
                fontSize = 10.sp,
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 15.dp),
                text = user.bio!!,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle.Default.copy(
                ),
                color = textColor,
                fontSize = 10.sp,
            )
        }
    }

}