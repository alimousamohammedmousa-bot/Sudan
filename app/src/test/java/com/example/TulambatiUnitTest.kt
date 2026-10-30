package com.example

import com.example.tulambati.data.local.InitialData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TulambatiUnitTest {

    @Test
    fun defaultStations_containsRealisticSudaneseStations() {
        val stations = InitialData.defaultStations()
        assertTrue("Stations list should not be empty", stations.isNotEmpty())

        val bashaer = stations.find { it.brand.contains("بشائر") }
        assertTrue("Bashaer station should exist", bashaer != null)
        assertTrue("Bashaer in Sittin St should have short or valid queue", bashaer!!.queueLevel == "SHORT")
        assertTrue("Prices should be positive", bashaer.petrolPrice > 0 && bashaer.dieselPrice > 0)
    }

    @Test
    fun defaultGasCenters_containsAntiPriceGougingData() {
        val gasCenters = InitialData.defaultGasCenters()
        assertTrue("Gas centers should not be empty", gasCenters.isNotEmpty())

        val amanGas = gasCenters.find { it.brand.contains("أمان") }
        assertTrue("Aman Gas should exist", amanGas != null)
        assertEquals(14500.0, amanGas!!.officialPrice, 0.01)
        assertTrue("Actual price should equal official price for fair centers", amanGas.actualPrice <= amanGas.officialPrice)
    }

    @Test
    fun defaultProfile_gamificationScoreIsValid() {
        val profile = InitialData.defaultProfile()
        assertTrue("Reputation score should be positive", profile.reputationScore > 0)
        assertTrue("Rank title should be present", profile.rankTitle.isNotBlank())
        assertFalse("Low bandwidth should default to false", profile.isLowBandwidthMode)
    }
}
