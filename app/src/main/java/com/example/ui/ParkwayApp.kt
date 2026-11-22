package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Assessment
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ActivePanel
import com.example.ui.components.AnimatedParkingBackdrop
import com.example.ui.components.HeroBannerSection
import com.example.ui.components.ParkWayToastBanner
import com.example.ui.screens.AdminPanelContent
import com.example.ui.screens.ClientPanelContent
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandBlue600
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.viewmodel.ParkwayViewModel
import kotlinx.coroutines.launch

@Composable
fun ParkwayApp(
    viewModel: ParkwayViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val (freeTotal, occTotal, resTotal) = state.regionTotals

    Box(modifier = modifier.fillMaxSize()) {
        // Animated grid & silhouette cars background matching HTML
        AnimatedParkingBackdrop()

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                ParkWayHeaderBar(
                    activePanel = state.activePanel,
                    onSwitchPanel = viewModel::switchPanel
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 840.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HeroBannerSection(
                        freeCount = freeTotal,
                        occupiedCount = occTotal,
                        reservedCount = resTotal,
                        activePlate = state.userPlate
                    )

                    if (state.activePanel == ActivePanel.CLIENT) {
                        ClientPanelContent(
                            state = state,
                            onSelectRegion = viewModel::selectRegion,
                            onSelectFloor = viewModel::selectFloor,
                            onSlotClick = viewModel::onSlotClicked,
                            onScrollToSubscriptions = {
                                coroutineScope.launch {
                                    scrollState.animateScrollTo(scrollState.maxValue)
                                }
                            },
                            onBuySubscription = viewModel::buySubscription,
                            onConfirmBooking = viewModel::confirmBooking,
                            onCloseBookingModal = viewModel::closeBookingModal
                        )
                    } else {
                        AdminPanelContent(state = state)
                    }

                    ParkWayFooter()
                }
            }
        }

        // Floating Toast Banner
        AnimatedVisibility(
            visible = state.toast != null,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            state.toast?.let { toast ->
                ParkWayToastBanner(
                    title = toast.title,
                    message = toast.message
                )
            }
        }
    }
}

@Composable
private fun ParkWayHeaderBar(
    activePanel: ActivePanel,
    onSwitchPanel: (ActivePanel) -> Unit
) {
    Surface(
        color = Navy900.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                brush = Brush.linearGradient(listOf(BrandBlue, BrandCyan)),
                                shape = RoundedCornerShape(11.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            ),
                            color = Navy950
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    append("Seva’s ")
                                    withStyle(SpanStyle(color = Color(0xFF60A5FA))) {
                                        append("ParkWay")
                                    }
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandBlue.copy(alpha = 0.16f),
                                border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.3f))
                            ) {
                                Text(
                                    text = "AI • IoT",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                    color = BrandCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Aqlli avtoturargoh boshqaruv tizimi",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate400
                        )
                    }
                }
            }

            // Mijoz Paneli / Admin Paneli Switcher
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Navy950.copy(alpha = 0.92f),
                border = BorderStroke(1.dp, Slate700.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val isClient = activePanel == ActivePanel.CLIENT
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (isClient) BrandBlue600 else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("tab_client_panel")
                            .clickable { onSwitchPanel(ActivePanel.CLIENT) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DirectionsCar,
                                contentDescription = null,
                                tint = if (isClient) Color.White else Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Mijoz Paneli",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isClient) Color.White else Slate400
                            )
                        }
                    }

                    val isAdmin = activePanel == ActivePanel.ADMIN
                    Surface(
                        shape = RoundedCornerShape(9.dp),
                        color = if (isAdmin) BrandAmber else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .testTag("tab_admin_panel")
                            .clickable { onSwitchPanel(ActivePanel.ADMIN) }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Assessment,
                                contentDescription = null,
                                tint = if (isAdmin) Navy950 else Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = "Admin Paneli",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isAdmin) Navy950 else Slate400
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParkWayFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalDivider(color = Slate800)
        Text(
            text = "Seva’s ParkWay • Aqlli boshqaruv, qulay avtoturargoh, samarali shahar",
            style = MaterialTheme.typography.bodySmall,
            color = Slate400
        )
        Text(
            text = "Muallif: Tolibova Sevara Shaxruzovna • Telegram: @shxrzvnaa",
            style = MaterialTheme.typography.labelSmall,
            color = BrandCyan
        )
    }
}
