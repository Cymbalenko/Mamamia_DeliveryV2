package com.example.mamamiadelivery.acra

import android.app.Application

class MamamiaDeliveryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance=this
    }
    companion object {
        var instance: MamamiaDeliveryApplication? = null
    }
}