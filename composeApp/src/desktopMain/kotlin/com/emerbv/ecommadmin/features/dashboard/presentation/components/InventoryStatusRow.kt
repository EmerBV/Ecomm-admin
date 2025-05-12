package com.emerbv.ecommadmin.features.dashboard.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun InventoryStatusRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        InventoryStatusCard(
            title = "In Stock",
            value = "25",
            subtitle = "products available",
            color = Color(0xFFD1FAE5),
            modifier = Modifier.weight(1f)
        )
        InventoryStatusCard(
            title = "Out of Stock",
            value = "2",
            subtitle = "products unavailable",
            color = Color(0xFFFEE2E2),
            modifier = Modifier.weight(1f)
        )
        InventoryStatusCard(
            title = "Pre-Order",
            value = "2",
            subtitle = "products on pre-order",
            color = Color(0xFFEDE9FE),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun InventoryStatusCard(title: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .background(color)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.h6)
            Text(title, style = MaterialTheme.typography.body2)
            Text(subtitle, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        }
    }
} 