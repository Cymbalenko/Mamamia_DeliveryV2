package com.example.mamamiadelivery.Connections

import android.preference.PreferenceManager
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders
import com.example.mamamiadelivery.model.repository.RoomRepository
import com.example.mamamiadelivery.timer.ProgressTimer
import com.example.mamamiadelivery.ui.ResponseXmlParser
import com.example.mamamiadelivery.ui.actualorders.ActualOrdersUiEvent
import com.example.mamamiadelivery.ui.actualorders.ActualOrdersViewModel
import com.example.mamamiadelivery.ui.settings.Setting.logView
import com.example.mamamiadelivery.ui.settings.Setting.progressState
import com.example.mamamiadelivery.ui.settings.Setting.progressView
import com.example.mamamiadelivery.ui.settings.Setting.uiHandler
import com.example.mamamiadelivery.util.LoadingState
import com.example.mamamiadelivery.util.call
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import org.ksoap2.SoapEnvelope
import org.ksoap2.SoapFault
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import java.util.*

class SoapGetDeliveryOrderList  () {
    var isSuccessHttpSession = false
    private var url = ""
    private var user: String? = ""
    private var pw: String? = ""
    private var redirect: String? = ""
    private var redirect_user: String? = ""
    private var redirect_key: String? = ""
    private var select_by: String? = ""
    private var restaurant: String? = ""
    private var driver: String? = ""
    private var responseXML = ""
    private val namespace = "urn:microsoft-dynamics-schemas/codeunit/RetailWebServices"

    private val soap_action = "WebRequest"
    fun run()  {
        Single.fromCallable {
            soapSession()
        }.doOnSubscribe {
            val preferences =
                PreferenceManager.getDefaultSharedPreferences(MamamiaDeliveryApplication.instance)
            val address = preferences.getString("address", "--")
            val port = preferences.getString("port", "--")
            val instance = preferences.getString("instance", "--")
            val companyname = preferences.getString("companyname", "--")
            select_by = preferences.getString("select_by", "--")
            restaurant = preferences.getString("restaurant", "--")
            driver = preferences.getString("driver", "--")
            url = "http://$address:$port/$instance/WS/$companyname/Codeunit/RetailWebServices"
            user = preferences.getString("login", "")
            pw = preferences.getString("password", "")
            redirect = preferences.getString("redirect", "")
            redirect_user = preferences.getString("redirect_user", "")
            redirect_key = preferences.getString("redirect_key", "")
            println("##########################################")
            println("================== login: $user")
            println("================ password: $pw")
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                println("=== RESULT ===")
                println(responseXML)
                println("==============")
            }, {
            })
    }
    private fun soapSession(): Boolean {
        return try {
            val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
            envelope.dotNet = true
            val request = greateSoapRequest()
            println("===== REQUEST ===== $request")
            envelope.setOutputSoapObject(request)
            val ntlm = NtlmTransport_()
            ntlm.debug = true
            ntlm.setCredentials(url, user, pw, "", "")
            ntlm.setXmlVersionTag("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>")
            ntlm.call(soap_action, envelope)
            if (envelope.bodyIn is SoapFault) {
                val str = (envelope.bodyIn as SoapFault).faultstring
                println("SoapFault =====$str")
                true
            } else {
                val resultsRequestSOAP = envelope.bodyIn as SoapObject
                val resp = resultsRequestSOAP.getProperty("pxmlResponse").toString()
                println("Response ===== $resp")
                responseXML = resp
                CreateDeliveryList(resp)
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println(e.toString())
            false
        }
    }
    fun CreateDeliveryList(xml_response: String?) {
         val deliveryOrderList: ArrayList<DeliveryOrders> = ResponseXmlParser.parse(xml_response.toString()) as ArrayList<DeliveryOrders>
         if (ResponseXmlParser.responseCode.compareTo("0000") !== 0) {
             uiHandler.post {
                 progressState(true)
                 logView("Сервер выдал код ошибки: " + ResponseXmlParser.responseCode, false)
                 logView("Текст ошибки: " + ResponseXmlParser.responseText, true)
                 logView("Перепроверьте настройки соединения и редиректа!", true)
             }
         }
        var mainrep:RoomRepository = RoomRepository
        deliveryOrderList.toList().forEach({
            mainrep.insert(it)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ id ->
                    println("success. id = $id")
                },{ exception ->
                    println("error: ${exception.message}")
                })
        }
        )
         // обновляем хранитель доставочных ордеров
    }
    private fun greateSoapRequest(): SoapObject {
        val requestRequest = SoapObject(namespace, soap_action)
        requestRequest.addProperty("pxmlRequest", "" +
                "<Request>" +
                "<Request_ID>WWW_GET_DELIVERY_ORDERS</Request_ID>" +
                "<Redirect>" + redirect + "</Redirect>" +
                "<Redirect_User>" + redirect_user + "</Redirect_User>" +
                "<Redirect_Key>" + redirect_key + "</Redirect_Key>" +
                "<Request_Body>" +
                "<Request_Type>" + select_by + "</Request_Type>" +
                "<Restaurant_No.>" + restaurant + "</Restaurant_No.>" +
                "<Driver_ID>" + driver + "</Driver_ID>" +
                "</Request_Body>" +
                "</Request>")
        requestRequest.addProperty("pxmlResponse", "")
        return requestRequest
    }
}
