package com.example.mamamiadelivery.ui.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Handler
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders
import com.example.mamamiadelivery.properties.AppProperties
import com.example.mamamiadelivery.ui.ConnectToServer
import com.example.mamamiadelivery.ui.ResponseXmlParser
import java.text.SimpleDateFormat
import java.util.*

object Setting {
    var isFirstStart = true
    var isFirstStartTimerBroadCastReceiver = true
    val msgTimeFormat = SimpleDateFormat("HH:mm:ss")
    val uiHandler = Handler()
    var versionSDK = 0

    // элементы строки версии и обновления
    var versionText: TextView? = null
    var versionUpdateText: TextView? = null
    var btnUpdate: Button? = null

    // элементы строки состояния
    var LLStateGpsService: LinearLayout? = null
    var LLStateControlService: LinearLayout? = null
    var textIsEnabledGPS: TextView? = null
    var textIsLocationEnabled: TextView? = null
    var textIsInternet: TextView? = null
    var textLocationLatitude: TextView? = null
    var textLocationLongitude: TextView? = null

    // элементы строки лога
    var MAX_LINE = 30
    var log_view: EditText? = null
    var state_progress = 0

    // элементы кнопки обновить и прогресс обновления
    var ll_wrap_progress: LinearLayout? = null
    var btn_progress: Button? = null
    var progressButtonMaxWidth = 0
    var progressInPix = 0

    // остальные переменные
    var latitude = 180.0
    var longitude = 180.0
    var latitude_round = 180.0
    var longitude_round = 180.0
    var provider: String? = "none"
    var lastDateTimeLocation = Date()
    fun progressView(progress: Int) {
        if (progress == 0) {
            Setting.state_progress = 0
            Setting.btn_progress?.width = 10
        }
        if (progress == 1) {
            Setting.state_progress++
            Setting.progressInPix = Setting.progressButtonMaxWidth * Setting.state_progress / 40
            Setting.btn_progress?.width = Setting.progressInPix
        }
        if (progress >= 100) {
            Setting.state_progress = 40
            Setting.progressInPix = Setting.progressButtonMaxWidth
            Setting.btn_progress?.width = Setting.progressInPix
        }
    }
    fun progressState(err: Boolean) {
        if (err) {
            Setting.btn_progress!!.setBackgroundColor(Color.rgb(255, 128, 128))
        } else {
            Setting.btn_progress!!.setBackgroundColor(Color.rgb(119, 170, 255))
        }
    }

    fun viewToast(msg: String?, txt_col: Int, bg_col: Int) {
        val toast = Toast.makeText(MamamiaDeliveryApplication.instance, msg, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 100)
        toast.view!!.setBackgroundColor(bg_col)
        val text = toast.view!!.findViewById<View>(android.R.id.message) as TextView
        text.setTextColor(txt_col)
        text.gravity = Gravity.CENTER
        text.setPadding(10, 10, 10, 10)
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        //text.setTypeface(null, Typeface.BOLD);
        toast.show()
    }
    fun logView(msg: String, new_row: Boolean) {
        try {
            Setting.log_view?.lineCount
        } // если нет доступа к экрану отбражения
        catch (e: Exception) { // то выход, чтоб не подвешивало процесс, не имеющий доступ и не валило приложение
            println("-Error- logView")
            return
        }

        // удаляем верхнюю(первую) линию, если много (>MAX_LINE) строк
        val excessLineNumber = Setting.log_view!!.lineCount - Setting.MAX_LINE
        if (excessLineNumber > 0) {
            val charSequence: CharSequence = Setting.log_view!!.text
            if (excessLineNumber > 0) {
                val ss =
                    charSequence.toString().split("\n".toRegex(), excessLineNumber.coerceAtLeast(0))
                        .toTypedArray()
                Setting.log_view!!.setText(ss[excessLineNumber - 1])
            } else {
                Setting.log_view!!.setText("")
            }
        }
        if (new_row) {
            Setting.log_view?.append("\n")
        }
        Setting.log_view?.append(" -> $msg")
        Setting.log_view?.refreshDrawableState()
    }
    fun ParseReportResponse(xml_response: String?) {
        val deliveryOrderList: ArrayList<DeliveryOrders> =
            ResponseXmlParser.parse(xml_response.toString()) as ArrayList<DeliveryOrders> // чисто заглушка для вызова парсера
        if (ResponseXmlParser.responseCode.compareTo("0000") !== 0) {
            uiHandler?.post {
                progressState(true)
                logView("Сервер выдал код ошибки: " + ResponseXmlParser.responseCode, false)
                logView("Текст ошибки: " + ResponseXmlParser.responseText, true)
                logView("Перепроверьте настройки соединения и редиректа!", true)
            }
        }
    }
    fun isSendDeliveryReport(statusOk: Boolean) {
        if (statusOk) {
            val connectToServer = ConnectToServer()
            connectToServer.deliveryOrderList()
        }
    }
    fun setVersionLayoutContent() {
        getAppVersion()
        versionText?.setText(AppProperties.currentAppVersion)
        if (AppProperties.isUpdateAvailable) {
            btnUpdate?.text = "ОБНОВИТЬ"
            val color_update_available = Color.parseColor("#BBFFBB")
            btnUpdate?.background?.mutate()?.colorFilter =
                PorterDuffColorFilter(color_update_available, PorterDuff.Mode.SRC)
            versionUpdateText?.text = " Доступно обновл верс." + AppProperties.availableAppVersion
        } else {
            versionUpdateText?.text = " Нет доступных обновл. "
            btnUpdate!!.text = "ПРОВЕРИТЬ"
        }
    }
    fun getAppVersion() {
        try {
            val pinfo = MamamiaDeliveryApplication.instance?.applicationContext?.packageName?.let { MamamiaDeliveryApplication.instance?.packageManager?.getPackageInfo(it, 0) }
            AppProperties.currentAppVersion = pinfo?.versionName.toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }
}