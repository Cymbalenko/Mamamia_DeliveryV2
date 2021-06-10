package com.example.mamamiadelivery.Connections

object ConnectionData {
    var AuthCode = 0
    var SoapCode = 0
    var SoapContentLenth = 0
    var receivedBytes = 0
    var readedAuthCode = false
    var readedSoapCode = false
    var readedSoapContentLenth = false
    fun init() {
        AuthCode = 0
        SoapCode = 0
        SoapContentLenth = 0
        readedAuthCode = false
        readedSoapCode = false
        readedSoapContentLenth = false
        receivedBytes = 0
    }
}
