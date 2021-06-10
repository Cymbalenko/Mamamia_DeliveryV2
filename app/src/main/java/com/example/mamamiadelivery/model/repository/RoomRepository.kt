package com.example.mamamiadelivery.model.repository

import androidx.lifecycle.LiveData
import androidx.room.Query
import androidx.room.Room
import com.example.mamamiadelivery.acra.MamamiaDeliveryApplication
import com.example.mamamiadelivery.model.db.OrdersDatabase
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

object RoomRepository {
    private val db: OrdersDatabase = createDatabase()

    fun insert(orders: DeliveryOrders): Single<Long> {
        return db.deliveryOrdersDao.insert(orders)
    }

    fun update(orders: DeliveryOrders): Completable {
        return db.deliveryOrdersDao.update(orders)
    }

    fun deleteById(id: Int): Completable {
        return db.deliveryOrdersDao.deleteById(id)
    }

    fun deleteByOrderId(no: String): Completable{
        return db.deliveryOrdersDao.deleteByOrderId(no)
    }

    fun getAllDeliveryOrders(): LiveData<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getAllDeliveryOrders()
    }

    fun getContactById(id: Int): Single<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getDeliveryOrderById(id)
    }

    fun getContactByOrderId(id: String): Single<List<DeliveryOrders>> {
        return db.deliveryOrdersDao.getDeliveryOrderByOrderId(id)
    }

    fun getDeliveryOrdersToday(): LiveData<List<DeliveryOrders>>{
        return db.deliveryOrdersDao.getDeliveryOrdersToday()
    }


    fun getDeliveryOrdersHistory(): LiveData<List<DeliveryOrders>>{
        //return db.deliveryOrdersDao.getDeliveryOrdersHistory()
        return db.deliveryOrdersDao.getAllDeliveryOrders()
    }

    fun updateDeliveredOrder(order: DeliveryOrders):Completable{
         order.let {
             it.delivered = "1"
         }
        return db.deliveryOrdersDao.update(order)
    }
    fun updateCanDeliveredOrder(order: DeliveryOrders):Completable{
        order.let {
            it.delivered = "0"
        }
        return db.deliveryOrdersDao.update(order)
    }
    private fun createDatabase(): OrdersDatabase {
        return Room.databaseBuilder(
            MamamiaDeliveryApplication.instance!!,
            OrdersDatabase::class.java,
            OrdersDatabase.NAME
        ).build()
    }
}