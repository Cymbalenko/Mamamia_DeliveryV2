package com.example.mamamiadelivery.ui

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset
import java.util.*

object ResponseXmlParser_RespCode {
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
            readResponse(parser)
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readResponse(parser: XmlPullParser) {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            var name: String? = null
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> {
                }
                XmlPullParser.START_TAG -> {
                    name = parser.name
                    if (name.compareTo("Response_Code") == 0) {
                        responseCode = parser.nextText()
                        println("==== responseCode = " + responseCode)
                    } else if (name.compareTo("Response_Text") == 0) {
                        responseText = parser.nextText()
                        println("==== responseText = " + responseText)
                    }
                }
                XmlPullParser.END_TAG -> {
                }
            }
            eventType = parser.next()
        }
    }
}
