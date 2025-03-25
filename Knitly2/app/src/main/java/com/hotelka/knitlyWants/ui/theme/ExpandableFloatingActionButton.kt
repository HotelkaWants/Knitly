package com.hotelka.knitlyWants.ui.theme

import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hotelka.knitlyWants.R
@Composable
fun CustomFabTools(
    expandable: Boolean,
    calculatorDecrease: () -> Unit,
    onFabClick: () -> Unit,
    calculatorIncrease: () -> Unit,
    fabIcon: ImageVector,
) {
    var isExpanded by remember { mutableStateOf(false) }
    if (!expandable) {
        isExpanded = false
    }

    val fabSize = 52.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded) 60.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )

    Column {
        Box(
            modifier = Modifier
                .offset(y = (25).dp)
                .size(
                    width = expandedFabWidth,
                    height = (animateDpAsState(
                        if (isExpanded) 180.dp else 0.dp,
                        animationSpec = spring(dampingRatio = 4f)
                    )).value
                )
                .background(
                    white,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(20.dp)

        ) {
            Column {
                    Row(
                        Modifier.clickable {
                            calculatorIncrease()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.calculateIncreases),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp),
                            color = textColor
                        )
                    }

                Row(
                    Modifier.padding(top = 20.dp).clickable{
                        calculatorDecrease
                    },
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    Text(
                        text = stringResource(R.string.calculateDecrease),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        color = textColor
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = {
                onFabClick()
                if (expandable) {
                    isExpanded = !isExpanded
                }
            },
            modifier = Modifier
                .padding(start = 10.dp, bottom = 10.dp)
                .width(expandedFabWidth)
                .height(expandedFabHeight),
            containerColor = basic,
            shape = RoundedCornerShape(
                30.dp
            ),
            elevation = FloatingActionButtonDefaults.elevation(10.dp)

        ) {

            Icon(
                imageVector = fabIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) -60.dp else 0.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    ),
                tint = headers_activeElement
            )

            Text(
                text = stringResource(R.string.calculate),
                softWrap = false,
                modifier = Modifier
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) 10.dp else 40.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    )
                    .alpha(
                        animateFloatAsState(
                            targetValue = if (isExpanded) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = if (isExpanded) 350 else 100,
                                delayMillis = if (isExpanded) 100 else 0,
                                easing = EaseIn
                            )
                        ).value
                    ),
                color = headers_activeElement
            )


        }
    }
}
@Composable
fun CustomFloatingActionButton(
    expandable: Boolean,
    saveProjectEnabled: Boolean,
    saveProject: () -> Unit,
    onFabClick: () -> Unit,
    saveDraft: () -> Unit,
    fabIcon: ImageVector,
) {
    var isExpanded by remember { mutableStateOf(false) }
    if (!expandable) {
        isExpanded = false
    }

    val fabSize = 64.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded) 58.dp else fabSize,
        animationSpec = spring(dampingRatio = 3f)
    )

    Column {
        Box(
            modifier = Modifier
                .offset(y = (25).dp)
                .size(
                    width = expandedFabWidth,
                    height = (animateDpAsState(
                        if (isExpanded) 155.dp else 0.dp,
                        animationSpec = spring(dampingRatio = 4f)
                    )).value
                )
                .background(
                    white,
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(20.dp)

        ) {
            Column {
                if (saveProjectEnabled) {
                    Row(
                        Modifier.clickable {
                            saveProject()
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = textColor
                        )
                        Text(
                            text = stringResource(R.string.saveDone),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp),
                            color = textColor
                        )
                    }
                }
                Row(
                    Modifier.padding(top = 20.dp).clickable{
                        saveDraft()
                    },
                    verticalAlignment = Alignment.CenterVertically
                )  {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = textColor
                    )
                    Text(
                        text = stringResource(R.string.saveDraft),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp),
                        color = textColor
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = {
                onFabClick()
                if (expandable) {
                    isExpanded = !isExpanded
                }
            },
            modifier = Modifier
                .width(expandedFabWidth)
                .height(expandedFabHeight),
            containerColor = accent_secondary,
            shape = RoundedCornerShape(
                topStart = 30.dp,
                topEnd = 30.dp,
                bottomStart = 30.dp
            ),
            elevation = FloatingActionButtonDefaults.elevation(10.dp)

        ) {

            Icon(
                imageVector = fabIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) -70.dp else 0.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    ),
                tint = white
            )

            Text(
                text = stringResource(R.string.save),
                softWrap = false,
                modifier = Modifier
                    .offset(
                        x = animateDpAsState(
                            if (isExpanded) 10.dp else 50.dp,
                            animationSpec = spring(dampingRatio = 3f)
                        ).value
                    )
                    .alpha(
                        animateFloatAsState(
                            targetValue = if (isExpanded) 1f else 0f,
                            animationSpec = tween(
                                durationMillis = if (isExpanded) 350 else 100,
                                delayMillis = if (isExpanded) 100 else 0,
                                easing = EaseIn
                            )
                        ).value
                    ),
                color = white
            )


        }
    }
}