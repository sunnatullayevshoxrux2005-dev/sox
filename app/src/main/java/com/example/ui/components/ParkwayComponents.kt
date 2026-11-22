package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.SpanStyle as TextSpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandBlue
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandGreen
import com.example.ui.theme.BrandRed
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.Navy800
import com.example.ui.theme.Navy900
import com.example.ui.theme.Navy950
import com.example.ui.theme.Slate300

@Composable
fun AnimatedParkingBackdrop(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "bgCars")
    val car1Progress by transition.animateFloat(
        initialValue = -0.2f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "car1"
    )
    val car2Progress by transition.animateFloat(
        initialValue = 1.2f,
        targetValue = -0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "car2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(color = Navy950)

        // Subtle grid pattern matching .bg-grid-pattern
        val step = 36.dp.toPx()
        val gridColor = BrandBlue.copy(alpha = 0.07f)
        var x = 0f
        while (x < size.width) {
            drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            x += step
        }
        var y = 0f
        while (y < size.height) {
            drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            y += step
        }

        // Ambient glow blobs
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(BrandBlue.copy(alpha = 0.14f), Color.Transparent),
                center = Offset(size.width * 0.25f, 0f),
                radius = size.width * 0.45f
            ),
            radius = size.width * 0.45f,
            center = Offset(size.width * 0.25f, 0f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(BrandEmerald.copy(alpha = 0.09f), Color.Transparent),
                center = Offset(size.width * 0.78f, size.height * 0.9f),
                radius = size.width * 0.45f
            ),
            radius = size.width * 0.45f,
            center = Offset(size.width * 0.78f, size.height * 0.9f)
        )

        // Dashed lane lines & moving silhouette cars
        val topLaneY = size.height * 0.18f
        val bottomLaneY = size.height * 0.88f
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)

        drawLine(
            color = BrandBlue.copy(alpha = 0.14f),
            start = Offset(0f, topLaneY),
            end = Offset(size.width, topLaneY),
            pathEffect = dashEffect,
            strokeWidth = 1.5f
        )
        drawLine(
            color = BrandBlue.copy(alpha = 0.16f),
            start = Offset(0f, bottomLaneY),
            end = Offset(size.width, bottomLaneY),
            pathEffect = dashEffect,
            strokeWidth = 1.5f
        )

        // Car 1 moving right along bottom lane
        drawCarSilhouette(
            x = size.width * car1Progress,
            y = bottomLaneY - 26.dp.toPx(),
            bodyColor = Color(0xFF1E3A8A).copy(alpha = 0.55f),
            strokeColor = BrandCyan.copy(alpha = 0.5f),
            headlightColor = Color(0xFFFDE047).copy(alpha = 0.65f),
            facingRight = true
        )

        // Car 2 moving left along top lane
        drawCarSilhouette(
            x = size.width * car2Progress,
            y = topLaneY - 26.dp.toPx(),
            bodyColor = Color(0xFF064E3B).copy(alpha = 0.48f),
            strokeColor = BrandEmerald.copy(alpha = 0.48f),
            headlightColor = BrandCyan.copy(alpha = 0.65f),
            facingRight = false
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCarSilhouette(
    x: Float,
    y: Float,
    bodyColor: Color,
    strokeColor: Color,
    headlightColor: Color,
    facingRight: Boolean
) {
    val w = 78.dp.toPx()
    val h = 24.dp.toPx()
    drawRoundRect(
        color = bodyColor,
        topLeft = Offset(x, y + 8.dp.toPx()),
        size = Size(w, h * 0.55f),
        cornerRadius = CornerRadius(5.dp.toPx())
    )
    drawRoundRect(
        color = strokeColor,
        topLeft = Offset(x, y + 8.dp.toPx()),
        size = Size(w, h * 0.55f),
        cornerRadius = CornerRadius(5.dp.toPx()),
        style = Stroke(1.3.dp.toPx())
    )
    val cabinPath = Path().apply {
        moveTo(x + w * 0.22f, y + 8.dp.toPx())
        lineTo(x + w * 0.34f, y)
        lineTo(x + w * 0.68f, y)
        lineTo(x + w * 0.78f, y + 8.dp.toPx())
        close()
    }
    drawPath(cabinPath, color = Navy950.copy(alpha = 0.6f))
    drawPath(cabinPath, color = strokeColor, style = Stroke(1.2.dp.toPx()))

    drawCircle(Navy900, radius = 4.5.dp.toPx(), center = Offset(x + w * 0.24f, y + 22.dp.toPx()))
    drawCircle(strokeColor, radius = 4.5.dp.toPx(), center = Offset(x + w * 0.24f, y + 22.dp.toPx()), style = Stroke(1.5.dp.toPx()))
    drawCircle(Navy900, radius = 4.5.dp.toPx(), center = Offset(x + w * 0.76f, y + 22.dp.toPx()))
    drawCircle(strokeColor, radius = 4.5.dp.toPx(), center = Offset(x + w * 0.76f, y + 22.dp.toPx()), style = Stroke(1.5.dp.toPx()))

    val lightX = if (facingRight) x + w - 5.dp.toPx() else x
    drawRect(
        color = headlightColor,
        topLeft = Offset(lightX, y + 11.dp.toPx()),
        size = Size(5.dp.toPx(), 3.5.dp.toPx())
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroBannerSection(
    freeCount: Int,
    occupiedCount: Int,
    reservedCount: Int,
    activePlate: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Navy900.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.28f)),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        listOf(Navy900, Navy800.copy(alpha = 0.92f), Navy900)
                    )
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // AI Badge
            Surface(
                shape = CircleShape,
                color = BrandBlue.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
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
                        text = "AI KOMPYUTER KO‘RISHI • IoT SENSORLAR • ALPR OCR",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color(0xFF67E8F9)
                    )
                }
            }

            // Headline
            Text(
                text = buildAnnotatedString {
                    append("Sun’iy intellekt asosida avtomatlashtirilgan ")
                    withStyle(TextSpanStyle(color = BrandCyan)) {
                        append("aqlli avtoturargoh")
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )

            Text(
                text = "Davlat raqamini AI yordamida soniyalar ichida tanib olish, 1–5 qavatlardagi bo‘sh joylarni real vaqtda kuzatish va qulay obuna paketlari orqali navbatsiz kirish.",
                style = MaterialTheme.typography.bodyMedium,
                color = Slate300
            )

            // Live status pills
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeroCountPill(color = BrandGreen, label = "Bo‘sh joylar:", count = freeCount)
                HeroCountPill(color = BrandRed, label = "Band:", count = occupiedCount)
                HeroCountPill(color = BrandBlue, label = "Bron qilingan:", count = reservedCount)
            }

            // Live AI Camera + Car + Shlagbaum Simulation Card
            LiveAiCameraSimulationCard(activePlate = activePlate)
        }
    }
}

