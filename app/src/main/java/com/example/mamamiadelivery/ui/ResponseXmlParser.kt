package com.example.mamamiadelivery.ui

import com.example.mamamiadelivery.model.db.entity.DeliveryOrders
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

object ResponseXmlParser {
    //private static final String ns = null;
    var responseCode = " "
    var responseText = " "
    fun parse(xml_response: String): ArrayList<*>? {
        val pullParserFactory: XmlPullParserFactory
        try {
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()
            val `in`: InputStream =
                ByteArrayInputStream(xml_response.toByteArray(Charset.forName("UTF-8")))
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(`in`, null)
            return readDeliveryOrder(parser)
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readDeliveryOrder(parser: XmlPullParser): ArrayList<DeliveryOrders?>? {
        var deliveryOrderList: ArrayList<DeliveryOrders?>? = null
        var currDeliveryOrder: DeliveryOrders? = null
        var order_no: String? = ""
        var driver_id: String? = ""
        var delivered: String? = ""
        var phone_no: String? = ""
        var city: String? = ""
        var address_2: String? = ""
        var address: String? = ""
        var order_taker_name: String? = ""
        var name_: String? = ""
        var directions: String? = ""
        var tender_type = ""
        var int_paym_type = 0
        var amount: String? = ""
        var contact_pickup_time: String? = ""
        var general_status: String? = ""
        var posting_time: String? = ""
        var posting_date: String? = ""
        var estimated_prod_time: String? = ""
        var created_at_call_center: String? = ""
        var restaurant_no: String? = ""
        var total_amount: String? = ""
        var cash_amount: String? = ""
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            var name: String? = null
            var isDeliveryOrder = false
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> deliveryOrderList = ArrayList<Any?>() as ArrayList<DeliveryOrders?>
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    if (name.compareTo("Response_Code") == 0) {
                        responseCode = parser.nextText()
                        println("==== responseCode = " + responseCode)
                    } else if (name.compareTo("Response_Text") == 0) {
                        responseText = parser.nextText()
                        println("==== responseText = " + responseText)
                    } else if (name.compareTo("Delivery_Order") == 0) {
                        //currDeliveryOrder = new DeliveryOrder();
                        isDeliveryOrder = true
                        order_no = ""
                        driver_id = ""
                        delivered = ""
                        phone_no = ""
                        city = ""
                        address_2 = ""
                        address = ""
                        order_taker_name = ""
                        name_ = ""
                        directions = ""
                        tender_type = ""
                        int_paym_type = 0
                        amount = ""
                        contact_pickup_time = ""
                        general_status = ""
                        posting_time = ""
                        posting_date = ""
                        estimated_prod_time = ""
                        created_at_call_center = ""
                        restaurant_no = ""
                        total_amount = ""
                        cash_amount = ""
                    } else if (name.compareTo("Order_No.") == 0) {
                        order_no = parser.nextText()
                    } else if (name.compareTo("Driver_ID") == 0) {
                        driver_id = parser.nextText()
                    } else if (name.compareTo("Delivered") == 0) {
                        delivered = parser.nextText()
                    } else if (name.compareTo("Phone_No.") == 0) {
                        phone_no = parser.nextText()
                    } else if (name.compareTo("City") == 0) {
                        city = parser.nextText()
                    } else if (name.compareTo("Address_2") == 0) {
                        address_2 = parser.nextText()
                    } else if (name.compareTo("Address") == 0) {
                        address = parser.nextText()
                    } else if (name.compareTo("Order_Taker_Name") == 0) {
                        order_taker_name = parser.nextText()
                    } else if (name.compareTo("Name") == 0) {
                        name_ = parser.nextText()
                    } else if (name.compareTo("Directions") == 0) {
                        directions = parser.nextText()
                    } else if (name.compareTo("Tender_Type") == 0) {
                        val tender_type_list = parser.getAttributeValue(0)
                        val int_tender_type = parser.nextText()
                        var index_type = 0
                        index_type = Integer.valueOf(int_tender_type)
                        if (index_type < 0) {
                            index_type = 0
                        }
                        val separated = tender_type_list.split(",".toRegex()).toTypedArray()
                        tender_type = separated[index_type]
                        int_paym_type = index_type
                        //int tender_type_cnt = parser.getAttributeCount();
                        //String tender_type_cnt_str = String.valueOf(tender_type_cnt);
                    } else if (name.compareTo("Amount_Incl._VAT") == 0) {
                        amount = parser.nextText()
                    } else if (name.compareTo("Contact_Pickup_Time") == 0) {
                        contact_pickup_time = parser.nextText()
                    } else if (name.compareTo("General_Status") == 0) {
                        general_status = parser.nextText()
                    } else if (name.compareTo("Posting_Time") == 0) {
                        posting_time = parser.nextText()
                    } else if (name.compareTo("Posting_Date") == 0) {
                        posting_date = parser.nextText()
                    } else if (name.compareTo("Estimated_Prod._Time__Min._") == 0) {
                        estimated_prod_time = parser.nextText()
                    } else if (name.compareTo("Created_at_Call_Center") == 0) {
                        created_at_call_center = parser.nextText()
                    } else if (name.compareTo("Restaurant_No.") == 0) {
                        restaurant_no = parser.nextText()
                    } else if (name.compareTo("Total_Amount") == 0) {
                        total_amount = parser.nextText()
                    } else if (name.compareTo("Cash_Amount") == 0) {
                        cash_amount = parser.nextText()
                    }
                }
                XmlPullParser.END_TAG -> {
                    name = parser.name
                    if (name.compareTo("Delivery_Order") == 0) {
                        currDeliveryOrder = DeliveryOrders(
                            order_no!!,
                            driver_id,
                            delivered,
                            phone_no!!,
                            city,
                            address_2,
                            address,
                            order_taker_name,
                            name_,
                            directions,
                            tender_type,
                            int_paym_type,
                            amount,
                            contact_pickup_time,
                            general_status,
                            posting_time,
                            posting_date,
                            estimated_prod_time,
                            created_at_call_center,
                            restaurant_no,
                            total_amount,
                            cash_amount
                        )
                        deliveryOrderList!!.add(currDeliveryOrder)
                        isDeliveryOrder = false
                    }
                }
            }
            eventType = parser.next()
        }
        return deliveryOrderList
    }
}
