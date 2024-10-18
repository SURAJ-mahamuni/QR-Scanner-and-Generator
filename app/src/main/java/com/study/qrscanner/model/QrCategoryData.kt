package com.study.qrscanner.model

import com.study.qrscanner.R

data class QrCategoryData(val icon: Int, val text: Int)

fun getQrCategoryDataList(): ArrayList<QrCategoryData> {
    return ArrayList<QrCategoryData>().apply {
        add(QrCategoryData(R.drawable.text_format_ic, R.string.text))
        add(QrCategoryData(R.drawable.url_format_ic, R.string.url))
        add(QrCategoryData(R.drawable.contact_format_ic, R.string.contact))
        add(QrCategoryData(R.drawable.email_format_ic, R.string.email))
        add(QrCategoryData(R.drawable.sms_format_ic, R.string.sms))
        add(QrCategoryData(R.drawable.contact_format_ic, R.string.phone))
    }
}