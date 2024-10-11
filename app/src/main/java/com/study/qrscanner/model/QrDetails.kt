package com.study.qrscanner.model

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("qr_history")
data class QrDetails(
    @PrimaryKey var id: Int? = null,
    val qrData: String,
    val format: QR_FORMATE,
    var isFavorite: Boolean = false,
    val date: String,
)

enum class QR_FORMATE {
    SMS, URL, CONTACT, MAIL, PHONE, TEXT
}