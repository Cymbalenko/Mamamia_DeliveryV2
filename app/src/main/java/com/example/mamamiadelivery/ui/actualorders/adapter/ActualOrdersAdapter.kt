package com.example.mamamiadelivery.ui.actualorders.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mamamiadelivery.R
import com.example.mamamiadelivery.model.db.entity.DeliveryOrders

class ActualOrdersAdapter(private val clickListener: (DeliveryOrders) -> Unit
) : ListAdapter<DeliveryOrders, ActualOrdersAdapter.OrdersViewHolder>(OrdersDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_actual_orders_item, parent, false)
        return OrdersViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        getItem(position)?.let { order ->
            holder.bind(order)
        }
    }

    class OrdersViewHolder(view: View, private val clickListener: (DeliveryOrders) -> Unit) : RecyclerView.ViewHolder(view) {

        fun bind(orders: DeliveryOrders) {
            val textViewAdress = itemView.findViewById<TextView>(R.id.text_view_adress_actual)
            val textViewPhone = itemView.findViewById<TextView>(R.id.text_view_phone_actual)
            val textViewName = itemView.findViewById<TextView>(R.id.text_view_name_actual)
            val textViewOrderId = itemView.findViewById<TextView>(R.id.text_view_orderdId_actual)
            val textViewAPay = itemView.findViewById<TextView>(R.id.text_view_pay_actual)

            textViewAdress.text = orders.address
            textViewPhone.text = orders.phone_no
            textViewName.text = orders.name_
            textViewOrderId.text = orders.order_no
            textViewAPay.text = orders.total_amount
            //textViewAdress.setOnClickListener { clickListener(orders) }
        }

    }

    class OrdersDiffUtil : DiffUtil.ItemCallback<DeliveryOrders>() {
        override fun areItemsTheSame(oldItem: DeliveryOrders, newItem: DeliveryOrders): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DeliveryOrders, newItem: DeliveryOrders): Boolean {
            return oldItem == newItem
        }
    }
}