@Composable
private fun HeroCountPill(
    color: Color,
    label: String,
    count: Int
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Navy950.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Slate300
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelLarge.copy(fontFamily = JetBrainsMonoFontFamily),
                color = color
            )
        }
    }
}

@Composable
fun LiveAiCameraSimulationCard(activePlate: String) {
    val transition = rememberInfiniteTransition(label = "heroSim")
    val beamAlpha by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "beamAlpha"
    )
    val barrierAngle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 7000
                0f at 0
                0f at 2800
                -56f at 3850
                -56f at 5950
                0f at 7000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "barrierAngle"
    )

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Navy950.copy(alpha = 0.94f),
        border = BorderStroke(1.dp, BrandBlue.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            .background(BrandRed)
                    )
                    Text(
                        text = "Avto turargoh • jonli AI nazorat",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF67E8F9)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandBlue.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "OCR: $activePlate",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF93C5FD),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF081026), Color(0xFF0E1B3D))
                        )
                    )
            ) {
                val sx = size.width / 380f
                val sy = size.height / 145f

                // Camera pole & housing
                drawRect(Color(0xFF1E3A8A), Offset(28 * sx, 42 * sy), Size(5 * sx, 90 * sy))
                drawRoundRect(
                    color = Color(0xFF0F172A),
                    topLeft = Offset(14 * sx, 24 * sy),
                    size = Size(44 * sx, 22 * sy),
                    cornerRadius = CornerRadius(5 * sx)
                )
                drawRoundRect(
                    color = BrandCyan,
                    topLeft = Offset(14 * sx, 24 * sy),
                    size = Size(44 * sx, 22 * sy),
                    cornerRadius = CornerRadius(5 * sx),
                    style = Stroke(1.6f * sx)
                )
                drawCircle(BrandGreen, 2.2f * sx, Offset(22 * sx, 31 * sy))
                drawCircle(BrandCyan, 5.5f * sx, Offset(50 * sx, 35 * sy), style = Stroke(2f * sx))
                drawCircle(BrandCyan, 2.5f * sx, Offset(50 * sx, 35 * sy))

                // Scanning laser beam
                val beamPath = Path().apply {
                    moveTo(56 * sx, 36 * sy)
                    lineTo(135 * sx, 112 * sy)
                    lineTo(185 * sx, 112 * sy)
                    close()
                }
                drawPath(
                    path = beamPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            BrandCyan.copy(alpha = beamAlpha),
                            BrandCyan.copy(alpha = 0.03f)
                        ),
                        start = Offset(56 * sx, 36 * sy),
                        end = Offset(180 * sx, 112 * sy)
                    )
                )

                // Mini monitor top right
                drawRoundRect(
                    color = Navy900,
                    topLeft = Offset(195 * sx, 10 * sy),
                    size = Size(172 * sx, 66 * sy),
                    cornerRadius = CornerRadius(7 * sx)
                )
                drawRoundRect(
                    color = Color(0xFF2563EB),
                    topLeft = Offset(195 * sx, 10 * sy),
                    size = Size(172 * sx, 66 * sy),
                    cornerRadius = CornerRadius(7 * sx),
                    style = Stroke(1.2f * sx)
                )
                drawCircle(BrandRed, 2 * sx, Offset(205 * sx, 19 * sy))
                drawCircle(Color(0xFFF59E0B), 2 * sx, Offset(212 * sx, 19 * sy))
                drawCircle(BrandGreen, 2 * sx, Offset(219 * sx, 19 * sy))

                val row1Colors = listOf(BrandGreen, BrandRed, BrandRed, BrandGreen, BrandBlue, BrandGreen)
                val row2Colors = listOf(BrandRed, BrandGreen, BrandGreen, BrandRed, BrandGreen, BrandBlue)
                row1Colors.forEachIndexed { idx, col ->
                    drawRoundRect(
                        color = col,
                        topLeft = Offset((205 + idx * 18) * sx, 28 * sy),
                        size = Size(14 * sx, 12 * sy),
                        cornerRadius = CornerRadius(2 * sx)
                    )
                }
                row2Colors.forEachIndexed { idx, col ->
                    drawRoundRect(
                        color = col,
                        topLeft = Offset((205 + idx * 18) * sx, 44 * sy),
                        size = Size(14 * sx, 12 * sy),
                        cornerRadius = CornerRadius(2 * sx)
                    )
                }

                // Mini sparkline & ring
                val chartPath = Path().apply {
                    moveTo(205 * sx, 68 * sy)
                    lineTo(230 * sx, 62 * sy)
                    lineTo(255 * sx, 65 * sy)
                    lineTo(285 * sx, 58 * sy)
                    lineTo(315 * sx, 61 * sy)
                    lineTo(350 * sx, 52 * sy)
                }
                drawPath(chartPath, color = BrandCyan, style = Stroke(1.8f * sx))
                drawArc(
                    color = BrandGreen,
                    startAngle = -90f,
                    sweepAngle = 250f,
                    useCenter = false,
                    topLeft = Offset(330 * sx, 27 * sy),
                    size = Size(22 * sx, 22 * sx),
                    style = Stroke(3.5f * sx)
                )

                // Front-facing car silhouette
                drawOval(
                    color = Color(0xFF040814),
                    topLeft = Offset(112 * sx, 125 * sy),
                    size = Size(96 * sx, 10 * sy)
                )
                val roofPath = Path().apply {
                    moveTo(131 * sx, 96 * sy)
                    lineTo(139 * sx, 80 * sy)
                    lineTo(181 * sx, 80 * sy)
                    lineTo(189 * sx, 96 * sy)
                    close()
                }
                drawPath(roofPath, color = Color(0xFF172554))
                drawPath(roofPath, color = Color(0xFF60A5FA), style = Stroke(1.6f * sx))

                drawRoundRect(
                    color = Color(0xFF1E3A8A),
                    topLeft = Offset(123 * sx, 96 * sy),
                    size = Size(74 * sx, 26 * sy),
                    cornerRadius = CornerRadius(7 * sx)
                )
                drawRoundRect(
                    color = Color(0xFF60A5FA),
                    topLeft = Offset(123 * sx, 96 * sy),
                    size = Size(74 * sx, 26 * sy),
                    cornerRadius = CornerRadius(7 * sx),
                    style = Stroke(1.8f * sx)
                )
                // Headlights & License Plate
                drawRoundRect(Color(0xFFE0F2FE), Offset(129 * sx, 102 * sy), Size(12 * sx, 5 * sy), CornerRadius(2.5f * sx))
                drawRoundRect(Color(0xFFE0F2FE), Offset(179 * sx, 102 * sy), Size(12 * sx, 5 * sy), CornerRadius(2.5f * sx))
                drawRoundRect(Color(0xFFF8FAFC), Offset(148 * sx, 110 * sy), Size(24 * sx, 8 * sy), CornerRadius(1.5f * sx))
                drawRoundRect(BrandCyan, Offset(148 * sx, 110 * sy), Size(24 * sx, 8 * sy), CornerRadius(1.5f * sx), style = Stroke(1.4f * sx))
                // Tires
                drawRoundRect(Color(0xFF020617), Offset(129 * sx, 122 * sy), Size(10 * sx, 7 * sy), CornerRadius(2 * sx))
                drawRoundRect(Color(0xFF020617), Offset(181 * sx, 122 * sy), Size(10 * sx, 7 * sy), CornerRadius(2 * sx))

                // Rotating Shlagbaum Barrier Arm
                val pivot = Offset(334 * sx, 99.5f * sy)
                rotate(degrees = barrierAngle, pivot = pivot) {
                    drawRoundRect(
                        color = Color(0xFFF8FAFC),
                        topLeft = Offset(75 * sx, 96 * sy),
                        size = Size(250 * sx, 7 * sy),
                        cornerRadius = CornerRadius(3 * sx)
                    )
                    listOf(95, 139, 183, 227, 271).forEach { stripeX ->
                        drawRect(Color(0xFF2563EB), Offset(stripeX * sx, 96 * sy), Size(22 * sx, 7 * sy))
                    }
                    drawCircle(BrandRed, 2.2f * sx, Offset(79 * sx, 99.5f * sy))
                }

                // Barrier pedestal
                drawRoundRect(Color(0xFF172554), Offset(320 * sx, 84 * sy), Size(28 * sx, 48 * sy), CornerRadius(5 * sx))
                drawRoundRect(BrandBlue, Offset(320 * sx, 84 * sy), Size(28 * sx, 48 * sy), CornerRadius(5 * sx), style = Stroke(1.6f * sx))
                drawCircle(BrandGreen, 3 * sx, Offset(334 * sx, 93 * sy))

                // Ground baseline
                drawLine(Color(0xFF1E3A8A), Offset(0f, 132 * sy), Offset(size.width, 132 * sy), strokeWidth = 2 * sy)
            }
        }
    }
}

@Composable
fun ParkWayToastBanner(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Navy800,
        border = BorderStroke(1.dp, BrandCyan.copy(alpha = 0.5f)),
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(BrandEmerald)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate300
                )
            }
        }
    }
}
