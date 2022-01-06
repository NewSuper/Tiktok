package com.aitd.module_chat.netty

import android.content.Context
import com.aitd.module_chat.pojo.TcpServer

class NettyClientModel(context: Context) {
    private var mNettyClient: NettyClient? = null
    fun initNettyClient(userToken: String) {
        mNettyClient = NettyClient.getInstance()//获取client实例
        mNettyClient?.initBootstrap()//初始化netty启动程序
        connect(userToken)
    }

    fun disconnectNettyClient() {
        mNettyClient?.disconnect()
    }

    fun getNettyClient(): NettyClient? {
        return mNettyClient
    }

    /**
     * 获取持久化的im server信息进行连接
     */
    private fun connect(userToken: String) {
        mNettyClient!!.connect(TcpServer.host, TcpServer.port, userToken)
//        mNettyClient!!.connect("192.168.3.157", 8700, userToken)
    }
}