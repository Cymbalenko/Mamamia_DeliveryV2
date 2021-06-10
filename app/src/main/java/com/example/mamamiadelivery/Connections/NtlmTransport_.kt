package com.example.mamamiadelivery.Connections
import com.example.mamamiadelivery.Connections.ConnectionData.init
import org.apache.http.HttpResponse
import org.apache.http.auth.AuthScheme
import org.apache.http.auth.AuthSchemeFactory
import org.apache.http.auth.AuthScope
import org.apache.http.auth.NTCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.impl.auth.NTLMScheme
import org.apache.http.impl.client.AbstractHttpClient
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.params.HttpParams
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.ksoap2.HeaderProperty
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.ServiceConnection
import org.ksoap2.transport.Transport
import org.xmlpull.v1.XmlPullParserException
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.*

@Suppress("UNREACHABLE_CODE")
class NtlmTransport_ : Transport() {
    private val client = DefaultHttpClient()
    private val localContext: HttpContext = BasicHttpContext()
    private var urlString: String? = null
    private var user: String? = null
    private var password: String? = null
    private var ntDomain: String? = null
    private var ntWorkstation: String? = null
    fun setCredentials(
        url: String?, user: String?, password: String?,
        domain: String?, workStation: String?
    ) {
        urlString = url
        this.user = user
        this.password = password
        ntDomain = domain
        ntWorkstation = workStation
    }

    @Throws(IOException::class, XmlPullParserException::class)
    override fun call(
        targetNamespace: String?,
        envelope: SoapEnvelope?,
        headers: List<*>?
    ): List<*>? {
        return call(targetNamespace!!, envelope!!, headers, null)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    override fun call(
        soapAction: String?,
        envelope: SoapEnvelope?,
        headers: List<*>?,
        outputFile: File?
    ): List<*>? {
        if (outputFile != null) {
            // implemented in HttpTransportSE if you are willing to port..
            throw RuntimeException("Writing to file not supported")
        }
        init() // для контроля загрузки
        var resp: HttpResponse? = null
        setupNtlm(urlString, user, password)
        try {
            // URL url = new URL(urlString);
            val httppost = HttpPost(urlString)
            //            UrlEncodedFormEntity byteArrayEntity =
//              new UrlEncodedFormEntity(new ArrayList<BasicNameValuePair>());
//            httppost.setEntity(byteArrayEntity);
            setHeaders(soapAction!!, envelope!!, httppost, headers)
            resp = client.execute(httppost, localContext)
            val respEntity = resp.entity
            var responseCode = 0
            responseCode = resp.statusLine.statusCode
            println("======= SOAP responseCode =====$responseCode")
            ConnectionData.SoapCode = responseCode
            ConnectionData.readedSoapCode = true
            var responceText = ""
            responceText = resp.statusLine.reasonPhrase
            println("======= SOAP responceText =====$responceText")
            val hArray = resp.allHeaders
            val size = hArray.size
            for (i in 0 until size) {
                val h = hArray[i]
                if (h.name == "Content-Length") {
                    ConnectionData.SoapContentLenth = Integer.valueOf(h.value)
                    ConnectionData.readedSoapContentLenth = true
                }
                println("======= SOAP Resp.Headers: $h")
            }
            val `is` = respEntity.content
            parseResponse(envelope, `is`)
        } catch (ex: java.lang.Exception) {
            // ex.printStackTrace();
        }
        return if (resp != null) {
            Arrays.asList(*resp.allHeaders)
        } else {
            null
        }
    }

    private fun setHeaders(
        soapAction: String,
        envelope: SoapEnvelope,
        httppost: HttpPost,
        headers: List<*>?
    ) {
        var requestData: ByteArray? = null
        try {
            requestData = createRequestData(envelope)
            //System.out.println(requestData.toString());
        } catch (iOException: IOException) {
            //System.out.println("=========IOException iOException createRequestData(envelope) ==========");
        }
        //System.out.println("requestData ======= "+requestData.toString());
        val byteArrayEntity = ByteArrayEntity(requestData)
        httppost.entity = byteArrayEntity
        //String tttt="<Request><Request_ID>GET_DELIVERY_ORDERS</Request_ID><Request_Body><Request_Type>By Restaurant No</Request_Type><Restaurant_No.>MPS06</Restaurant_No.><Driver_ID>2215</Driver_ID></Request_Body></Request>";
        //httppost.setEntity(byteArrayEntity (tttt));
        httppost.addHeader("User-Agent", USER_AGENT)
        // SOAPAction is not a valid header for VER12 so do not add
        // it
        // @see "http://code.google.com/p/ksoap2-android/issues/detail?id=67
        if (envelope.version != SoapSerializationEnvelope.VER12) {
            httppost.addHeader("SOAPAction", soapAction)
        }
        if (envelope.version == SoapSerializationEnvelope.VER12) {
            httppost.addHeader("Content-Type", CONTENT_TYPE_SOAP_XML_CHARSET_UTF_8)
        } else {
            httppost.addHeader("Content-Type", CONTENT_TYPE_XML_CHARSET_UTF_8)
        }

        // Pass the headers provided by the user along with the call
        if (headers != null) {
            for (i in headers.indices) {
                val hp = headers[i] as HeaderProperty
                httppost.addHeader(hp.key, hp.value)
            }
        }
    }

    // Try to execute a cheap method first. This will trigger NTLM authentication
    fun setupNtlm(dummyUrl: String?, userId: String?, password: String?) {
        try {
            (client as AbstractHttpClient).authSchemes.register("ntlm", NTLMSchemeFactory())
            val creds = NTCredentials(userId, password, ntWorkstation, ntDomain)
            client.credentialsProvider.setCredentials(AuthScope.ANY, creds)
            val httpget = HttpGet(dummyUrl)
            val response1 = client.execute(httpget, localContext)
            val entity1 = response1.entity
            var responseCode = 0
            responseCode = response1.statusLine.statusCode
            println("======= NTLM responseCode =====$responseCode")
            ConnectionData.AuthCode = responseCode
            ConnectionData.readedAuthCode = true
            var responceText = ""
            responceText = response1.statusLine.reasonPhrase
            println("======= NTLM responceText =====$responceText")
            val hArray = response1.allHeaders
            val size = hArray.size
            for (i in 0 until size) {
                val h = hArray[i]
                if (h.name == "WWW-Authenticate") {
                    entity1.consumeContent()
                    throw Exception("Failed Authentication")
                }
                println("======= NTLM Auth.Resp.Headers: $h")
            }
            entity1.consumeContent()
        } catch (ex: Exception) {
            // swallow
        }
    }

    //NTLM Scheme factory
    private inner class NTLMSchemeFactory : AuthSchemeFactory {
        override fun newInstance(params: HttpParams): AuthScheme {
            // see http://www.robertkuzma.com/2011/07/
            // manipulating-sharepoint-list-items-with-android-java-and-ntlm-authentication/
            return NTLMScheme(JCIFSEngine())
        }
    }

    @Throws(IOException::class)
    override fun getServiceConnection(): ServiceConnection {
        throw IOException("Not using ServiceConnection in transport")
    }

    override fun getHost(): String {
        var retVal: String? = null
        try {
            retVal = URL(url).host
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return retVal!!
    }

    override fun getPort(): Int {
        var retVal = -1
        try {
            retVal = URL(url).port
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return retVal
    }

    override fun getPath(): String {
        var retVal: String? = null
        try {
            retVal = URL(url).path
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return retVal!!
    }

    companion object {
        const val ENCODING = "utf-8"
    }
}