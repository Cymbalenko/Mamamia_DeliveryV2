package com.example.mamamiadelivery

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.IBinder
import android.util.Log
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.util.HalfSerializer.onError
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.InetAddress
import java.util.*

class ControlService : Service() {
    private var mTimer: Timer? = null

    var timer_interval = (30 * 1000 // 30 сек
            ).toLong()
    var intent: Intent? = null
    var locCtrlServContext: Context? = null

    //#####################################################################
    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    //#####################################################################
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "=== CONTROL SERVICE Started")
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    //#####################################################################
    override fun onCreate() {
        intent = Intent(str_control_receiver)
        mTimer = Timer()
        mTimer?.schedule(TimerTaskToControl(), 5, timer_interval)
        locCtrlServContext = this
        intent?.putExtra("start", "1")
        intent?.putExtra("timer", "0")
        sendBroadcast(intent)
    }

    //#####################################################################
    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
    }

    //#####################################################################
    inner class TimerTaskToControl : TimerTask() {
        var isInternet = "0"
        override fun run() {
            Single.fromCallable{
                if (isNetworkConnected) {
                    println("======= isNetworkConnected ======")
                    if (isInternetAvailable) {
                        isInternet = "1"
                    }
                }
                return@fromCallable null
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        Log.e(TAG, "===================== CONTROL SERVICE AsyncTask ==================")
                        intent?.putExtra("start", "1")
                        intent?.putExtra("timer", "1")
                        intent?.putExtra("internet", isInternet)
                        println("======= internet == " + intent!!.getStringExtra("internet") + " ======")
                        sendBroadcast(intent)
                    }, {

                    })
                // проверка отправленны ли статусы водителя
                // TODO

                // проверка новых заказов
                // TODO

            }

    }

    //#####################################################################
    private val isNetworkConnected: Boolean
        private get() {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
        }
    val isInternetAvailable: Boolean
        get() = try {
            val ipAddr = InetAddress.getByName("www.google.com")
            ipAddr.toString() != ""
        } catch (e: Exception) {
            false
        }




    companion object {
        private const val TAG = "ControlService"
        var str_control_receiver = "mamamia.control_service.control_receiver"
    }
}