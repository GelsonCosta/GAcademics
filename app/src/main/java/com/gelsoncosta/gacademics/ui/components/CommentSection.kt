package com.gelsoncosta.gacademics.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gelsoncosta.gacademics.data.models.Comment
import com.gelsoncosta.gacademics.ui.viewmodel.CommentViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val DarkBackground = Color(0xFF1A1A1A)
private val DarkSurface = Color(0xFF2D2D2D)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFB0B0B0)
private val AccentColor = Color(0xFF6B8AFE)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentSection(
    materialId: Int,
    viewModel: CommentViewModel,
    currentUserId : Int,
    modifier: Modifier = Modifier
) {
    var newCommentText by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(materialId) {
        viewModel.fetchComments(materialId)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkBackground)
            .padding(horizontal = 20.dp)
    ) {
        Text(
            text = "Comments (${comments.size})",
            style = MaterialTheme.typography.titleLarge,
            color = TextWhite,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        errorMessage?.let { error ->
            Text(
                text = error,
                color = Color(0xFFFF6B6B),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        CommentInput(
            value = newCommentText,
            onValueChange = { newCommentText = it },
            onSubmit = {
                if (newCommentText.isNotBlank()) {
                    viewModel.addComment(materialId, newCommentText)
                    newCommentText = ""
                }
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading && comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentColor)
            }
        } else if (comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No comments yet. Be the first to comment!",
                    color = TextGray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    items = comments,
                    key = { it.id }
                ) { comment ->
                    CommentItem(
                        comment = comment,
                        currentUserId = currentUserId,
                        onEdit = { newContent ->
                            viewModel.editComment(comment.id, newContent)
                        },
                        onDelete = {
                            viewModel.deleteComment(comment.id)
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun CommentInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentColor,
                    unfocusedBorderColor = TextGray,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                placeholder = {
                    Text("Write a comment...", color = TextGray)
                },
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onSubmit,
                enabled = !isLoading && value.isNotBlank(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (!isLoading && value.isNotBlank()) AccentColor else TextGray.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = TextWhite
                    )
                } else {
                    Icon(
                        Icons.AutoMirrored.Default.Send,
                        contentDescription = "Send",
                        tint = if (value.isBlank()) TextGray else TextWhite
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CommentItem(
    comment: Comment,
    currentUserId: Int,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedContent by remember { mutableStateOf(comment.content) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
                    )
                    Text(
                        text = formatDate(comment.createdAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }

                if (comment.updatedAt != null) {
                    Text(
                        text = "(edited)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isEditing) {
                OutlinedTextField(
                    value = editedContent,
                    onValueChange = { editedContent = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentColor,
                        unfocusedBorderColor = TextGray,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            isEditing = false
                            editedContent = comment.content
                        }
                    ) {
                        Text("Cancel", color = TextGray)
                    }
                    TextButton(
                        onClick = {
                            if (editedContent.isNotBlank() && editedContent != comment.content) {
                                onEdit(editedContent)
                            }
                            isEditing = false
                        }
                    ) {
                        Text("Save", color = AccentColor)
                    }
                }
            } else {
                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextWhite
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    if(comment.userId == currentUserId){
                        IconButton(
                            onClick = { isEditing = true },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = TextGray
                            )
                        }
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFF6B6B)
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateString: String): String {
    return try {
        val instant = Instant.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm")
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        dateString
    }
}