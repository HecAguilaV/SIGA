package com.example.sigaapp

import com.example.sigaapp.ui.viewmodel.CardSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sigaapp.ui.theme.*

enum class TileSize {
    SMALL,   // 1x1
    MEDIUM,  // 2x1 (ancho doble)
    LARGE    // 2x2 (cuadrado grande)
}

// Import moved to top

@Composable
fun LiveMetricTile(
    title: String,
    value: String,
    trend: String? = null,
    trendIcon: ImageVector? = null,
    isTrendPositive: Boolean = true,
    icon: ImageVector,
    iconColor: Color = PrimaryDark,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp) // V2 standard height for live tiles
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Icon + MenuDots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = iconColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                    }
                }
            }
            
            // Content
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextPrimary
                )
                if (trend != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (trendIcon != null) {
                            Icon(
                                trendIcon, 
                                contentDescription = null, 
                                tint = if (isTrendPositive) EmeraldOps else AlertRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = trend,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isTrendPositive) EmeraldOps else AlertRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardTile(
    title: String,
    icon: ImageVector,
    color: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    size: TileSize = TileSize.SMALL,
    cardSizePreference: CardSize = CardSize.MEDIUM,
    modifier: Modifier = Modifier
) {
    // V2 Style: White background, Colored Icon/Text
    val height = 100.dp // Standard small tile height
    
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight // Always white surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .shadow(
                elevation = if (enabled) 4.dp else 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (enabled) color else DisabledGray,
                modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
            )
            Text(
                text = title,
                color = if (enabled) TextPrimary else DisabledGray,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 1
            )
        }
    }
}
