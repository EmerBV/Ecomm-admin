package com.emerbv.ecommadmin.features.products.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emerbv.ecommadmin.core.ui.theme.EcommAdminTheme
import com.emerbv.ecommadmin.features.products.data.model.ProductDto
import com.emerbv.ecommadmin.features.products.data.model.VariantDto
import java.text.NumberFormat
import java.util.*

@Composable
fun ProductDetailScreen(
    product: ProductDto,
    onBackClick: () -> Unit,
    onEditClick: (ProductDto) -> Unit
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault())
    val scrollState = rememberScrollState()

    EcommAdminTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(product.name) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                        IconButton(onClick = { onEditClick(product) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header con información principal
                ProductHeader(product, currencyFormatter)

                Divider()

                // Descripción
                ProductSection(title = "Descripción") {
                    Text(
                        text = product.description.ifEmpty { "Sin descripción disponible" },
                        style = MaterialTheme.typography.body1
                    )
                }

                Divider()

                // Estado e inventario
                ProductSection(title = "Estado e Inventario") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusItem(
                            icon = Icons.Default.Inventory,
                            label = "Inventario",
                            value = "${product.inventory} unidades",
                            color = if (product.inventory > 0) Color.Unspecified else Color.Red
                        )

                        val statusColor = when (product.status) {
                            "IN_STOCK" -> Color.Green
                            "OUT_OF_STOCK" -> Color.Red
                            "LOW_STOCK" -> Color(0xFFFFA000) // Amber
                            else -> Color.Unspecified
                        }

                        val statusText = when (product.status) {
                            "IN_STOCK" -> "En stock"
                            "OUT_OF_STOCK" -> "Agotado"
                            "LOW_STOCK" -> "Bajo stock"
                            else -> product.status
                        }

                        StatusItem(
                            icon = Icons.Default.Info,
                            label = "Estado",
                            value = statusText,
                            color = statusColor
                        )

                        StatusItem(
                            icon = Icons.Default.Loyalty,
                            label = "Pre-orden",
                            value = if (product.preOrder) "Sí" else "No"
                        )
                    }
                }

                Divider()

                // Estadísticas
                ProductSection(title = "Estadísticas") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatusItem(
                            icon = Icons.Default.ShoppingCart,
                            label = "Ventas",
                            value = "${product.salesCount} unidades"
                        )

                        StatusItem(
                            icon = Icons.Default.Favorite,
                            label = "Lista de deseos",
                            value = "${product.wishCount} usuarios"
                        )

                        StatusItem(
                            icon = Icons.Default.LocalOffer,
                            label = "Descuento",
                            value = "${product.discountPercentage}%"
                        )
                    }
                }

                // Variantes (si hay)
                if (!product.variants.isNullOrEmpty()) {
                    Divider()

                    ProductSection(title = "Variantes") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            product.variants.forEach { variant ->
                                VariantItem(
                                    variant = variant,
                                    currencyFormatter = currencyFormatter
                                )
                            }
                        }
                    }
                }

                // Imágenes (si hay)
                if (!product.images.isNullOrEmpty()) {
                    Divider()

                    ProductSection(title = "Imágenes") {
                        if (product.images != null) {
                            Text("Este producto tiene ${product.images.size} imagen(es)")

                            // Detalles de las imágenes
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                product.images.forEach { image ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = "Imagen",
                                            tint = MaterialTheme.colors.primary,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Column {
                                            Text(
                                                text = image.fileName,
                                                style = MaterialTheme.typography.body2
                                            )

                                            Text(
                                                text = image.downloadUrl ?: "Sin URL",
                                                style = MaterialTheme.typography.caption,
                                                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            Text("Este producto no tiene imágenes")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductHeader(
    product: ProductDto,
    currencyFormatter: NumberFormat
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen principal o placeholder
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = product.name.take(1).uppercase(),
                style = MaterialTheme.typography.h4,
                color = MaterialTheme.colors.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = product.name,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = product.brand,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Categoría: ${product.category.name}",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.primaryVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = currencyFormatter.format(product.price),
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.primary
        )

        if (product.discountPercentage > 0) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${product.discountPercentage}% de descuento",
                style = MaterialTheme.typography.body2,
                color = Color.Green
            )
        }
    }
}

@Composable
private fun ProductSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )

        content()
    }
}

@Composable
private fun StatusItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color = Color.Unspecified
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colors.primary
        )

        Text(
            text = label,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.body2,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun VariantItem(
    variant: VariantDto,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = variant.name,
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = currencyFormatter.format(variant.price),
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.primary
            )
        }
    }
}