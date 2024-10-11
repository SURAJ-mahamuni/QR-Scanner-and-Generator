package com.study.qrscanner.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.Result
import com.study.qrscanner.database.dao.QrHistoryDao
import com.study.qrscanner.utils.AppEvent
import com.study.qrscanner.model.QR_FORMATE
import com.study.qrscanner.model.QrDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class QrDetailsViewModel @Inject constructor(private val qrHistoryDao: QrHistoryDao) : ViewModel() {

    var _appEvent: MutableLiveData<AppEvent> = MutableLiveData()

    val appEvent: LiveData<AppEvent> get() = _appEvent

    var qrDetails = MutableLiveData<QrDetails>()

    fun setDetails(result: Result) {
        result.barcodeFormat
        val format = when {
            result.text.contains("smsto") -> {
                QR_FORMATE.SMS
            }

            result.text.contains("://") || result.text.contains("http") -> {
                QR_FORMATE.URL
            }

            result.text.contains("VCARD") -> {
                QR_FORMATE.CONTACT
            }

            result.text.contains("mailto") -> {
                QR_FORMATE.MAIL
            }

            result.text.contains("tel") -> {
                QR_FORMATE.PHONE
            }

            else -> {
                QR_FORMATE.TEXT
            }
        }
        val date = getDate()
        try {
            val data = if (result.text.contains("VCARD")) {
                result.text.replace(":", " : ").removeRange(0, 28).replace(";", " ")
            } else {
                result.text
            }
            val qrData = QrDetails(qrData = data, format = format, date = date)
            qrDetails.postValue(qrData)
            addInHistory(qrData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    var isFavorite = false

    private fun addInHistory(qrData: QrDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            qrHistoryDao.addQrDetails(qrData)
        }
    }

    private fun getDate(): String {
        // Define the date format
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy'\n'h:mm a", Locale.ENGLISH)
        // Format the date
        return dateFormat.format(Date())
    }

    fun favoriteOnClick() {
        _appEvent.postValue(AppEvent.Other("Favorite"))
    }

    fun shareOnClick() {
        _appEvent.postValue(AppEvent.Other("Share"))
    }

    fun copyOnClick() {
        _appEvent.postValue(AppEvent.Other("Copy"))
    }

    fun updateQrDetails() {
        viewModelScope.launch(Dispatchers.IO) {
            qrHistoryDao.updateQrDetails(
                QrDetails(
                    id = qrDetails.value?.id ?: 0,
                    qrData = qrDetails.value?.qrData ?: "",
                    format = qrDetails.value?.format ?: QR_FORMATE.TEXT,
                    isFavorite = isFavorite,
                    date = qrDetails.value?.date ?: ""
                )
            )
        }
    }

    fun saveContact(){
        _appEvent.postValue(AppEvent.Other("SaveContact"))
    }

    fun makeCall(){
        _appEvent.postValue(AppEvent.Other("MakeCall"))
    }

    fun openUrl(){
        _appEvent.postValue(AppEvent.Other("OpenUrl"))
    }

    fun openMail(){
        _appEvent.postValue(AppEvent.Other("OpenMail"))
    }

}