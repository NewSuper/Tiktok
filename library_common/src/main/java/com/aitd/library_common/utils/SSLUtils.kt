package com.aitd.library_common.utils

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext


object SSLUtils {
    private var sHostnameVerifier: HostnameVerifier? = null
    private var sSSLContext: SSLContext? = null

    private fun initSSLContent() {
        try {
            sSSLContext = SSLContext.getInstance("TLS")
            sSSLContext?.init(null, null, null)
        } catch (e:Exception) {
            e.printStackTrace()
        }
    }

    private fun initHost() {
        sHostnameVerifier = HostnameVerifier { hostname, session -> true }
    }

    fun getSSLContext(): SSLContext? {
        if (sSSLContext == null) {
            initSSLContent()
        }
        return sSSLContext
    }

    fun setSSLContext(sslContext: SSLContext) {
        sSSLContext = sslContext
    }

    fun setHostnameVerifier(verifier: HostnameVerifier) {
        sHostnameVerifier = verifier
    }

    fun getHostVerifier(): HostnameVerifier? {
        if (sHostnameVerifier == null) {
            initHost()
        }
        return sHostnameVerifier
    }
}