package com.example.mamamiadelivery.ui

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Button
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication

 public class OnClickListenerDialPhone {
     public fun getOnClickDial(button: Button?): View.OnClickListener {
        return View.OnClickListener { v ->
            val phone_no = v.tag as String
            val phone_number = "tel:$phone_no"
            val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse(phone_number))
            MamamiaDeliveryApplication.instance?.startActivity(callIntent)
        }
    }
}
