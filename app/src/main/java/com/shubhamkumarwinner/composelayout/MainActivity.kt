package com.shubhamkumarwinner.composelayout

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Doorbell
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.glide.rememberGlidePainter
import com.shubhamkumarwinner.composelayout.ui.theme.ComposeLayoutTheme
import kotlinx.coroutines.launch
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeLayoutTheme {
                // A surface container using the 'background' color from the theme
                /*Surface(color = MaterialTheme.colors.background) {
                    PhotographerCard()
                }*/
                LayoutCodelab()

//                SimpleList()

//                LazyList()

//                ImageList()
            }
        }
    }
}

//Custom complex layout

@Composable
fun StaggeredGrid(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
){
    Layout(content = content, modifier = modifier){measurables, constraints ->
        val rowWidths = IntArray(rows){0}
        val rowHeights = IntArray(rows){0}

        val placeables = measurables.mapIndexed{index, measurable ->
            val placeable = measurable.measure(constraints)

            val row = index % rows
            rowWidths[row] += placeable.width
            rowHeights[row] = max(rowHeights[row], placeable.height)

            placeable
        }

        val width = rowWidths.maxOrNull()?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))
            ?: constraints.minWidth

        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i-1] + rowHeights[i-1]
        }

        layout(width, height) {
            // x cord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

//Custom complex layout
@Composable
fun Chip(modifier: Modifier = Modifier, text: String){
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier
                .size(16.dp, 16.dp)
                .background(color = MaterialTheme.colors.secondary))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text)

        }

    }
}

@Composable
fun SimpleList(){
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        repeat(100){
            Text(text = "Item #$it")
        }
    }
}

@Composable
fun LazyList(){
    val scrollState = rememberLazyListState()
    LazyColumn(state = scrollState) {
        items(100){
            Text(text = "Item #$it")

        }
    }
}

@Composable
fun ImageList(){
    val listSize = 100
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    Column {
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    // 0 is the first item index
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Text("Scroll to the top")
            }

            Button(onClick = {
                coroutineScope.launch {
                    // listSize - 1 is the last index of the list
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }) {
                Text("Scroll to the end")
            }
        }
        LazyColumn(state = scrollState) {
            items(listSize){
                ImageListItem(it)

            }
        }
    }

}

@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Image(
            painter = rememberCoilPainter("https://developer.android.com/images/brand/Android_Robot.png"),
            contentDescription = "Android Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun LayoutCodelab(){
    Scaffold(topBar = {
        TopAppBar(
            title = {
                Text(
                    text = "LayoutsCodelab"
                )
            },
            actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Doorbell, contentDescription = null)
                }
            }
        )
    }) { innerPadding ->
        BodyContent(
            Modifier
                .padding(innerPadding)
                .padding(8.dp))
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier){
    val topics = listOf(
        "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
        "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
        "Religion", "Social sciences", "Technology", "TV", "Writing"
    )

    Row(modifier = modifier.horizontalScroll(rememberScrollState())) {
        StaggeredGrid{
            for (topic in topics) {
                Chip(modifier = Modifier.padding(8.dp), text = topic)
            }
        }
    }
}

@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    content: @Composable ()->Unit
){
    Layout(content = content, modifier = modifier){ measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        var yPosition = 0
        layout(constraints.maxWidth, constraints.maxHeight){
            placeables.forEach{placeable ->
                placeable.placeRelative(0, y = yPosition)

                yPosition += placeable.height
            }
        }
    }
}

fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.layout{measurable, constraints ->
    val placeable = measurable.measure(constraints)

    check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
    Log.d("checkValue", "${check(placeable[FirstBaseline] != AlignmentLine.Unspecified)}")
    val firstBaseline = placeable[FirstBaseline]

    val placeableY = firstBaselineToTop.roundToPx() - firstBaseline

    val height = placeable.height + placeableY

    layout(placeable.width, height = height){
        placeable.placeRelative(0, placeableY)
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun PhotographerCard(modifier: Modifier = Modifier){
    Row(
        //order matters if padding is before clickable means
        // first padding is applied and then clickable
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.surface)
            .clickable(onClick = { /* Ignoring onClick */ })
            .padding(16.dp)
    ) {
        Surface(
            modifier = modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        ) {

        }
        Column(
            modifier = modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(text = "Shubham Kumar", fontWeight = FontWeight.Bold)
            // LocalContentAlpha is defining opacity level of its children
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text(text = "3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }
}

//@Preview
@Composable
fun PhotographerCardPreview() {
    ComposeLayoutTheme {
        PhotographerCard()
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeLayoutTheme {
        Chip(text = "Hi Shubham")
    }
}