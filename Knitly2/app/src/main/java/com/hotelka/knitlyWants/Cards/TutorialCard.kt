package com.hotelka.knitlyWants.Cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.secondary
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.white

@Composable
@Preview
fun TutorialCard() {
    var title = "Title"
    var quickDescription = "Quick Description"
    var liked by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(
                white,
                RoundedCornerShape(20.dp)
            ),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(white)
                .padding(10.dp)
        ) {

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = textColor
            )
            Text(
                text = quickDescription,
                fontSize = 16.sp,
                color = textColor
            )
        }

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(secondary)
            ) {

                Image(
                    painter = painterResource(R.drawable.reviews),
                    contentDescription = "Reviews",
                    Modifier
                        .padding(start = 15.dp)
                        .size(25.dp)
                        .align(Alignment.CenterVertically),
                    colorFilter = ColorFilter.tint(textColor)
                )
                Text(
                    text = 0.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 5.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Image(
                    imageVector = if (liked) Icons.Filled.Favorite
                    else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like",
                    Modifier
                        .size(25.dp)
                        .align(Alignment.CenterVertically)
                        .clickable {

                        },
                    colorFilter = ColorFilter.tint(textColor)
                )
                Text(
                    text = 0.toString(),
                    fontSize = 12.sp,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 5.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                var padding by remember { mutableIntStateOf(0) }
                IconButton(
                    {},
                    Modifier.fillMaxWidth()
                        .onGloballyPositioned {
                            padding = it.size.width/4
                        }
                        .padding(start = padding.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.baseline_navigate_next_24),
                        contentDescription = null,
                        tint = textColor
                    )
                }
            }

    }
}
