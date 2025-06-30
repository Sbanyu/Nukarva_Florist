package com.example.nukarva_florist.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(
    val id: Int,
    val userId: Int,
    val label: String,
    val addressLine: String,
    val city: String,
    val postalCode: String,
    val country: String,
    val recipientName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val notes: String,
    val pinpoint: String,
    val isMain: Boolean,
    val latitude: Double,
    val longitude: Double
) : Parcelable
