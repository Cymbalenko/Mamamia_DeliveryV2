package com.example.mamamiadelivery.timer

import android.os.Handler
import com.example.mamamiadelivery.Connections.ConnectionData
import com.example.mamamiadelivery.ui.main.MainActivity
import com.example.mamamiadelivery.ui.settings.Setting.progressView

class ProgressTimer {
    //#########################################################################
    fun startProgressTimer() {
        counter = 0
        startTimer()
    }

    //#########################################################################
    fun startTimer() {
        val handler = Handler()
        val r = Runnable { isProgressTimer }
        handler.postDelayed(r, 250) //таймер в милисекундах
    }// начинает видно с 10

    //#########################################################################
    private val isProgressTimer: Unit
        private get() {
            counter++
            if (counter < 240) {
                startTimer()
            }
            if (counter < 21 || // начинает видно с 10
                counter == 24 || counter == 30 || counter == 38 || counter == 44 || counter == 60 || counter == 76 || counter == 100 || counter == 140 || counter == 192
            ) {
                println("progressView(1)")
                progressView(1)
            }
            if (ConnectionData.readedAuthCode) {
                ConnectionData.readedAuthCode = false
                var code = Integer.toString(ConnectionData.AuthCode)
                if (ConnectionData.AuthCode == 200) {
                    code = "OK"
                }
                //MainActivity.logView("Auth:$code", false)
            }
            if (ConnectionData.readedSoapCode) {
                ConnectionData.readedSoapCode = false
                var code = Integer.toString(ConnectionData.SoapCode)
                if (ConnectionData.SoapCode == 200) {
                    code = "OK"
                }
                //MainActivity.logView("Soap:$code", false)
            }
            if (ConnectionData.readedSoapContentLenth) {
                ConnectionData.readedSoapContentLenth = false
                val len = Integer.toString(ConnectionData.SoapContentLenth)
                //MainActivity.logView("Bytes:$len", false)
            }
        }

    //#########################################################################
    fun stopProgressTimer() {
        counter = 241
    } //#########################################################################

    companion object {
        private var counter = 0
    }
}
