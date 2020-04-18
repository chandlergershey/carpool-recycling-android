package com.example.carpool_recycling_app.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class SimpleGroup(
    var id: String? = "",
    var groupName: String? = "",
    var adminId: String? = "",
    var inRoute: Boolean = false
)