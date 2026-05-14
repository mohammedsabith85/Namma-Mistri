package com.example.nammamistri.model

// Keep your status enum here so the whole app can use it
enum class SiteStatus { ACTIVE, COMPLETED, HALTED }

// This is your unified Master Data Class for Firebase
data class SiteProject(
    var id: String = "", // Required for Firebase
    val siteName: String = "",
    val ownerName: String = "",
    val address: String = "",
    val village: String = "",
    val taluk: String = "",
    val district: String = "",
    val pincode: String = "",
    val state: String = "Karnataka",
    val landmark: String = "",
    val gps: String = "NOT CAPTURED",
    val budget: String = "",
    var status: String = SiteStatus.ACTIVE.name // Required for Firebase
)