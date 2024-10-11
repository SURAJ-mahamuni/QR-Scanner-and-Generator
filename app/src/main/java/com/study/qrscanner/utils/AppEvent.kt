package com.study.qrscanner.utils

sealed class AppEvent() {
    data class ToastEvent(val message: Int) : AppEvent()
    data class NavigateFragmentEvent(val screenID: Int?) : AppEvent()
    data class NavigateActivityEvent(val screenID: String?) : AppEvent()
    data class NavigateBackEvent(val message: String) : AppEvent()
    data class ImagePickEvent(val message: String) : AppEvent()
    data class OpenDialog(val message: String) : AppEvent()
    data class Other(val message: String) : AppEvent()
}