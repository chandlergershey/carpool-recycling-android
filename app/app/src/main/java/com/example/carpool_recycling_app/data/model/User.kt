package com.example.carpool_recycling_app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String, val firstname: String, val lastname: String,
           val numPlastic: Int, val numPaper: Int, val numAluminum: Int, val numGlass: Int, val groupid: String, val newUser: Boolean): Parcelable {
    constructor() : this("", "", "", "", "",
        0, 0, 0, 0, "", true)
}