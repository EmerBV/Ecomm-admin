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
fun PopularProductsList() {
    Card(elevation = 4.dp) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Popular Products", style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
                TextButton(onClick = { /* TODO: Navegar a lista completa */ }) {
                    Text("View all")
                }
            }
            Spacer(Modifier.height(8.dp))
            val products = listOf(
                ProductPopularUi("One Piece Fantasy Studio Yamato Resin Statue", "Fantasy Studio", "$345.00", 55),
                ProductPopularUi("Monkey D. Luffy - Gear 5", "Bandai Spirits", "$39.99", 35),
                ProductPopularUi("One Piece Lx Studio Marshall D. Teach Resin Statue", "Lx Studio", "$399.00", 33),
                ProductPopularUi("Naruto Miss Studio Minato Resin Statue", "Miss Studio", "$100.00", 10),
                ProductPopularUi("Dragon Ball Infinite Studio Majin Vegeta Resin Statue", "Infinite Studio", "$225.00", 7)
            )
            products.forEach {
                PopularProductItem(it)
                Spacer(Modifier.height(8.dp))
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