package com.example.nammamistri.data

import java.util.UUID

// ADD THIS IF IT'S MISSING:
data class CalculationResult(
    val bricks: Int,
    val cementBags: Int,
    val sandCft: Double,
    val totalCost: Double
)

data class Worker(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val dailyRate: Int,
    val phone: String,
    var isPresent: Boolean = true,
    var currentAdvance: Int = 0,
    val totalAdvance: Int = 0
)

enum class SiteStatus { ACTIVE, COMPLETED, ON_HOLD }

data class Site(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val owner: String,
    val address: String,
    val status: SiteStatus = SiteStatus.ACTIVE
)