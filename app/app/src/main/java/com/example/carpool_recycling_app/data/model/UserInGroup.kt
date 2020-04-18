package com.example.carpool_recycling_app.data.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserInGroup(
    var uid: String? = ""
)