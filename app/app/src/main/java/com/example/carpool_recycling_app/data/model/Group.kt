package com.example.carpool_recycling_app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Group (val uid: String, val groupname: String, val admin: String):
    Parcelable {
        constructor() : this("", "", "")
}