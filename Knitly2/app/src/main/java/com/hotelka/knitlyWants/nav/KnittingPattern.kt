package com.hotelka.knitlyWants.nav

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.hotelka.knitlyWants.Data.CellData
import com.hotelka.knitlyWants.Data.PatternData
import com.hotelka.knitlyWants.FirebaseUtils.FirebaseDB
import com.hotelka.knitlyWants.R
import com.hotelka.knitlyWants.Supbase.getData
import com.hotelka.knitlyWants.Supbase.uploadFile
import com.hotelka.knitlyWants.ui.theme.ColorPicker
import com.hotelka.knitlyWants.ui.theme.LoadingAnimation
import com.hotelka.knitlyWants.ui.theme.basic
import com.hotelka.knitlyWants.ui.theme.headers_activeElement
import com.hotelka.knitlyWants.ui.theme.textColor
import com.hotelka.knitlyWants.ui.theme.textFieldColor
import com.hotelka.knitlyWants.ui.theme.white
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

@Preview
@Composable
fun KnittingPatternConstructor(onSave: (String) -> Unit = {}) {
    var rows by remember { mutableIntStateOf(10) }
    var columns by remember { mutableIntStateOf(10) }
    var selectedStitchType by remember { mutableStateOf(StitchType.None) }
    var selectedColor by remember { mutableStateOf(Color.White) }
    val scope = rememberCoroutineScope()
    var gridState by remember(rows, columns) {
        mutableStateOf(Array(rows * columns) { CellData() })
    }

    var loading by remember { mutableStateOf(false) }
    Surface(color = basic) {
        if (loading) {
            LoadingAnimation()
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item {
                PatternControls(
                    rows = rows,
                    columns = columns,
                    selectedStitchType = selectedStitchType,
                    selectedColor = selectedColor,
                    onRowsChange = { rows = it },
                    onColumnsChange = { columns = it },
                    onStitchTypeChange = { selectedStitchType = it },
                    onColorChange = { selectedColor = it },
                    onGenerate = {
                        gridState = Array(rows * columns) { CellData() }
                    },
                    onSave = {
                        loading = true
                        scope.launch {
                            savePatternToJSON(
                                rows = rows, columns = columns, gridState = gridState
                            ) {
                                loading = false
                                onSave(it)
                            }
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item {
                PatternGrid(
                    columns = columns,
                    gridState = gridState,
                    onCellClick = { index ->
                        val newState = gridState.copyOf().apply {
                            this[index] = CellData(selectedStitchType, selectedColor)
                        }
                        gridState = newState
                    }
                )
            }
        }
    }
}

@Composable
fun PatternControls(
    rows: Int,
    columns: Int,
    selectedStitchType: StitchType,
    selectedColor: Color,
    onRowsChange: (Int) -> Unit,
    onColumnsChange: (Int) -> Unit,
    onStitchTypeChange: (StitchType) -> Unit,
    onColorChange: (Color) -> Unit,
    onGenerate: () -> Unit,
    onSave: () -> Unit,
) {

    Column {
        Row {
            NumberInput(stringResource(R.string.rows), rows, onRowsChange)
            Spacer(modifier = Modifier.width(16.dp))
            NumberInput(stringResource(R.string.loopsNumber), columns, onColumnsChange)
        }
        Spacer(modifier = Modifier.height(16.dp))

        ColorPicker(selectedColor) { onColorChange(it) }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(10),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(0.dp, 500.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(StitchType.entries) { stitchType ->
                Image(
                    imageVector = ImageVector.vectorResource(stitchType.symbol),
                    modifier = Modifier
                        .weight(1f)
                        .size(30.dp)
                        .drawBehind {
                            drawRoundRect(
                                color = if (selectedStitchType == stitchType) textColor else Transparent,
                                style = Stroke(
                                    width = 2f
                                ),
                            )
                        }
                        .clickable { onStitchTypeChange(stitchType) }
                        .wrapContentSize(),
                    contentDescription = "StitchType"
                )

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 5.dp),
                onClick = onGenerate,
                colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement)
            ) {
                Text(stringResource(R.string.generatePattern))
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(start = 5.dp),
                onClick = {
                    onSave()
                },
                colors = ButtonDefaults.buttonColors(containerColor = headers_activeElement)
            ) {
                Text(stringResource(R.string.savePattern))
            }
        }
    }
}

suspend fun savePatternToJSON(
    rows: Int,
    columns: Int,
    gridState: Array<CellData>,
    onDone: (String) -> Unit
) {
    val patternData = PatternData(rows = rows, columns = columns, gridState = gridState.toList())
    val data = Gson().toJson(patternData).toByteArray(StandardCharsets.UTF_8)
    val fileName = getData(
        "patterns", uploadFile(
            bucket = "patterns",
            file = data,
            extension = ".json"
        ).toString()
    )
    FirebaseDB.savePattern(fileName) { onDone(it) }
}

@Composable
fun NumberInput(label: String, value: Int, onValueChange: (Int) -> Unit) {

    TextField(
        value = value.toString(),
        onValueChange = { onValueChange(it.toIntOrNull() ?: 0) },
        modifier = Modifier
            .background(textFieldColor)
            .padding(10.dp)
            .width(150.dp)
            .clip(RoundedCornerShape(20.dp)),
        colors = TextFieldDefaults.colors(
            unfocusedTextColor = textColor,
            focusedTextColor = textColor,
            focusedContainerColor = white,
            unfocusedContainerColor = white,
            unfocusedLabelColor = DarkGray,
            focusedLabelColor = DarkGray
        ),
        textStyle = TextStyle(fontSize = 16.sp),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number
        ),
        label = { Text(label) }
    )

}

@Composable
fun PatternGrid(
    columns: Int,
    gridState: Array<CellData>,
    onCellClick: (Int) -> Unit
) {
    val cellSize = 30.dp
    val horizontalScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        HorizontalScrollableGrid(columns, gridState, cellSize, onCellClick, horizontalScrollState)
    }
}

