package com.emerbv.ecommadmin.features.dashboard.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PopularProductsList(products: List<ProductPopularUi>) {
    Card(elevation = 4.dp) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Popular Products", style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
                TextButton(onClick = { /* TODO: Navigate to full list */ }) {
                    Text("View all")
                }
            }
            Spacer(Modifier.height(8.dp))

            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No popular products found",
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                products.forEach {
                    PopularProductItem(it)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

data class ProductPopularUi(val name: String, val studio: String, val price: String, val sold: Int)

@Composable
fun PopularProductItem(product: ProductPopularUi) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colors.primary, modifier = Modifier.size(32.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(product.name, style = MaterialTheme.typography.body1)
            Text(product.studio, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(product.price, style = MaterialTheme.typography.body2)
            Text("Sold: ${product.sold}", style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        }
    }
} 