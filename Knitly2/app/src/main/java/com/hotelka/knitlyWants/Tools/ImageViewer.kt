package com.hotelka.knitlyWants.Tools

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageViewer(
    imageUrls: List<String?>,
    modifier: Modifier = Modifier,
    startIndex: Int = 0,
) {
    val pagerState = rememberPagerState(pageCount = { imageUrls.size }, initialPage = startIndex)
    val scope = rememberCoroutineScope()
    HorizontalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) { page ->
        var containerSize by remember { mutableStateOf(IntSize.Zero) }
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        LaunchedEffect(page) {
            scale = 1f
            offset = Offset.Zero
        }

        val maxOffsetX by remember {
            derivedStateOf {
                if (scale <= 1f || containerSize.width == 0) 0f
                else (scale - 1) * (containerSize.width / 2f)
            }
        }

        val maxOffsetY by remember {
            derivedStateOf {
                if (scale <= 1f || containerSize.height == 0) 0f
                else (scale - 1) * (containerSize.height / 2f)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .onSizeChanged { containerSize = it }
                .pointerInput(Unit) {
                    detectTransformGestures(
                        panZoomLock = scale == 1f
                    ) { _, pan, zoom, _ ->
                        Log.d("Zoom",pan.x.toFloat().toString())
                        val newScale = (scale * zoom).coerceIn(1f, 5f)

                        val newOffset = offset + pan
                        offset = Offset(
                            x = newOffset.x.coerceIn(-maxOffsetX, maxOffsetX),
                            y = newOffset.y.coerceIn(-maxOffsetY, maxOffsetY)
                        )

                        scale = newScale
                        if (scale == 1f && zoom == 1f){
                            if (pan.x > 50f){
                                scope.launch{
                                    pagerState.animateScrollToPage(page - 1)
                                }
                            } else if (pan.x < -50f){
                                scope.launch{
                                    pagerState.animateScrollToPage(page + 1)
                                }
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = {
                            scale = if (scale > 1f) 1f else 2f
                            offset = Offset.Zero
                        }
                    )
                }
        ) {
            AsyncImage(
                model = imageUrls[page],
                contentDescription = "Image ${page + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offset.x
                        translationY = offset.y
                    }
            )
        }
    }
}