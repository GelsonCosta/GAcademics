package com.gelsoncosta.gacademics.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gelsoncosta.gacademics.data.models.Comment

@Composable
fun CommentSection(
    comments: List<Comment>,
    onAddComment: (String) -> Unit
) {
    var newComment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Comments",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(comments) { comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = comment.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = comment.createdAt,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            TextField(
                value = newComment,
                onValueChange = { newComment = it },
                placeholder = { Text("Add a comment") },
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    if (newComment.isNotBlank()) {
                        onAddComment(newComment)
                        newComment = ""
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Post")
            }
        }
    }
}