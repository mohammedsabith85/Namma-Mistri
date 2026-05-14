package com.example.nammamistri.logic

import kotlin.math.ceil
import kotlin.math.roundToInt

// Data classes to hold our inputs and outputs clearly
data class MaterialRates(
    val bricks: Double,
    val cement: Double,
    val sand: Double
)

data class CalculationResult(
    val bricks: Int,
    val cementBags: Int,
    val sandCft: Int,
    val materialCost: Int,
    val wastageCost: Int,
    val laborCost: Int,
    val totalCost: Int
)

// The Singleton Object that does all the heavy lifting
object CalculatorLogic {
    fun calculateMaterials(
        length: Double,
        width: Double,
        height: Double,
        thickness: Double,
        rates: MaterialRates,
        wastagePercent: Double = 5.0,
        laborPercent: Double = 15.0
    ): CalculationResult {

        // 1. Calculate Volume
        val totalLength = if (width > 0) 2 * (length + width) else length
        val volume = totalLength * height * (thickness / 12.0)

        // 2. Material Quantity Estimates
        val bricksRequired = ceil(volume * 13.5).toInt()
        val mortarVolume = volume * 0.3

        val cementBags = ceil(mortarVolume * 0.25).toInt()
        val sandCft = ceil(mortarVolume * 0.75).toInt()

        // 3. Cost Estimates
        val materialCost = (bricksRequired * (rates.bricks / 1000.0)) +
                (cementBags * rates.cement) +
                (sandCft * rates.sand)

        val wastageCost = (materialCost * wastagePercent) / 100.0
        val laborCost = (materialCost * laborPercent) / 100.0
        val totalCost = materialCost + wastageCost + laborCost

        // 4. Return the packaged result
        return CalculationResult(
            bricks = bricksRequired,
            cementBags = cementBags,
            sandCft = sandCft,
            materialCost = materialCost.roundToInt(),
            wastageCost = wastageCost.roundToInt(),
            laborCost = laborCost.roundToInt(),
            totalCost = totalCost.roundToInt()
        )
    }
}