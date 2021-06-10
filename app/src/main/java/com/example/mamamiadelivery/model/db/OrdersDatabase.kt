package com.example.mamamiadelivery.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mamamiadelivery.model.db.dao.DeliveryOrdersDao
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders

@Database(entities = [DeliveryOrders::class], version = 1)
abstract class OrdersDatabase : RoomDatabase() {

        companion object {
            const val NAME = "delivery_orders_db"
        }

        abstract val deliveryOrdersDao: DeliveryOrdersDao
}
