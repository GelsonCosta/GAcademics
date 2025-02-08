package com.gelsoncosta.gacademics.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.gelsoncosta.gacademics.data.models.PdfMaterial
import com.gelsoncosta.gacademics.ui.theme.primary
import com.gelsoncosta.gacademics.ui.theme.text

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemPdfMaterial(
    pdfMaterial: PdfMaterial,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable(onClick = onItemClick)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background)
            .clip(RoundedCornerShape(20.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSurface),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                imageUrl = pdfMaterial.cover?:"",
                contentDescription = null,
                modifier = Modifier
                    .size(98.dp, 145.dp)
                    .padding(16.dp),
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = pdfMaterial.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = text.copy(0.7F)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pdfMaterial.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = text
                )

                Spacer(modifier = Modifier.height(8.dp))


                Spacer(modifier = Modifier.height(12.dp))

                FlowRow {
                    pdfMaterial.tags.split(" ").forEach { tag ->
                        AssistChip(
                            onClick = { },
                            label = { Text(tag) },
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun ChipView(category: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(primary.copy(.10F))
            .padding(start = 12.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodySmall,
            color = primary
        )
    }
}