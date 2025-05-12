package com.emerbv.ecommadmin.features.dashboard.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun KpiCardsRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        KpiCard(
            title = "Total Products",
            value = "27",
            icon = Icons.Default.Inventory,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Total Categories",
            value = "15",
            icon = Icons.Default.Category,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Total Inventory",
            value = "377 units",
            icon = Icons.Default.ViewList,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Pre-Orders",
            value = "2",
            icon = Icons.Default.ShoppingCart,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun KpiCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = MaterialTheme.colors.primary,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(value, style = MaterialTheme.typography.h6)
                Text(title, style = MaterialTheme.typography.body2, color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
            }
        }
    }
} 