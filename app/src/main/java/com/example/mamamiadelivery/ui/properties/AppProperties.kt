package com.example.mamamiadelivery.properties

import java.util.*

object AppProperties {

    var isUpdateAvailable=true
    var log_redirect = "VISAVIS_HO"
    var log_redirect_user = "visavis"
    var log_redirect_key = ""
    var log_url = ""
    var isSendedDriverState = false
    var isSendedAcraReport = false
    var currentAppVersion = ".."
    var availableAppVersion = ""

    //// =========================================================
    var linkToUpdateApkFile = ""

    var isControlServiceStarted = false
    var isGpsServiceStarted = false
    var isGpsEnabled = false // только GPS
    var isLocationEnabled = false // полностью местоположение
    var isInternet = false // доступ в интернет
    var isStartCheckingUpdate: Boolean? = false   // признак запуска проверки обновления
    var isStartUpdateOrdersToDelivery =
        false // признак запуска обновления списка ордеров на доставку
    lateinit var  lastTime_CheckAppUpdate: Date; //время последней провнрки обновления

    var lastTime_UpdateOrdersToDelivery //время последнего обновления ордеров для доставки
            : Date? = null
    var state_OnShift = true // пока всегда истина - нужно при старте приложения
    var counterControlTimer = 0
    var state_Sended = false
}