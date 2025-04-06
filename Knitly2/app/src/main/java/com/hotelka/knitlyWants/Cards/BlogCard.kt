package com.hotelka.knitlyWants.Cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.blogCurrent
import com.hotelka.knitlyWants.formatNumber
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData

@Preview
@Composable
fun BlogCard(blog: Blog = Blog()) {
    var context = LocalContext.current
    var likes by remember { mutableStateOf(blog.projectData!!.likes) }
    var likeIcon by remember { mutableStateOf(
        if (blog.projectData!!.likes.users?.contains(
                userData.value.userId
            ) == true
        ) Icons.Filled.Favorite
        else Icons.Outlined.FavoriteBorder
    ) }
    var likeCount by remember { mutableStateOf(blog.projectData!!.likes.total) }
    var iconColorFilter by remember { mutableStateOf(
        if (blog.projectData!!.likes.users?.contains(
                userData.value.userId
            ) == true
        ) ColorFilter.tint(headers_activeElement)
        else ColorFilter.tint(Color.Gray)
    ) }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(white)
            .clickable {
                FirebaseDB.sendReviewBlog(blog.projectData!!.projectId.toString(), blog.projectData!!.reviews)
                blogCurrent = blog
                navController.navigate("projectOverview")
            },
        content = {
            AsyncImage(
                model = blog.projectData!!.cover,
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp),
                contentScale = ContentScale.Crop,
                contentDescription = "SchemePreview"
            )

            Text(
                text = blog.projectData!!.title!!,
                modifier = Modifier
                    .width(300.dp)
                    .padding(horizontal = 5.dp)
                    .padding(10.dp),
                fontSize = 24.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            if (blog.projectData!!.description != "") {
                Text(
                    text = blog.projectData!!.description,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .widthIn(0.dp, 300.dp)
                        .padding(start = 20.dp, end = 20.dp, bottom = 15.dp),
                    fontSize = 16.sp,
                    color = textColor
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp, start = 20.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.reviews),
                    contentDescription = "Reviews",
                    Modifier
                        .size(30.dp),
                    colorFilter = ColorFilter.tint(textColor)
                )
                Text(
                    text = formatNumber(blog.projectData!!.reviews),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 5.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Image(
                    imageVector = if (likes.users?.contains(
                            userData.value.userId
                        ) == true
                    ) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    Modifier
                        .size(30.dp)
                        .padding(start = 5.dp)
                        .clickable {
                            FirebaseDB.sendLikeBlog(
                                blog.projectData!!.projectId.toString(),
                                likes
                            ) { sent, like ->
                                likes = like
                                if (sent){
                                    likeCount = likeCount!! + 1
                                    likeIcon = Icons.Filled.Favorite
                                    iconColorFilter = ColorFilter.tint(headers_activeElement)

                                } else {
                                    likeCount = likeCount!! - 1
                                    likeIcon = Icons.Outlined.FavoriteBorder
                                    iconColorFilter = ColorFilter.tint(Color.Gray)

                                }

                            }
                        },
                    colorFilter = if (likes.users?.contains(
                            userData.value.userId
                        ) == true
                    ) ColorFilter.tint(headers_activeElement)
                    else ColorFilter.tint(Color.Gray)
                )
                Text(
                    text = formatNumber(likeCount!!),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 5.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

        }

    )
}
