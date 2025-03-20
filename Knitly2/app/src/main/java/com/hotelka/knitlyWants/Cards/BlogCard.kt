package com.hotelka.knitlyWants.Cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
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
import com.hotelka.knitlyWants.navController
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white
import com.hotelka.knitlyWants.userData

@Preview
@Composable
fun BlogCard(blog: Blog = Blog()) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(white)
            .clickable{
                blogCurrent = blog
                navController.navigate("projectOverview")
            },
        content = {
            AsyncImage(
                model = blog.projectData!!.cover,
                modifier = Modifier
                    .widthIn(300.dp)
                    .heightIn(300.dp),
                contentScale = ContentScale.FillBounds,
                contentDescription = "SchemePreview"
            )

            Text(
                text = blog.projectData!!.title!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .padding(10.dp),
                fontSize = 24.sp,
                color = textColor,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = blog.projectData!!.description,
                textAlign = TextAlign.Justify,
                modifier = Modifier.widthIn(0.dp, 300.dp).padding(start = 20.dp, end = 20.dp, bottom = 15.dp),
                fontSize = 16.sp,
                color = textColor
            )


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
                    text = blog.projectData!!.reviews.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 5.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Image(
                    imageVector = if (blog.projectData!!.likes.users?.contains(
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
                                blog.projectData!!.likes
                            )
                        },
                    colorFilter = ColorFilter.tint(textColor)
                )
                Text(
                    text = blog.projectData!!.likes.total.toString(),
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
