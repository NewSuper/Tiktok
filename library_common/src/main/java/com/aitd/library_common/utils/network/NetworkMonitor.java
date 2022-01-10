package com.aitd.library_common.utils.network;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

public class NetworkMonitor {

    private NetworkMonitorListener mNetworkMonitorListener;

    public static NetworkMonitor getInstance() {
        return Holder.instance;
    }

    public void initMonitor(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//API 大于26时
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//API 大于21时
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        } else {//低版本
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ANDROID_NET_CHANGE_ACTION);
            context.registerReceiver(receiver, intentFilter);
        }
    }


    private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ANDROID_NET_CHANGE_ACTION)) {
                //网络发生变化 没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
                int netType = NetStateUtils.getAPNType(context);
                NetWorkState netWorkState = NetWorkState.NONE;
                switch (netType) {
                    case 0://None
                        if(mNetworkMonitorListener != null) {
                            mNetworkMonitorListener.onLost();
                        }
                        break;
                    default:
                        if(mNetworkMonitorListener != null) {
                            mNetworkMonitorListener.onAvailable();
                        }
                        break;
                }
            }
        }
    };

    // TODO 最小支持21的API NetworkCallback
    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        /**
         * 网络可用的回调连接成功
         */
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            if(mNetworkMonitorListener!= null) {
                mNetworkMonitorListener.onAvailable();
            }
        }

        /**
         * 网络不可用时调用和onAvailable成对出现
         */
        @Override
        public void onLost(Network network) {
            super.onLost(network);
            if(mNetworkMonitorListener!= null) {
                mNetworkMonitorListener.onLost();
            }
        }

        /**
         * 在网络连接正常的情况下，丢失数据会有回调 即将断开时
         */
        @Override
        public void onLosing(Network network, int maxMsToLive) {
            super.onLosing(network, maxMsToLive);
        }

        /**
         * 网络功能更改 满足需求时调用
         * @param network
         * @param networkCapabilities
         */
        @Override
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
        }

        /**
         * 网络连接属性修改时调用
         * @param network
         * @param linkProperties
         */
        @Override
        public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
            super.onLinkPropertiesChanged(network, linkProperties);
        }

        /**
         * 网络缺失network时调用
         */
        @Override
        public void onUnavailable() {
            super.onUnavailable();
        }
    };

    public void setNetworkMonitorListener(NetworkMonitorListener listener) {
        mNetworkMonitorListener = listener;
    }

    public interface NetworkMonitorListener {
        void onAvailable();
        void onLost();
    }

    public static class Holder {
        static NetworkMonitor instance = new NetworkMonitor();
    }
}
