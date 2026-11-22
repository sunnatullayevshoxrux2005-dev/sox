package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ActivePanel
import com.example.data.model.ParkRegion
import com.example.data.model.ParkingSlot
import com.example.data.model.ParkingTransaction
import com.example.data.model.SlotStatus
import com.example.data.model.ToastMessage
import com.example.data.model.UZBEKISTAN_REGIONS
import com.example.data.model.formatUZS
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ParkWayUiState(
    val activePanel: ActivePanel = ActivePanel.CLIENT,
    val selectedRegionId: String = "toshkent_sh",
    val selectedFloor: Int = 1,
    val userPlate: String = "01 A 123 BC",
    val subscriptionName: String = "PLUS 100 soat",
    val subscriptionHours: Int = 100,
    val lastBookingText: String = "Hali bron qilinmagan",
    val selectedSlotToBook: ParkingSlot? = null,
    val floorsData: Map<String, Map<Int, List<ParkingSlot>>> = emptyMap(),
    val totalRevenue: Long = 18_450_000L,
    val todayRevenue: Long = 2_890_000L,
    val subRevenue: Long = 14_200_000L,
    val bookingRevenue: Long = 4_250_000L,
    val activeSubscribers: Int = 48,
    val regionRevenues: Map<String, Long> = emptyMap(),
    val transactions: List<ParkingTransaction> = emptyList(),
    val toast: ToastMessage? = null
) {
    val currentRegion: ParkRegion
        get() = UZBEKISTAN_REGIONS.firstOrNull { it.id == selectedRegionId } ?: UZBEKISTAN_REGIONS.first()

    val currentFloorSlots: List<ParkingSlot>
        get() = floorsData[selectedRegionId]?.get(selectedFloor).orEmpty()

    val regionTotals: Triple<Int, Int, Int>
        get() {
            val floors = floorsData[selectedRegionId].orEmpty()
            var free = 0
            var occ = 0
            var res = 0
            floors.values.flatten().forEach { slot ->
                when (slot.status) {
                    SlotStatus.FREE -> free++
                    SlotStatus.OCCUPIED -> occ++
                    SlotStatus.RESERVED -> res++
                }
            }
            return Triple(free, occ, res)
        }
}

class ParkwayViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<ParkWayUiState> = _uiState.asStateFlow()

    private var toastJob: Job? = null

    private fun createInitialState(): ParkWayUiState {
        val floorsMap = mutableMapOf<String, Map<Int, List<ParkingSlot>>>()
        UZBEKISTAN_REGIONS.forEachIndexed { rIdx, region ->
            val regionFloors = mutableMapOf<Int, List<ParkingSlot>>()
            for (f in 1..5) {
                val slots = (1..16).map { i ->
                    val code = if (i <= 8) "A$i" else "B${i - 8}"
                    val seed = (rIdx * 7 + f * 13 + i * 5) % 11
                    val status = when (seed) {
                        1, 4, 7, 9 -> SlotStatus.OCCUPIED
                        2, 8 -> SlotStatus.RESERVED
                        else -> SlotStatus.FREE
                    }
                    ParkingSlot(
                        id = "$f-$code",
                        floor = f,
                        code = code,
                        status = status,
                        plate = if (status != SlotStatus.FREE) "01 X 882 AA" else ""
                    )
                }
                regionFloors[f] = slots
            }
            floorsMap[region.id] = regionFloors
        }

        val initialRegionRevenues = linkedMapOf(
            "Toshkent shahri" to 8_400_000L,
            "Samarqand viloyati" to 3_150_000L,
            "Buxoro viloyati" to 2_200_000L,
            "Farg‘ona viloyati" to 1_850_000L,
            "Andijon viloyati" to 1_550_000L,
            "Boshqa viloyatlar" to 1_300_000L
        )

        val initialTransactions = listOf(
            ParkingTransaction("tx-1", "12:58", "01 Z 777 ZZ", "Toshkent shahri", "VIP MAX 300 soat obuna", 1_100_000L),
            ParkingTransaction("tx-2", "12:41", "30 A 512 BA", "Samarqand viloyati", "PRO 200 soat obuna", 800_000L),
            ParkingTransaction("tx-3", "12:25", "80 M 304 AA", "Buxoro viloyati", "2-qavat #B3 (3 soat bron)", 19_500L),
            ParkingTransaction("tx-4", "11:50", "01 H 909 KA", "Toshkent shahri", "PLUS 100 soat obuna", 450_000L),
            ParkingTransaction("tx-5", "11:15", "40 D 118 OA", "Farg‘ona viloyati", "1-qavat #A5 (4 soat bron)", 22_000L)
        )

        return ParkWayUiState(
            floorsData = floorsMap,
            regionRevenues = initialRegionRevenues,
            transactions = initialTransactions
        )
    }

    fun switchPanel(panel: ActivePanel) {
        _uiState.update { it.copy(activePanel = panel) }
    }

    fun selectRegion(regionId: String) {
        _uiState.update { it.copy(selectedRegionId = regionId) }
    }

    fun selectFloor(floor: Int) {
        _uiState.update { it.copy(selectedFloor = floor.coerceIn(1, 5)) }
    }

    fun onSlotClicked(slot: ParkingSlot) {
        val currentFloor = _uiState.value.selectedFloor
        when (slot.status) {
            SlotStatus.FREE -> {
                _uiState.update { it.copy(selectedSlotToBook = slot) }
            }
            SlotStatus.OCCUPIED -> {
                showToast(
                    "Joy band",
                    "${currentFloor}-qavat, ${slot.code} joyda hozir avtomobil turibdi."
                )
            }
            SlotStatus.RESERVED -> {
                showToast(
                    "Allaqachon bron qilingan",
                    "${currentFloor}-qavat, ${slot.code} joy band qilingan."
                )
            }
        }
    }

    fun closeBookingModal() {
        _uiState.update { it.copy(selectedSlotToBook = null) }
    }

    fun confirmBooking(plateInput: String, hours: Int, payWithBalance: Boolean) {
        val currentState = _uiState.value
        val slot = currentState.selectedSlotToBook ?: return
        val cleanPlate = plateInput.trim().uppercase()
        if (cleanPlate.isEmpty()) {
            showToast("Xatolik", "Iltimos, avtomobil davlat raqamini kiriting!")
            return
        }

        val currentRegion = currentState.currentRegion
        val totalAmount = hours * currentRegion.rate
        val useBalance = payWithBalance && currentState.subscriptionHours >= hours
        val timeStr = SimpleDateFormat("HH:mm", Locale.US).format(Date())

        val updatedFloors = currentState.floorsData.toMutableMap()
        val regionFloors = updatedFloors[currentRegion.id].orEmpty().toMutableMap()
        val floorSlots = regionFloors[slot.floor].orEmpty().map { item ->
            if (item.id == slot.id) {
                item.copy(status = SlotStatus.RESERVED, plate = cleanPlate)
            } else {
                item
            }
        }
        regionFloors[slot.floor] = floorSlots
        updatedFloors[currentRegion.id] = regionFloors

        val updatedRegionRev = currentState.regionRevenues.toMutableMap()
        if (!useBalance) {
            if (updatedRegionRev.containsKey(currentRegion.name)) {
                updatedRegionRev[currentRegion.name] = (updatedRegionRev[currentRegion.name] ?: 0L) + totalAmount
            } else {
                updatedRegionRev["Boshqa viloyatlar"] = (updatedRegionRev["Boshqa viloyatlar"] ?: 0L) + totalAmount
            }
        }

        val newTx = ParkingTransaction(
            id = "tx-${System.currentTimeMillis()}",
            time = timeStr,
            plate = cleanPlate,
            region = currentRegion.name,
            service = if (useBalance) {
                "${slot.floor}-qavat #${slot.code} (Obuna: -${hours}h)"
            } else {
                "${slot.floor}-qavat #${slot.code} ($hours soat bron)"
            },
            amount = if (useBalance) 0L else totalAmount
        )

        val shortRegion = currentRegion.name.replace(" viloyati", "")
        val newSubHours = if (useBalance) currentState.subscriptionHours - hours else currentState.subscriptionHours

        _uiState.update { state ->
            state.copy(
                userPlate = cleanPlate,
                subscriptionHours = newSubHours,
                lastBookingText = "$shortRegion, ${slot.floor}-qavat #${slot.code}",
                selectedSlotToBook = null,
                floorsData = updatedFloors,
                totalRevenue = if (useBalance) state.totalRevenue else state.totalRevenue + totalAmount,
                todayRevenue = if (useBalance) state.todayRevenue else state.todayRevenue + totalAmount,
                bookingRevenue = if (useBalance) state.bookingRevenue else state.bookingRevenue + totalAmount,
                regionRevenues = updatedRegionRev,
                transactions = listOf(newTx) + state.transactions
            )
        }

        if (useBalance) {
            showToast(
                "Joy bron qilindi!",
                "${currentRegion.name}, ${slot.floor}-qavat #${slot.code} joy obuna hisobidan band qilindi."
            )
        } else {
            showToast(
                "To‘lov va bron tasdiqlandi!",
                "${formatUZS(totalAmount)} to‘landi. Joy #${slot.code} siz uchun band!"
            )
        }
    }

    fun buySubscription(planName: String, hours: Int, price: Long) {
        val currentState = _uiState.value
        val currentRegion = currentState.currentRegion
        val timeStr = SimpleDateFormat("HH:mm", Locale.US).format(Date())

        val updatedRegionRev = currentState.regionRevenues.toMutableMap()
        if (updatedRegionRev.containsKey(currentRegion.name)) {
            updatedRegionRev[currentRegion.name] = (updatedRegionRev[currentRegion.name] ?: 0L) + price
        } else {
            updatedRegionRev["Boshqa viloyatlar"] = (updatedRegionRev["Boshqa viloyatlar"] ?: 0L) + price
        }

        val newTx = ParkingTransaction(
            id = "tx-${System.currentTimeMillis()}",
            time = timeStr,
            plate = currentState.userPlate,
            region = currentRegion.name,
            service = "$planName obuna paketi",
            amount = price
        )

        val newTotalHours = currentState.subscriptionHours + hours

        _uiState.update { state ->
            state.copy(
                subscriptionName = planName,
                subscriptionHours = newTotalHours,
                totalRevenue = state.totalRevenue + price,
                todayRevenue = state.todayRevenue + price,
                subRevenue = state.subRevenue + price,
                activeSubscribers = state.activeSubscribers + 1,
                regionRevenues = updatedRegionRev,
                transactions = listOf(newTx) + state.transactions
            )
        }

        showToast(
            "$planName faollashtirildi!",
            "Balansingizga +$hours soat qo‘shildi. Jami: $newTotalHours soat."
        )
    }

    fun showToast(title: String, message: String) {
        toastJob?.cancel()
        _uiState.update {
            it.copy(toast = ToastMessage(System.currentTimeMillis(), title, message))
        }
        toastJob = viewModelScope.launch {
            delay(3600L)
            _uiState.update { it.copy(toast = null) }
        }
    }
}
