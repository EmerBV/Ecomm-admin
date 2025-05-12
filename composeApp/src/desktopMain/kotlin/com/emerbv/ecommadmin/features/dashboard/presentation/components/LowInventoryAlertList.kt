package com.emerbv.ecommadmin.features.dashboard.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LowInventoryAlertList() {
    Card(elevation = 4.dp) {
        Column(Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Low Inventory Alert", style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold))
                TextButton(onClick = { /* TODO: Navegar a lista completa */ }) {
                    Text("View all")
                }
            }
            Spacer(Modifier.height(8.dp))
            val alerts = listOf(
                LowInventoryUi("Spirited Away Bard Studio x UP Studio Gallery Resin Statue", "Bard Studio x UP Studio", 3),
                LowInventoryUi("Chainsaw Man Modomodo Studio Makima Resin Statue", "Modomodo Studio", 3),
                LowInventoryUi("Naruto Xiu Studio Minato Namikaze Resin Statue", "Xiu Studio", 5),
                LowInventoryUi("Bleach HZ Studio Ichigo Kurosaki Hollow Resin Statue", "HZ Studio", 3),
                LowInventoryUi("Demon Slayer KNY Studio Nezuko Kamado Resin Statue", "KNY Studio", 3)
            )
            alerts.forEach {
                LowInventoryAlertItem(it)
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

data class LowInventoryUi(val name: String, val studio: String, val units: Int)

@Composable
fun LowInventoryAlertItem(alert: LowInventoryUi) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colors.secondary, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(alert.name, style = MaterialTheme.typography.body1)
            Text(alert.studio, style = MaterialTheme.typography.caption, color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f))
        }
        Text(
            "${alert.units} units",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.secondary
        )
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = { /* TODO: Acción de actualizar */ }) {
            Text("Update")
        }
    }
} 