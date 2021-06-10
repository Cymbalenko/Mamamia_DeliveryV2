package com.example.mamamiadelivery

import android.Manifest
import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*


class MyLocationService : Service() {
    private val mHandler = Handler()
    private var mTimer: Timer? = null
    var notify_interval: Long = 30000 // 30 сек
    private var mLocationManager: LocationManager? =null
    var intent: Intent? = null
    var locServContext: Context? = null

    inner class LocationListener(provider: String) : android.location.LocationListener {
        var mLastLocation: Location
        override fun onLocationChanged(location: Location) {
            Log.e(TAG, "onLocationChanged: $location")
            println("=== Latitude " + java.lang.Double.toString(location.latitude) + "")
            //System.out.println("=== Longitude "+Double.toString(location.getLongitude())+"");
            //intent = new Intent(str_gps_receiver);
            intent?.putExtra("timer", "0")
            intent?.putExtra("status", "0")
            intent?.putExtra("location", "1")
            intent?.putExtra("provider", location.provider)
            intent?.putExtra("latitude", java.lang.Double.toString(location.latitude))
            intent?.putExtra("longitude", java.lang.Double.toString(location.longitude))
            sendBroadcast(intent)
            mLastLocation.set(location)
        }

        override fun onProviderDisabled(provider: String) {
            Log.e(
                TAG,
                "========================== onProviderDisabled: $provider"
            )
            if (provider.equals("gps", ignoreCase = true)) {
                Log.e(TAG, "===== gps =====")
                is_gps_enabled = false
                intent?.putExtra("timer", "0")
                intent?.putExtra("status", "1")
                intent?.putExtra("location", "0")
                intent?.putExtra("gps_enabled", "0")
                if (isLocationServicesAvailable(locServContext)) {
                    intent?.putExtra("is_location_enabled", "1")
                } else {
                    intent?.putExtra("is_location_enabled", "0")
                }
                sendBroadcast(intent)
            }
        }

        override fun onProviderEnabled(provider: String) {
            Log.e(
                TAG,
                "========================== onProviderEnabled: $provider"
            )
            if (provider.equals("gps", ignoreCase = true)) {
                Log.e(TAG, "===== gps =====")
                is_gps_enabled = true
                intent?.putExtra("timer", "0")
                intent?.putExtra("status", "1")
                intent?.putExtra("location", "0")
                intent?.putExtra("gps_enabled", "1")
                if (isLocationServicesAvailable(locServContext)) {
                    intent?.putExtra("is_location_enabled", "1")
                } else {
                    intent?.putExtra("is_location_enabled", "0")
                }
                sendBroadcast(intent)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.e(
                TAG,
                "========================== onStatusChanged: $provider"
            )
        }

        init {
            Log.e(TAG, "LocationListener $provider")
            mLastLocation = Location(provider)
        }
    }

    var mLocationListeners = arrayOf(
        LocationListener(LocationManager.GPS_PROVIDER),
        LocationListener(LocationManager.NETWORK_PROVIDER)
    )


    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    //#####################################################################
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "=== GPS SERVICE Started")
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }

    //#####################################################################
    override fun onCreate() {
        Log.e(TAG, "onCreate")
        initializeLocationManager()
        intent = Intent(str_gps_receiver)
        mTimer = Timer()
        mTimer?.schedule(TimerTaskToGetLocation(), 5, notify_interval)
        locServContext = this
        try {
            mLocationManager?.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "network provider does not exist, " + ex.message)
        }
        try {
            mLocationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE,
                mLocationListeners[1]
            )
        } catch (ex: SecurityException) {
            Log.i(TAG, "fail to request location update, ignore", ex)
        } catch (ex: IllegalArgumentException) {
            Log.d(TAG, "gps provider does not exist " + ex.message)
        }
    }

    //#####################################################################
    override fun onDestroy() {
        Log.e(TAG, "onDestroy")
        super.onDestroy()
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) !== PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    mLocationManager?.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.i(TAG, "fail to remove location listener, ignore", ex)
                }
            }
        }
    }

    //#####################################################################
    private fun initializeLocationManager() {
        Log.e(
            TAG,
            "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE
        )
        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(LOCATION_SERVICE) as LocationManager
        }
    }

    //#####################################################################
    private inner class TimerTaskToGetLocation : TimerTask() {
        override fun run() {
            mHandler.post {
                Log.e(
                    TAG,
                    "======================= GPS SERVICE Timer ===================="
                )
                intent?.putExtra("timer", "1")
                intent?.putExtra("status", "1")
                intent?.putExtra("location", "0")
                if (isLocationServicesAvailable(locServContext)) {
                    intent?.putExtra("is_location_enabled", "1")
                } else {
                    intent?.putExtra("is_location_enabled", "0")
                }
                if (is_gps_enabled) {
                    intent?.putExtra("gps_enabled", "1")
                } else {
                    intent?.putExtra("gps_enabled", "0")
                }
                sendBroadcast(intent)
            }
        }
    }

    companion object {
        private const val TAG = "MyLocationService"
        var str_gps_receiver = "mamamia.my_location_service.gps_receiver"
        private const val LOCATION_INTERVAL = 5000
        private const val LOCATION_DISTANCE = 1f
        var is_gps_enabled = true // по умолчанию включен

        //#####################################################################
        @SuppressLint("ObsoleteSdkInt")
        fun isLocationEnabled(context: Context): Boolean {
            var locationMode = 0
            var locationProviders: String = ""
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                locationMode = try {
                    Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                    return false
                }
                locationMode != Settings.Secure.LOCATION_MODE_OFF
            } else {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                    locationProviders = Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED
                    )
                }
                !TextUtils.isEmpty(locationProviders)
            }
        }

        fun isLocationServicesAvailable(context: Context?): Boolean {
            var locationMode = 0
            var locationProviders: String = ""
            var isAvailable = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // > API 19
                try {
                    locationMode = Settings.Secure.getInt(
                        context!!.contentResolver,
                        Settings.Secure.LOCATION_MODE
                    )
                } catch (e: SettingNotFoundException) {
                    e.printStackTrace()
                }
                isAvailable = locationMode != Settings.Secure.LOCATION_MODE_OFF
                println("=== isLocServAvailable = $isAvailable === ")
            } else { // < API 19
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                    locationProviders = Settings.Secure.getString(
                        context!!.contentResolver,
                        Settings.Secure.LOCATION_PROVIDERS_ALLOWED
                    )
                }
                isAvailable = !TextUtils.isEmpty(locationProviders)
                println("=== isLocServAvailable = $isAvailable === ")
            }
             return isAvailable
        } //#####################################################################
    }
}
