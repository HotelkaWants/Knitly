package com.hotelka.knitlyWants.Cards

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.hotelka.knitlyWants.Data.Blog
import com.hotelka.knitlyWants.Data.Likes
import com.hotelka.knitlyWants.Data.UserData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refBlogs
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB.Companion.refProjects
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.extractUrls
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white

@Composable
fun CommentItem(
    postType: Any?,
    postId: String,
    commentId: String,
    userData: UserData,
    commentText: String,
    timestamp: String,
    likes: Likes = Likes(),
    additionalImages: MutableList<String?>
) {
    var liked by remember { mutableStateOf(likes.users!!.contains(com.hotelka.knitlyWants.userData.value.userId)) }
    var likeCount by remember { mutableIntStateOf(likes.total!!) }

    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(textFieldColor)
            .padding(8.dp)

    ) {
        AsyncImage(
            model = if (userData.profilePictureUrl != "") userData.profilePictureUrl
            else R.drawable.baseline_account_circle_24,
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(white),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = userData.username!!,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = textColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            var links by remember { mutableStateOf(extractUrls(commentText.toString())) }
            val annotatedText = buildAnnotatedString {
                links.forEach {
                    withStyle(
                        style = SpanStyle(
                            color = textColor,
                        )
                    ) { append(commentText.split(it.url)[0]) }

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
            }

            if (links.isNotEmpty()) {
                ClickableText(
                    text = annotatedText,
                    style = LocalTextStyle.current.copy(fontSize = 14.sp),

                    ) { offset ->
                    annotatedText
                        .getStringAnnotations("URL", offset, offset)
                        .firstOrNull()?.let { stringAnnotation ->
                            Log.d("url", stringAnnotation.item)
                        }

                }
            } else {
                Text(
                    text = commentText,
                    fontSize = 14.sp,
                    color = textColor
                )
            }
            Spacer(modifier = Modifier.height(5.dp))

            LazyRow (
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(0.dp, 200.dp)
            ){
                itemsIndexed(additionalImages){ index, uri ->
                    AsyncImage(
                        model = uri,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 10.dp)
                            .clip(
                                RoundedCornerShape(
                                    20.dp
                                )
                            ),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = "Comment Image"
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.reply),
                    fontSize = 14.sp,
                    color = textColor
                )
                IconButton(
                    modifier = Modifier.wrapContentSize(),
                    onClick = {
                        liked = !liked
                        if (liked) {
                            likeCount++
                        } else {
                            likeCount--
                        }
                        FirebaseDB.sendLikeComment(
                            commentId, postId, when (postType) {
                                is Blog -> refBlogs
                                else -> refProjects
                            }
                        )
                    }
                ) {
                    Icon(
                        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) headers_activeElement else Color.Gray
                    )

                }
                Text(
                    text = "$likeCount",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )
            }
        }
    }
}