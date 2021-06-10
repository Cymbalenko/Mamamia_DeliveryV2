package com.example.mamamiadelivery.model.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeliveryOrders(
    var order_no: String,
    var driver_id: String? = null,
    var delivered: String? = null,
    var phone_no: String,
    var city: String? = null,
    var address_2: String? = null,
    var address: String? = null,
    var order_taker_name: String? = null,
    var name_: String? = null,
    var directions: String? = null,
    var tender_type: String? = null,
    var int_paym_type: Int = 0,
    var amount: String? = null,
    var contact_pickup_time: String? = null,
    var general_status: String? = null,
    var posting_time: String? = null,
    var posting_date: String? = null,
    var estimated_prod_time: String? = null,
    var created_at_call_center: String? = null,
    var restaurant_no: String? = null,
    var total_amount: String? = null,
    var cash_amount: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}