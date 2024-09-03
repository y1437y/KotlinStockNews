package com.example.jetpackpractice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import coil.compose.AsyncImage
import com.example.jetpackpractice.viewModel.NewsModel
import com.example.jetpackpractice.ui.theme.JetpackPracticeTheme
import com.example.jetpackpractice.viewModel.NewsData

class MainActivity : ComponentActivity() {
    private val newsModel : NewsModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Fetch Data
        newsModel.getNewsBySymbol("AAPL")

        setContent {
            JetpackPracticeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SearchBar(
                        newsModel = newsModel,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    newsModel : NewsModel
) {
    //Observe the news data
    val news = newsModel.stockNews.value

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }

    Column(modifier = modifier.padding(16.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
            },
            trailingIcon = {
                IconButton(onClick = {
                    newsModel.getNewsBySymbol(searchQuery)
                    searchQuery = ""
                    keyboardController?.hide()
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            placeholder = {
                Text(text = "Search")
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(news.size) { index ->
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                ) {
                    Button(
                        onClick = {
                          val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news[index].url))
                          context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor =  MaterialTheme.colorScheme.onSurface
                        ),
                        elevation = null

                    ) {
                        Text(text = news[index].title)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    AsyncImage(
                        model = news[index].imageUrl,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val mockNewsModel = object : NewsModel() {
        init {
            // Providing fake data
            _stockNews.value += NewsData("", "", "")
        }
    }

    JetpackPracticeTheme {
        SearchBar(newsModel = mockNewsModel)
    }
}

