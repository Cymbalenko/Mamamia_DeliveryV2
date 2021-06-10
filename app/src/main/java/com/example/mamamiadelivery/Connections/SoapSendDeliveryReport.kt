package com.example.mamamiadelivery.Connections

import android.preference.PreferenceManager
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.timer.ProgressTimer
import com.example.mamamiadelivery.ui.settings.Setting
import com.example.mamamiadelivery.ui.settings.Setting.ParseReportResponse
import com.example.mamamiadelivery.ui.settings.Setting.isSendDeliveryReport
import com.example.mamamiadelivery.ui.settings.Setting.lastDateTimeLocation
import com.example.mamamiadelivery.ui.settings.Setting.logView
import com.example.mamamiadelivery.ui.settings.Setting.progressState
import com.example.mamamiadelivery.ui.settings.Setting.progressView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.ksoap2.SoapEnvelope
import org.ksoap2.SoapFault
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import java.util.*

class SoapSendDeliveryReport (){
    var isSuccessSoapSession = false
    private var url = ""
    private var user: String? = ""
    private var pw: String? = ""
    private var redirect: String? = ""
    private var redirect_user: String? = ""
    private var redirect_key: String? = ""
    private var restaurant: String? = ""
    private var driver: String? = ""
    private var latitude = ""
    private var longitude = ""
    private var elapsed_sec = ""
    private var provider = ""
    private var curr_time: Date? = null
    private var responseXML = ""
    private var orderNo = ""
    private var deliveryStatus = ""
    private var status_ok = false
    private val namespace = "urn:microsoft-dynamics-schemas/codeunit/RetailWebServices"
    private val soap_action = "WebRequest"
    private val progressTimer: ProgressTimer = ProgressTimer()
    private var deliv_status=""

    fun run(order:String?, status:String?)  {
        Single.fromCallable{
             order.let {orderNo  }
             status.let { deliv_status }
            if (deliv_status?.compareTo("1") == 0) {
                deliveryStatus = "Delivered"
            }
            if (deliv_status?.compareTo("0") == 0) {
                deliveryStatus = "UnDelivered"
            }
            return@fromCallable if (soapSession()) {
                isSuccessSoapSession = true
                null
            } else {
                isSuccessSoapSession = false
                null
            }
        }.doOnSubscribe { println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            progressView(0)
            progressState(false)
            val preferences = PreferenceManager.getDefaultSharedPreferences(MamamiaDeliveryApplication.instance)
            val address = preferences.getString("address", "--")
            val port = preferences.getString("port", "--")
            val instance = preferences.getString("instance", "--")
            val companyname = preferences.getString("companyname", "--")
            restaurant = preferences.getString("restaurant", "--")
            driver = preferences.getString("driver", "--")
            Setting.latitude =  Setting.latitude
            Setting.longitude =  Setting.longitude
            curr_time = Date()
            elapsed_sec =
                Integer.toString((curr_time!!.time - lastDateTimeLocation.getTime()) as Int / 1000)
            Setting.provider = Setting.provider
            url = "http://$address:$port/$instance/WS/$companyname/Codeunit/RetailWebServices"
            user = preferences.getString("login", "--")
            pw = preferences.getString("password", "--")
            redirect = preferences.getString("redirect", "")
            redirect_user = preferences.getString("redirect_user", "")
            redirect_key = preferences.getString("redirect_key", "")
            logView("Отправляем отчет на сервер", true)
            progressTimer.startProgressTimer() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                progressTimer.stopProgressTimer()
                println("=== RESULT ===")
                println(responseXML)
                println("==============")
                isSendDeliveryReport(status_ok)
            }, {

            })
        // проверка отправленны ли статусы водителя
        // TODO

        // проверка новых заказов
        // TODO

    }



    //#####################################################################
    private fun soapSession(): Boolean {
        return try {
            val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
            envelope.dotNet = true
            val request = createSoapRequest()
            println("===== REQUEST ===== $request")
            envelope.setOutputSoapObject(request)
            val ntlm = NtlmTransport_()
            ntlm.debug = true
            ntlm.setCredentials(url, user, pw, "", "")

            ntlm.call(soap_action, envelope)
            if (envelope.bodyIn is SoapFault) {
                val str = (envelope.bodyIn as SoapFault).faultstring
                println(str)
                false
            } else {
                val resultsRequestSOAP = envelope.bodyIn as SoapObject
                val resp = resultsRequestSOAP.getProperty("pxmlResponse").toString()
                println("response ===== $resp")
                responseXML = resp
                ParseReportResponse(resp)
                status_ok = true
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.toString())
            false
        }
    }

    //#####################################################################
    private fun createSoapRequest(): SoapObject {
        val requestRequest = SoapObject(namespace, soap_action)
        requestRequest.addProperty(
            "pxmlRequest", "" +
                    "<Request>" +
                    "<Request_ID>WWW_SEND_DELIVERY_REPORT</Request_ID>" +
                    "<Redirect>" + redirect + "</Redirect>" +
                    "<Redirect_User>" + redirect_user + "</Redirect_User>" +
                    "<Redirect_Key>" + redirect_key + "</Redirect_Key>" +
                    "<Request_Body>" +
                    "<Request_Type>" + deliveryStatus + "</Request_Type>" +
                    "<Order_No>" + orderNo + "</Order_No>" +
                    "<Driver_ID>" + driver + "</Driver_ID>" +
                    "<Latitude_Closed>" + latitude + "</Latitude_Closed>" +
                    "<Longitude_Closed>" + longitude + "</Longitude_Closed>" +
                    "<Closed_Location_Elapsed_sec>" + elapsed_sec + "</Closed_Location_Elapsed_sec>" +
                    "<Closed_Location_Provider>" + provider + "</Closed_Location_Provider>" +
                    "</Request_Body>" +
                    "</Request>"
        )
        requestRequest.addProperty("pxmlResponse", "")
        return requestRequest
    } //#####################################################################



}