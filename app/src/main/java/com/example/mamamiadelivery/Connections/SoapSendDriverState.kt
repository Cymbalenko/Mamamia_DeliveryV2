package com.example.mamamiadelivery.Connections

import android.graphics.Color
import android.os.AsyncTask
import android.preference.PreferenceManager
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.properties.AppProperties
import com.example.mamamiadelivery.timer.ProgressTimer
import com.example.mamamiadelivery.ui.main.MainActivity
import com.example.mamamiadelivery.ui.settings.Setting
import com.example.mamamiadelivery.ui.settings.Setting.ParseReportResponse
import com.example.mamamiadelivery.ui.settings.Setting.logView
import com.example.mamamiadelivery.ui.settings.Setting.progressState
import com.example.mamamiadelivery.ui.settings.Setting.progressView
import com.example.mamamiadelivery.ui.settings.Setting.viewToast
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.ksoap2.SoapEnvelope
import org.ksoap2.SoapFault
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import java.util.*

class SoapSendDriverState (){
    var isSuccessSoapSession = false
    private var url = ""
    private var user: String? = ""
    private var pw: String? = ""
    private var redirect = ""
    private var redirect_user = ""
    private var redirect_key = ""
    private var restaurant: String? = ""
    private var driver: String? = ""
    private var latitude = ""
    private var longitude = ""
    private val elapsed_sec = ""
    private val provider = ""
    private val curr_time: Date? = null
    private var responseXML = ""
    private val orderNo = ""
    private val deliveryStatus = ""
    private var status_ok = false
    private val namespace = "urn:microsoft-dynamics-schemas/codeunit/RetailWebServices"
    private val soap_action = "WebRequest"
    private val progressTimer: ProgressTimer = ProgressTimer()

    fun run() {
        val subscribe = Single.fromCallable({
            return@fromCallable if (soapSession()) {

                isSuccessSoapSession = true
                null
            } else {

                isSuccessSoapSession = false
                null
            }
        }).doOnSubscribe {
            val preferences = PreferenceManager.getDefaultSharedPreferences(MamamiaDeliveryApplication.instance?.applicationContext)
            val address = preferences.getString("address", "--")
            val port = preferences.getString("port", "--")
            val instance = preferences.getString("instance", "--")
            val companyname = preferences.getString("companyname", "--")
            restaurant = preferences.getString("restaurant", "--")
            driver = preferences.getString("driver", "--")
            latitude = java.lang.Double.toString(Setting.latitude)
            longitude = java.lang.Double.toString(Setting.longitude)
            url = "http://$address:$port/$instance/WS/$companyname/Codeunit/RetailWebServices"
            println("url: $url")
            user = preferences.getString("login", "--")
            pw = preferences.getString("password", "--")
            redirect = AppProperties.log_redirect // "VISAVIS_HO"
            redirect_user = AppProperties.log_redirect_user // "visavis"
            redirect_key = AppProperties.log_redirect_key // ""
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("<<< SoapSendDriverState <<< Finish")
                if (isSuccessSoapSession) {
                    AppProperties.isSendedDriverState = true
                }
            }, {


            })
    }
    //#####################################################################




    //#####################################################################
    private fun soapSession(): Boolean {
        return try {
            val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
            envelope.dotNet = true
            val request = greateSoapRequest()
            println("===== REQUEST0 ===== $request")
            envelope.setOutputSoapObject(request)
            println("===== REQUEST1 ===== $request")
            val ntlm = NtlmTransport_()
            println("===== REQUEST2 ===== $request")
            ntlm.debug = true
            ntlm.setCredentials(url, user, pw, "", "")
            ntlm.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
            //ntlm.debug = true;//NEW ADDED
            println("===== ntlm ===== $ntlm")
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
    private fun greateSoapRequest(): SoapObject {
        val requestRequest = SoapObject(namespace, soap_action)
        requestRequest.addProperty(
            "pxmlRequest", "" +
                    "<Request>" +
                    "<Request_ID>WWW_SEND_DELIV_DRIVER_STATE</Request_ID>" +
                    "<Redirect>" + redirect + "</Redirect>" +
                    "<Redirect_User>" + redirect_user + "</Redirect_User>" +
                    "<Redirect_Key></Redirect_Key>" +
                    "<Request_Body>" +
                    "<Request_Type></Request_Type>" +
                    "<Driver_ID>" + driver + "</Driver_ID>" +
                    "<Store>" + restaurant + "</Store>" +
                    "<Latitude>" + latitude + "</Latitude>" +
                    "<Longitude>" + longitude + "</Longitude>" +
                    "<On_Shift>" + AppProperties.state_OnShift + "</On_Shift>" +
                    "<In_Restaurant></In_Restaurant>" +
                    "<On_Trip></On_Trip>" +
                    "<On_Hold></On_Hold>" +
                    "<Version_App>" + AppProperties.currentAppVersion + "</Version_App>" +
                    "<Free_Text>" + AppProperties.currentAppVersion + "</Free_Text>" +
                    "</Request_Body>" +
                    "</Request>"
        )
        requestRequest.addProperty("pxmlResponse", "")
        return requestRequest
    }


}