@Composable
private fun HorizontalScrollableGrid(
    columns: Int,
    gridState: Array<CellData>,
    cellSize: Dp,
    onCellClick: (Int) -> Unit,
    scrollState: ScrollState
) {

    Box(
        modifier = Modifier
            .horizontalScroll(scrollState)
    ) {
        Column {
            Row {
                for (i in 0..columns + 1) {
                    Box {
                        Text(
                            modifier = Modifier
                                .size(30.dp)
                                .background(basic),
                            text = if (i != 0 && i != columns + 1) i.toString() else "",
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Row {
                Column {
                    for (i in 1..gridState.size.div(columns)) {
                        Box {
                            Text(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(basic),
                                text = i.toString(),
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center

                            )
                        }
                    }
                }
                LazyVerticalGrid(
                    modifier = Modifier
                        .heightIn(0.dp, 2000.dp)
                        .width((cellSize * columns)),
                    columns = GridCells.Fixed(columns),
                    content = {
                        itemsIndexed(gridState) { index, cell ->
                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(cell.color ?: Color.White)
                                    .border(1.dp, DarkGray)
                                    .clickable { onCellClick(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                cell.stitchType?.symbol?.let {
                                    cell.color?.luminance()?.let { it1 ->
                                        Image(
                                            imageVector = ImageVector.vectorResource(it),
                                            contentDescription = "StitchSymbol",
                                            colorFilter = ColorFilter.tint(
                                                if (it1 < 0.5f) white
                                                else textColor
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
                Column {
                    for (i in 1..gridState.size.div(columns)) {
                        Box {
                            Text(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(basic),
                                text = i.toString(),
                                color = textColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center

                            )
                        }
                    }
                }
            }
            Row {
                for (i in 0..columns + 1) {
                    Box {
                        Text(
                            modifier = Modifier
                                .size(30.dp)
                                .background(basic),
                            text = if (i != 0 && i != columns + 1) i.toString() else "",
                            color = textColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

enum class StitchType(val symbol: Int, val displayName: String) {
    None(R.drawable.empty, "Empty"),
    EdgeStitch(R.drawable.edge_stitch, "Edge Stitch"),
    Knit(R.drawable.knit, "Knit"),
    Purl(R.drawable.purl, "Purl"),
    K2togR(R.drawable.k2tog_r, "K2togR"),
    K2togL(R.drawable.k2tog_l, "K2togL"),
    P2togR(R.drawable.p2tog_r, "P2togR"),
    P2togL(R.drawable.p2tog_l, "P2togL"),
    K3tog(R.drawable.k3tog, "K3tog"),
    P3tog(R.drawable.p3tog, "P3tog"),
    Inc1To3(R.drawable.inc_1_to_3, "Inc-1-to-3"),
    Inc1To4(R.drawable.inc_1_to_4, "Inc-1-to-4"),
    Inc1To5(R.drawable.inc_1_to_5, "Inc-1-to-5"),
    Inc1To6(R.drawable.inc_1_to_6, "Inc-1-to-6"),
    TwistedKSt(R.drawable.twisted_k_st, "Twisted k st"),
    TwistedPSt(R.drawable.twisted_p_st, "Twisted p st"),
    Sl1k(R.drawable.sl1k, "Sl1k"),
    Sl1p(R.drawable.sl1p, "Slip one stitch as if to purl with yarn in back"),
    TwistedK2Tog(R.drawable.twisted_k2_tog, "Twisted k2tog"),
    TwistedP2Tog(R.drawable.twisted_p2_tog, "Twisted p2tog"),
    K1ftb(R.drawable._1kt_b, "1 front crossed loop from the broach"),
    K1ptb(R.drawable.k1ptb, "1 back crossed loop from the broach"),
    Bobble(R.drawable.bobble, "Bobble"),
    YarnOver(R.drawable.yarn_over, "Yarn Over"),
    YarnOver2(R.drawable.yo2, "2 Yarn Over"),
    YarnOver3(R.drawable.yo3, "3 Yarn Over"),
    YarnOver4(R.drawable.yo4, "4 Yarn Over"),
    YarnOver5(R.drawable.yo5, "5 Yarn Over"),
    YarnOver6(R.drawable.yo6, "6 Yarn Over"),
    CentreOnTop(
        R.drawable.centre_on_top,
        "Knit 3 together through the back loops – centered decrease"
    ),
    Drop(R.drawable.drop_stitch, "Drop stitches down to the yarn over"),
    DroppingExtra(R.drawable.slip_1, "Slip one stitch as if to purl, dropping the extra loop"),
    WrapTwice(
        R.drawable.double_over,
        "Knit 1 stitch, wrapping the yarn twice around the needle, then pull both wraps through the loop"
    ),
    KnitWhileDropping(
        R.drawable.knit_when_yo,
        "Knit 1 stitch while dropping one of the yarnovers from the previous row"
    ),
    KnitWhenYO(R.drawable.knit_when_yo, "Knit the stitch together with its yarnover"),
    PurlWhenYO(R.drawable.purl_when_yo, "Purl the stitch and its yarnover together"),
    SlipWhenYOasPurl(
        R.drawable.purl_drop_when_yo,
        "Slip one stitch with its yarnover as if to purl"
    ),
    ElongatedStitch(R.drawable.elongated_loop, "Elongated loop"),
    Braid1(R.drawable.braid1, ""),
    Braid2(R.drawable.braid2, ""),
    Braid3(R.drawable.braid3, ""),
    Braid4(R.drawable.braid4, ""),
    Braid5(R.drawable.braid5, ""),
    Braid6(R.drawable.braid6, ""),
    Braid7(R.drawable.braid7, ""),
    Braid8(R.drawable.braid8, ""),
    Braid9(R.drawable.braid9, ""),
    Braid10(R.drawable.braid10, ""),
    Braid11(R.drawable.braid11, ""),
    Braid12(R.drawable.braid12, ""),
    Braid13(R.drawable.braid13, ""),
    Braid14(R.drawable.braid14, ""),
    Braid15(R.drawable.braid15, ""),
    Braid16(R.drawable.braid16, ""),
    Braid19(R.drawable.braid19, ""),
    Braid20(R.drawable.braid20, ""),
    Braid17(R.drawable.braid17, ""),
    Braid18(R.drawable.braid18, ""),
    Braid21(R.drawable.braid21, ""),
    Braid22(R.drawable.braid22, ""),
    Braid23(R.drawable.braid23, ""),
    Braid24(R.drawable.braid24, ""),
    Braid25(R.drawable.braid25, ""),
    Braid26(R.drawable.braid26, ""),
    Braid27(R.drawable.braid27, ""),
    Braid28(R.drawable.braid28, ""),
    Braid29(R.drawable.braid29, ""),
    Braid30(R.drawable.braid30, ""),
    Braid31(R.drawable.braid31, ""),
    Braid32(R.drawable.braid32, ""),
    Braid33(R.drawable.braid33, ""),
    Braid34(R.drawable.braid34, ""),
    Braid35(R.drawable.braid35, ""),
    Braid36(R.drawable.braid36, ""),
    Braid37(R.drawable.braid37, ""),
    Braid38(R.drawable.braid38, ""),
    Braid39(R.drawable.braid39, ""),
    Braid40(R.drawable.braid40, ""),
}