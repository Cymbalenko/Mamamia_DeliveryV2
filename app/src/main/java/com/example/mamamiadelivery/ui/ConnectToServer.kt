package com.example.mamamiadelivery.ui

import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.example.mamamiadelivery.Connections.SoapGetDeliveryOrderList
import com.example.mamamiadelivery.Connections.SoapSendDeliveryReport
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.properties.AppProperties
import com.example.mamamiadelivery.ui.settings.Setting.lastDateTimeLocation
import com.example.mamamiadelivery.ui.settings.Setting.latitude
import com.example.mamamiadelivery.ui.settings.Setting.longitude
import java.util.*

  class ConnectToServer {
    fun deliveryOrderList(){
        val soapGetDeliveryOrderList = SoapGetDeliveryOrderList()
            soapGetDeliveryOrderList.run()
        }

    fun setDeliveryStatusToOrder(order: String, status: String) {
        repOrder = order
        repStatus = status
        if (status.compareTo("1") == 0) {
            val elapsed_sec =
                (Date().time - lastDateTimeLocation.getTime()) as Int / 1000
            val builder = MamamiaDeliveryApplication.instance?.applicationContext?.let { AlertDialog.Builder(it) }
            if (!AppProperties.isGpsEnabled || !AppProperties.isLocationEnabled) { // не включена геолокация
                val confirmText =
                    "Геолокация не включена. Координаты некорректные. Уверены что хотите отправить отчет о доставке с такими координатами?"
                builder?.setMessage(confirmText)?.setPositiveButton("Да", dialogClickListener)
                    ?.setNegativeButton("Нет", dialogClickListener)?.show()
                return
            }
            if (latitude === 180.0 && longitude === 180.0) { // координаты не инициализированы
                val confirmText =
                    "GPS-координаты не инициалирированы и равны 180. Уверены что хотите отправить отчет о доставке с такими координатами?"
                builder?.setMessage(confirmText)?.setPositiveButton("Да", dialogClickListener)
                    ?.setNegativeButton("Нет", dialogClickListener)?.show()
                return
            }
            if (latitude === 0.0 && longitude === 0.0) { // координаты временно пустые
                val confirmText =
                    "GPS-координаты временно равны 0. Уверены что хотите отправить отчет о доставке с такими координатами, или подождете менее минуты?"
                builder?.setMessage(confirmText)?.setPositiveButton("Да", dialogClickListener)
                    ?.setNegativeButton("Нет", dialogClickListener)?.show()
                return
            }
            if (elapsed_sec > 300) {  // координаты неактуальны более 5 мин
                val confirmText =
                    "GPS-координаты не актуальны более 5 минут. Уверены что хотите отправить отчет о доставке с такими координатами?"
                builder?.setMessage(confirmText)?.setPositiveButton("Да", dialogClickListener)
                    ?.setNegativeButton("Нет", dialogClickListener)?.show()
                return
            }
        } else {
            startSetDeliveryStatusToOrder(repOrder, repStatus)
        }
    }

    private fun startSetDeliveryStatusToOrder(order: String?, status: String?) {
        val soapSendDeliveryReport = SoapSendDeliveryReport()
        soapSendDeliveryReport.run(order, status)
    }

    var dialogClickListener =
        DialogInterface.OnClickListener { _, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> startSetDeliveryStatusToOrder(
                        repOrder,
                        repStatus
                )
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }

    companion object {
        var sendReport = false
        var repOrder: String? = null
        var repStatus: String? = null
    }
}