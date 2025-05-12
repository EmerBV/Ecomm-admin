package com.emerbv.ecommadmin.features.products.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProductListItem(
    product: ProductDto,
    onClick: (ProductDto) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = 2.dp,
        onClick = { onClick(product) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto o placeholder
            val imageDownloadUrl = product.images?.firstOrNull()?.downloadUrl

            Surface(
                modifier = Modifier
                    .size(64.dp),
                color = MaterialTheme.colors.primary.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = product.name.take(1).uppercase(),
                        style = MaterialTheme.typography.h5,
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.subtitle1,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.brand,
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = product.category.name,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.primaryVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Precio y stock
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = "Precio",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colors.primary
                    )

                    Text(
                        text = currencyFormatter.format(product.price),
                        style = MaterialTheme.typography.body2,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "Inventario",
                        modifier = Modifier.size(16.dp),
                        tint = if (product.inventory > 0) MaterialTheme.colors.primary else Color.Red
                    )

                    Text(
                        text = "${product.inventory} unidades",
                        style = MaterialTheme.typography.caption,
                        color = if (product.inventory > 0)
                            MaterialTheme.colors.onSurface
                        else
                            Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                val statusColor = when (product.status) {
                    "IN_STOCK" -> Color.Green
                    "OUT_OF_STOCK" -> Color.Red
                    "LOW_STOCK" -> Color(0xFFFFA000) // Amber
                    else -> MaterialTheme.colors.onSurface
                }

                Text(
                    text = when (product.status) {
                        "IN_STOCK" -> "En stock"
                        "OUT_OF_STOCK" -> "Agotado"
                        "LOW_STOCK" -> "Bajo stock"
                        else -> product.status
                    },
                    style = MaterialTheme.typography.caption,
                    color = statusColor
                )
            }
        }
    }
}

@Composable
fun EmptyProductList(
    message: String = "No se encontraron productos",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .alpha(0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.body1,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}