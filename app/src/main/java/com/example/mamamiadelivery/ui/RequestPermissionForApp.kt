package com.example.mamamiadelivery.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.ui.settings.Setting.versionSDK

object RequestPermissionForApp {
    @RequiresApi(api = Build.VERSION_CODES.M)
    fun RequestPermissionForWriteExternalStorage() {
        if (versionSDK >= 23) {
            val REQUEST_CODE_CONTACT = 101
            println("===== permissions =====  ")
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            for (str in permissions) {
                if (MamamiaDeliveryApplication.instance?.checkSelfPermission(str) !== PackageManager.PERMISSION_GRANTED) {
                    //
                     ActivityCompat.requestPermissions(MamamiaDeliveryApplication.instance?.applicationContext as Activity,permissions, REQUEST_CODE_CONTACT)
                    return
                } else {
                    //
                }
            }
        }
    }
}
