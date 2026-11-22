package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ParkRegion
import com.example.data.model.ParkingSlot
import com.example.data.model.SlotStatus
import com.example.data.model.UZBEKISTAN_REGIONS
import com.example.data.model.formatUZS
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlue600
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandRed
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.VipNavyTop
import com.example.ui.viewmodel.ParkWayUiState

@Composable
fun ClientPanelContent(
    state: ParkWayUiState,
    onSelectRegion: (String) -> Unit,
    onSelectFloor: (Int) -> Unit,
    onSlotClick: (ParkingSlot) -> Unit,
    onScrollToSubscriptions: () -> Unit,
    onBuySubscription: (String, Int, Long) -> Unit,
    onConfirmBooking: (String, Int, Boolean) -> Unit,
    onCloseBookingModal: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 01 • Hududiy tarif va lokatsiya
        RegionSelectorCard(
            currentRegion = state.currentRegion,
            onSelectRegion = onSelectRegion
        )

        // Mijozning Raqamli Avto Turargoh Passi
        DigitalPassCard(
            plate = state.userPlate,
            subName = state.subscriptionName,
            subHours = state.subscriptionHours,
            lastBooking = state.lastBookingText,
            onScrollToSubscriptions = onScrollToSubscriptions
        )

        // 02 • 1 dan 5 gacha qavatlar va interaktiv joylar xaritasi
        InteractiveParkingFloorCard(
            state = state,
            onSelectFloor = onSelectFloor,
            onSlotClick = onSlotClick
        )

        // 03 • Obuna tariflari (100 PLUS, 200 PRO, 300 VIP + Jonli Radar)
        SubscriptionsSection(
            state = state,
            onBuySubscription = onBuySubscription
        )
    }

    // Bron qilish modali
    state.selectedSlotToBook?.let { slot ->
        BookingDialogModal(
            slot = slot,
            region = state.currentRegion,
            initialPlate = state.userPlate,
            subscriptionHours = state.subscriptionHours,
            onDismiss = onCloseBookingModal,
            onConfirm = onConfirmBooking
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegionSelectorCard(
    currentRegion: ParkRegion,
    onSelectRegion: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Navy900.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.8f)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "01 • HUDUDIY TARIF VA LOKATSIYA",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandBlue
                    )
                    Text(
                        text = "Viloyatni tanlang (narxlar hududlar kesimida)",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandBlue.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.35f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Soatlik narx",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = Slate300
                        )
                        Text(
                            text = "${formatUZS(currentRegion.rate)}/soat",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = JetBrainsMonoFontFamily
                            ),
                            color = BrandCyan
                        )
                    }
                }
            }

            // 14 Region Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = "${currentRegion.name} — ${formatUZS(currentRegion.rate)}/soat",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("O‘zbekiston hududi / viloyati:") },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "Viloyatni tanlash",
                            tint = BrandCyan
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Navy950,
                        unfocusedContainerColor = Navy950,
                        focusedBorderColor = BrandCyan,
                        unfocusedBorderColor = BrandBlue.copy(alpha = 0.45f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                        .fillMaxWidth()
                        .testTag("region_dropdown")
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(Navy900)
                ) {
                    UZBEKISTAN_REGIONS.forEach { reg ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${reg.name} — ${formatUZS(reg.rate)}/soat",
                                    color = if (reg.id == currentRegion.id) BrandCyan else Color.White,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onClick = {
                                onSelectRegion(reg.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Selected Hub Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Navy950.copy(alpha = 0.85f),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tanlangan markaz:",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                        Text(
                            text = currentRegion.hub,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )
                        Text(
                            text = "Obuna bilan 100% bepul kirish",
                            style = MaterialTheme.typography.bodySmall,
                            color = BrandEmerald
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BrandEmerald.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "5 Qavat",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandEmerald,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Quick region pills
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Viloyatlar kesimidagi tezkor narxlar (bosib tanlang):",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(UZBEKISTAN_REGIONS, key = { it.id }) { reg ->
                        val selected = reg.id == currentRegion.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) BrandBlue600.copy(alpha = 0.25f) else Navy950,
                            border = BorderStroke(
                                1.dp,
                                if (selected) BrandCyan else Slate800
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("region_pill_${reg.id}")
                                .clickable { onSelectRegion(reg.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "${reg.name.replace(" viloyati", "")}:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (selected) Color(0xFFA5F3FC) else Slate400
                                )
                                Text(
                                    text = String.format("%.1fk", reg.rate / 1000.0),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DigitalPassCard(
    plate: String,
    subName: String,
    subHours: Int,
    lastBooking: String,
    onScrollToSubscriptions: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Navy900,
        border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.35f)),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(Navy800, Navy950)))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RAQAMLI AVTO TURARGOH PASSI",
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandCyan
                    )
                    Text(
                        text = "Mijoz profili va balans",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = BrandEmerald.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.45f))
                ) {
                    Text(
                        text = "Obuna: Faol",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF6EE7B7),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            HorizontalDivider(color = Slate700.copy(alpha = 0.6f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Navy950.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Slate700.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Davlat raqami", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White,
                            border = BorderStroke(2.dp, Slate400)
                        ) {
                            Text(
                                text = plate,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Navy950,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Navy950.copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, Slate700.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("Faol obuna va soat", style = MaterialTheme.typography.bodySmall, color = Slate400)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${subName.split(" ").first()} • $subHours soat",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = BrandAmber,
                            modifier = Modifier.testTag("client_sub_balance_text")
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = BrandBlue.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Oxirgi bron: $lastBooking",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Obunani yangilash",
                        style = MaterialTheme.typography.labelMedium.copy(
                            textDecoration = TextDecoration.Underline
                        ),
                        color = BrandCyan,
                        modifier = Modifier
                            .clickable(onClick = onScrollToSubscriptions)
                            .padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InteractiveParkingFloorCard(
    state: ParkWayUiState,
    onSelectFloor: (Int) -> Unit,
    onSlotClick: (ParkingSlot) -> Unit
) {
    val regionFloors = state.floorsData[state.selectedRegionId].orEmpty()
    val slots = state.currentFloorSlots
    val freeCount = slots.count { it.status == SlotStatus.FREE }
    val occCount = slots.count { it.status == SlotStatus.OCCUPIED }
    val resCount = slots.count { it.status == SlotStatus.RESERVED }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Navy900.copy(alpha = 0.95f),
        border = BorderStroke(1.dp, Slate700.copy(alpha = 0.8f)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "02 • AQLLI JOYLARNI NAZORAT QILISH (1–5 QAVATLAR)",
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandEmerald
                )
                Text(
                    text = "Qavatni tanlang va bo‘sh joyni bron qiling",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            // 1..5 floor selector tabs
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items((1..5).toList()) { floor ->
                    val floorFree = regionFloors[floor].orEmpty().count { it.status == SlotStatus.FREE }
                    val active = state.selectedFloor == floor
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (active) BrandBlue600 else Navy950,
                        border = BorderStroke(
                            1.dp,
                            if (active) BrandCyan else Slate700
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("floor_tab_$floor")
                            .clickable { onSelectFloor(floor) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "${floor}-qavat",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (active) Navy950.copy(alpha = 0.65f) else BrandEmerald.copy(alpha = 0.16f)
                            ) {
                                Text(
                                    text = "$floorFree bo‘sh",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Legend bar
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Navy950.copy(alpha = 0.8f),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        LegendItem(color = BrandGreen, label = "Bo‘sh ($freeCount)")
                        LegendItem(color = BrandRed, label = "Band ($occCount)")
                        LegendItem(color = BrandBlue, label = "Bron qilingan ($resCount)")
                    }
                    Text(
                        text = "* Yashil katak ustiga bosib darhol bron qiling",
                        style = MaterialTheme.typography.bodySmall,
                        color = BrandCyan
                    )
                }
            }

            // Interactive Grid Map (Top row A1..A8, Sensor Aisle, Bottom row B1..B8)
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Navy950,
                border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SlotGridRows(
                        slots = slots.take(8),
                        onSlotClick = onSlotClick
                    )

                    // Middle aisle: KIRISH SENSORI
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Navy900.copy(alpha = 0.9f),
                        border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandCyan.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.45f))
                            ) {
                                Text(
                                    text = "KIRISH SENSORI →",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandCyan,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "${state.selectedFloor}-qavat • A/B sektor ($freeCount ta bo‘sh)",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    SlotGridRows(
                        slots = slots.drop(8),
                        onSlotClick = onSlotClick
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = Slate300)
    }
}

@Composable
private fun SlotGridRows(
    slots: List<ParkingSlot>,
    onSlotClick: (ParkingSlot) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        slots.chunked(4).forEach { rowSlots ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowSlots.forEach { slot ->
                    ParkingSlotTile(
                        slot = slot,
                        onClick = { onSlotClick(slot) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ParkingSlotTile(
    slot: ParkingSlot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when (slot.status) {
        SlotStatus.FREE -> BrandGreen.copy(alpha = 0.2f)
        SlotStatus.OCCUPIED -> BrandRed.copy(alpha = 0.24f)
        SlotStatus.RESERVED -> BrandBlue.copy(alpha = 0.24f)
    }
    val borderColor = when (slot.status) {
        SlotStatus.FREE -> BrandEmerald
        SlotStatus.OCCUPIED -> BrandRed.copy(alpha = 0.8f)
        SlotStatus.RESERVED -> BrandBlue
    }
    val dotColor = when (slot.status) {
        SlotStatus.FREE -> BrandEmerald
        SlotStatus.OCCUPIED -> BrandRed
        SlotStatus.RESERVED -> BrandCyan
    }
    val statusText = when (slot.status) {
        SlotStatus.FREE -> "BO‘SH"
        SlotStatus.OCCUPIED -> "BAND"
        SlotStatus.RESERVED -> "BRON"
    }

    Surface(
        modifier = modifier
            .height(68.dp)
            .testTag("parking_slot_${slot.id}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(1.5.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = slot.code,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.ExtraBold
                ),
                color = Color.White
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                color = Slate300
            )
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
        }
    }
}

@Composable
private fun SubscriptionsSection(
    state: ParkWayUiState,
    onBuySubscription: (String, Int, Long) -> Unit
) {
    val regionFloors = state.floorsData[state.selectedRegionId].orEmpty()

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "03 • DOIMIY MIJOZLAR UCHUN OBUNA PAKETLARI",
                style = MaterialTheme.typography.labelSmall,
                color = BrandAmber
            )
            Text(
                text = "Qulay obuna tarifini tanlang",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
            Text(
                text = "Obuna sotib olinganda soatlar balansingizga avtomatik qo‘shiladi",
                style = MaterialTheme.typography.bodySmall,
                color = Slate400
            )
        }

        // 1. PLUS 100 soat
        SubscriptionPlanCard(
            badge = "PLUS OBUNA",
            badgeColor = BrandBlue,
            subtitle = "100 soatlik paket",
            hoursTitle = "100 soat",
            periodNote = "/ oyiga",
            priceText = "450,000 so‘m",
            priceColor = BrandCyan,
            features = listOf(
                "100 soatlik avtoturargoh limiti",
                "Davlat raqami orqali shlagbaumdan tez kirish",
                "1–5 qavatlarda oldindan joy bron qilish"
            ),
            buttonText = "100 soat PLUS sotib olish",
            buttonColor = BrandBlue600,
            buttonTextColor = Color.White,
            borderColor = BrandBlue.copy(alpha = 0.4f),
            highlightTag = null,
            onBuy = { onBuySubscription("PLUS 100 soat", 100, 450_000L) },
            testTag = "buy_plus_100"
        )

        // 2. PRO 200 soat
        SubscriptionPlanCard(
            badge = "PRO OBUNA",
            badgeColor = BrandCyan,
            subtitle = "200 soatlik paket",
            hoursTitle = "200 soat",
            periodNote = "/ oyiga",
            priceText = "800,000 so‘m",
            priceColor = BrandEmerald,
            features = listOf(
                "200 soatlik kengaytirilgan balans",
                "Barcha 14 viloyat parkovkalarida amal qiladi",
                "Navbatsiz AI Fast-Pass va SMS xabarnoma"
            ),
            buttonText = "200 soat PRO sotib olish",
            buttonColor = BrandCyan,
            buttonTextColor = Navy950,
            borderColor = BrandCyan.copy(alpha = 0.65f),
            highlightTag = "Ommabop tanlov",
            onBuy = { onBuySubscription("PRO 200 soat", 200, 800_000L) },
            testTag = "buy_pro_200"
        )

        // 3. VIP MAX 300 soat + Jonli Radar
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Navy950,
            border = BorderStroke(2.dp, BrandAmber.copy(alpha = 0.65f)),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .background(Brush.verticalGradient(listOf(VipNavyTop, Navy950)))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandAmber.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = "VIP MAX 300",
                            style = MaterialTheme.typography.labelMedium,
                            color = BrandAmber,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Surface(
                        shape = CircleShape,
                        color = BrandAmber
                    ) {
                        Text(
                            text = "300h + Jonli Radar",
                            style = MaterialTheme.typography.labelSmall,
                            color = Navy950,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "300 soat",
                        style = MaterialTheme.typography.headlineMedium.copy(fontFamily = JetBrainsMonoFontFamily),
                        color = Color.White
                    )
                    Text(
                        text = "+ AI Joy Topuvchi",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate300,
                        modifier = Modifier.padding(bottom = 3.dp)
                    )
                }

                Text(
                    text = "1,100,000 so‘m",
                    style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMonoFontFamily),
                    color = BrandAmber
                )

                // VIP 300 Live Radar box showing exact free spots per floor
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy950.copy(alpha = 0.92f),
                    border = BorderStroke(1.dp, BrandAmber.copy(alpha = 0.35f))
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(BrandEmerald)
                                )
                                Text(
                                    text = "Qayerda bo‘sh joy bor (Jonli):",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BrandAmber
                                )
                            }
                            Text(
                                text = "1–5 qavat",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandEmerald
                            )
                        }

                        (1..5).forEach { floor ->
                            val freeCodes = regionFloors[floor].orEmpty()
                                .filter { it.status == SlotStatus.FREE }
                                .map { it.code }
                            val preview = if (freeCodes.isEmpty()) {
                                "Bo‘sh joy yo‘q"
                            } else {
                                freeCodes.take(5).joinToString(", ") +
                                    if (freeCodes.size > 5) " (+${freeCodes.size - 5})" else ""
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Navy900.copy(alpha = 0.9f),
                                border = BorderStroke(1.dp, Slate800)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${floor}-qavat:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = preview,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BrandEmerald
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Hudud: ${state.currentRegion.name} • Barcha bo‘sh kataklar faol",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                            color = BrandCyan
                        )
                    }
                }

                Button(
                    onClick = { onBuySubscription("VIP MAX 300 soat", 300, 1_100_000L) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandAmber,
                        contentColor = Navy950
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("buy_vip_300")
                ) {
                    Text(
                        text = "300 soat VIP sotib olish",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionPlanCard(
    badge: String,
    badgeColor: Color,
    subtitle: String,
    hoursTitle: String,
    periodNote: String,
    priceText: String,
    priceColor: Color,
    features: List<String>,
    buttonText: String,
    buttonColor: Color,
    buttonTextColor: Color,
    borderColor: Color,
    highlightTag: String?,
    onBuy: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Navy900.copy(alpha = 0.95f),
        border = BorderStroke(if (highlightTag != null) 2.dp else 1.dp, borderColor),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                if (highlightTag != null) {
                    Surface(
                        shape = CircleShape,
                        color = BrandCyan
                    ) {
                        Text(
                            text = highlightTag,
                            style = MaterialTheme.typography.labelSmall,
                            color = Navy950,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp)
                        )
                    }
                } else {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate400
                    )
                }
            }

            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = hoursTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(fontFamily = JetBrainsMonoFontFamily),
                    color = Color.White
                )
                Text(
                    text = periodNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate400,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }

            Text(
                text = priceText,
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = JetBrainsMonoFontFamily),
                color = priceColor
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                features.forEach { feat ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("✓", color = BrandEmerald, fontWeight = FontWeight.Bold)
                        Text(feat, style = MaterialTheme.typography.bodyMedium, color = Slate300)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onBuy,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag(testTag)
            ) {
                Text(text = buttonText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun BookingDialogModal(
    slot: ParkingSlot,
    region: ParkRegion,
    initialPlate: String,
    subscriptionHours: Int,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Boolean) -> Unit
) {
    var plate by remember(slot.id) { mutableStateOf(initialPlate) }
    var selectedHours by remember(slot.id) { mutableIntStateOf(2) }
    var payWithBalance by remember(slot.id) { mutableStateOf(subscriptionHours > 0) }
    val hourOptions = listOf(1, 2, 3, 5, 12)
    val totalCost = selectedHours * region.rate
    val enoughSub = subscriptionHours >= selectedHours

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Navy900,
            border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.45f)),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI ALPR • JOYNI BRON QILISH",
                            style = MaterialTheme.typography.labelSmall,
                            color = BrandCyan
                        )
                        Text(
                            text = "${region.name} • ${slot.floor}-qavat, Joy #${slot.code}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Yopish",
                            tint = Slate300
                        )
                    }
                }

                HorizontalDivider(color = Slate800)

                OutlinedTextField(
                    value = plate,
                    onValueChange = { plate = it.uppercase().take(12) },
                    label = { Text("Avtomobil davlat raqami:") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Navy950,
                        unfocusedContainerColor = Navy950,
                        focusedBorderColor = BrandCyan,
                        unfocusedBorderColor = BrandBlue.copy(alpha = 0.4f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("modal_plate_input")
                )

                Text(
                    text = "Davomiyligi (soat):",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate300
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    hourOptions.forEach { hrs ->
                        val selected = selectedHours == hrs
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (selected) BrandBlue600 else Navy950,
                            border = BorderStroke(1.dp, if (selected) BrandCyan else Slate700),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .clickable { selectedHours = hrs }
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            ) {
                                Text(
                                    text = "${hrs}h",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "To‘lov turi:",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate300
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (payWithBalance) BrandEmerald.copy(alpha = 0.2f) else Navy950,
                        border = BorderStroke(1.dp, if (payWithBalance) BrandEmerald else Slate700),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clickable { payWithBalance = true }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Obuna soatidan",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (!payWithBalance) BrandBlue.copy(alpha = 0.2f) else Navy950,
                        border = BorderStroke(1.dp, if (!payWithBalance) BrandCyan else Slate700),
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .clickable { payWithBalance = false }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Naqd / Karta",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Navy950,
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Viloyat tarifi:", style = MaterialTheme.typography.bodySmall, color = Slate400)
                            Text("${formatUZS(region.rate)}/soat", style = MaterialTheme.typography.labelSmall, color = Slate300)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Obuna balansingiz:", style = MaterialTheme.typography.bodySmall, color = Slate400)
                            Text("$subscriptionHours soat", style = MaterialTheme.typography.labelSmall, color = BrandAmber)
                        }
                        HorizontalDivider(color = Slate800)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Jami to‘lov:", style = MaterialTheme.typography.titleSmall, color = Color.White)
                            Text(
                                text = when {
                                    payWithBalance && enoughSub -> "0 so‘m (-${selectedHours}h obunadan)"
                                    payWithBalance && !enoughSub -> formatUZS(totalCost)
                                    else -> formatUZS(totalCost)
                                },
                                style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFontFamily),
                                color = if (payWithBalance && enoughSub) BrandEmerald else BrandCyan
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Slate800,
                            contentColor = Slate300
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Text("Bekor qilish")
                    }
                    Button(
                        onClick = { onConfirm(plate, selectedHours, payWithBalance) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandBlue600,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("confirm_booking_button")
                    ) {
                        Text("Tasdiqlash")
                    }
                }
            }
        }
    }
}
