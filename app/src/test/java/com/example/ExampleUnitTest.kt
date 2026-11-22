package com.example

import com.example.data.model.ActivePanel
import com.example.data.model.SlotStatus
import com.example.data.model.UZBEKISTAN_REGIONS
import com.example.data.model.formatUZS
import com.example.ui.viewmodel.ParkwayViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `regions list contains all 14 Uzbekistan regions`() {
        assertEquals(14, UZBEKISTAN_REGIONS.size)
        assertEquals("10,000 so‘m", formatUZS(10_000L))
    }

    @Test
    fun `booking free slot updates status, subscription hours, and admin transactions`() {
        val viewModel = ParkwayViewModel()
        val initial = viewModel.uiState.value
        assertEquals(ActivePanel.CLIENT, initial.activePanel)
        assertEquals(100, initial.subscriptionHours)

        val freeSlot = initial.currentFloorSlots.first { it.status == SlotStatus.FREE }
        viewModel.onSlotClicked(freeSlot)
        assertNotNull(viewModel.uiState.value.selectedSlotToBook)

        viewModel.confirmBooking(plateInput = "01 Z 777 AA", hours = 2, payWithBalance = true)
        val afterBooking = viewModel.uiState.value
        assertEquals(98, afterBooking.subscriptionHours)
        assertEquals("01 Z 777 AA", afterBooking.userPlate)
        assertEquals(
            SlotStatus.RESERVED,
            afterBooking.currentFloorSlots.first { it.id == freeSlot.id }.status
        )
        assertEquals(initial.transactions.size + 1, afterBooking.transactions.size)
    }

    @Test
    fun `buying 300h VIP subscription increases hours and admin revenue`() {
        val viewModel = ParkwayViewModel()
        val before = viewModel.uiState.value

        viewModel.buySubscription("VIP MAX 300 soat", 300, 1_100_000L)
        val after = viewModel.uiState.value

        assertEquals(before.subscriptionHours + 300, after.subscriptionHours)
        assertEquals(before.totalRevenue + 1_100_000L, after.totalRevenue)
        assertEquals(before.subRevenue + 1_100_000L, after.subRevenue)
        assertTrue(after.transactions.first().service.contains("VIP MAX 300"))
    }
}
