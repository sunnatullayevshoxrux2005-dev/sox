package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SlotStatus
import com.example.data.model.formatUZS
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandPurple
import com.example.ui.theme.BrandRed
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.viewmodel.ParkWayUiState

@Composable
fun AdminPanelContent(
    state: ParkWayUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("admin_panel_root"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin warning / status banner (Bron qilish funksiyasi o'chirilgan)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Navy900.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, BrandAmber.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BrandAmber.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, BrandAmber.copy(alpha = 0.45f)),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Admin rejimi",
                                tint = BrandAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Administrator Nazorat va Tushumlar Paneli",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Faqat monitoring va moliyaviy tahlil rejimi (Admin panelda joy bron qilish o‘chirilgan)",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = BrandEmerald.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "● Tizim onlayn • 14 Viloyat ulangan",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF6EE7B7),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        // 4 Revenue KPI Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                title = "Umumiy tushum (Jami)",
                value = formatUZS(state.totalRevenue),
                sub = "↑ +14.8% o‘tgan haftaga nisbatan",
                valueColor = BrandEmerald,
                borderColor = BrandEmerald.copy(alpha = 0.38f),
                modifier = Modifier.weight(1f),
                testTag = "admin_total_revenue"
            )
            KpiCard(
                title = "Bugungi tushum",
                value = formatUZS(state.todayRevenue),
                sub = "Tranzaksiyalar: ${state.transactions.size} ta",
                valueColor = BrandCyan,
                borderColor = BrandBlue.copy(alpha = 0.38f),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            KpiCard(
                title = "Obunalardan tushum (100/200/300h)",
                value = formatUZS(state.subRevenue),
                sub = "Faol obunachilar: ${state.activeSubscribers} nafar",
                valueColor = BrandAmber,
                borderColor = BrandAmber.copy(alpha = 0.38f),
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                title = "Soatlik bronlardan tushum",
                value = formatUZS(state.bookingRevenue),
                sub = "O‘rtacha chek: 18,500 so‘m",
                valueColor = Color(0xFFD8B4FE),
                borderColor = BrandPurple.copy(alpha = 0.38f),
                modifier = Modifier.weight(1f)
            )
        }

        // Viloyatlar kesimida tushum tahlili
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Navy900.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Slate700.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column {
                    Text(
                        text = "Viloyatlar kesimida tushum tahlili",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Eng yuqori daromad keltirayotgan hududlar",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate400
                    )
                }

                val sortedRegions = state.regionRevenues.entries.sortedByDescending { it.value }
                val maxRevenue = sortedRegions.firstOrNull()?.value?.coerceAtLeast(1L) ?: 1L

                sortedRegions.forEach { (regName, amount) ->
                    val ratio = (amount.toFloat() / maxRevenue.toFloat()).coerceIn(0.05f, 1f)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = regName,
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate300
                            )
                            Text(
                                text = formatUZS(amount),
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandEmerald
                            )
                        }
                        LinearProgressIndicator(
                            progress = { ratio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = BrandCyan,
                            trackColor = Navy950
                        )
                    }
                }
            }
        }

        // 1–5 qavatlar real-vaqt bandligi (Faqat kuzatuv)
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Navy900.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Slate700.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "1–5 qavatlar real-vaqt bandligi",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Sensorlardan kelayotgan umumiy statistika",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Slate800
                    ) {
                        Text(
                            text = "Faqat kuzatuv",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate300,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                val regionFloors = state.floorsData[state.selectedRegionId].orEmpty()
                (1..5).forEach { floor ->
                    val slots = regionFloors[floor].orEmpty()
                    val free = slots.count { it.status == SlotStatus.FREE }
                    val occ = slots.count { it.status == SlotStatus.OCCUPIED }
                    val res = slots.count { it.status == SlotStatus.RESERVED }
                    val total = slots.size.coerceAtLeast(1)

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Navy950.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, Slate800)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${floor}-qavat (${state.currentRegion.name})",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("Bo‘sh:$free", style = MaterialTheme.typography.labelSmall, color = BrandEmerald)
                                    Text("Band:$occ", style = MaterialTheme.typography.labelSmall, color = BrandRed)
                                    Text("Bron:$res", style = MaterialTheme.typography.labelSmall, color = BrandBlue)
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(Slate800)
                            ) {
                                if (occ > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(occ.toFloat() / total)
                                            .fillMaxHeight()
                                            .background(BrandRed)
                                    )
                                }
                                if (res > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(res.toFloat() / total)
                                            .fillMaxHeight()
                                            .background(BrandBlue)
                                    )
                                }
                                if (free > 0) {
                                    Box(
                                        modifier = Modifier
                                            .weight(free.toFloat() / total)
                                            .fillMaxHeight()
                                            .background(BrandGreen)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Mijozlardan kelayotgan oxirgi tushumlar
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Navy900.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Slate700.copy(alpha = 0.8f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mijozlardan kelayotgan oxirgi tushumlar",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Mijoz panelida qilingan har bir bron va obuna xaridi jonli aks etadi",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandCyan.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "Jonli moliya",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandCyan,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = Slate800)

                state.transactions.forEach { tx ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Navy950.copy(alpha = 0.7f),
                        border = BorderStroke(1.dp, Slate800)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = tx.time,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Slate400
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(5.dp),
                                        color = Slate800,
                                        border = BorderStroke(1.dp, Slate700)
                                    ) {
                                        Text(
                                            text = tx.plate,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = tx.region,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Slate300,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = tx.service,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BrandCyan
                                )
                            }
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = if (tx.amount > 0) "+${formatUZS(tx.amount)}" else "Obuna balansi",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (tx.amount > 0) BrandEmerald else BrandAmber
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    sub: String,
    valueColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    Surface(
        modifier = modifier.let { if (testTag != null) it.testTag(testTag) else it },
        shape = RoundedCornerShape(16.dp),
        color = Navy900,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Navy800.copy(alpha = 0.7f), Navy900)))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Slate400,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = valueColor
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Slate300,
                maxLines = 2
            )
        }
    }
}
