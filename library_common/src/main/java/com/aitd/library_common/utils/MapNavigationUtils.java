package com.aitd.library_common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.aitd.library_common.R;
import com.blankj.utilcode.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MapNavigationUtils {

    /**
     * Google地图
     */
    private static final String GOOGLE_MAP_PACKAGE = "com.google.android.apps.maps";
    /**
     * 高德地图
     */
    private static final String GAODE_MAP_PACKAGE = "com.autonavi.minimap";

    /**
     * 百度地图
     */
    private static final String BAIDU_MAP_PACKAGE = "com.baidu.BaiduMap";

    /**
     * 腾讯地图
     */
    private static final String TENCENT_MAP_PACKAGE = "com.tencent.map";

    /**
     * 获取已安装的地图app列表
     *
     * @return
     */
    public static List<MapNavigationType> getInstalledMapApp(Activity context) {
        List<MapNavigationType> mapNavigationTypes = new ArrayList<>();
        if (isAvilible(context, GOOGLE_MAP_PACKAGE)) {
            mapNavigationTypes.add(new MapNavigationType(1,StringUtil.getResourceStr(context, R.string.navigation_google),GOOGLE_MAP_PACKAGE));
        }
        if (isAvilible(context, GAODE_MAP_PACKAGE)) {
            mapNavigationTypes.add(new MapNavigationType(2,StringUtil.getResourceStr(context, R.string.navigation_gaode),GAODE_MAP_PACKAGE));
        }
        if (isAvilible(context, BAIDU_MAP_PACKAGE)) {
            mapNavigationTypes.add(new MapNavigationType(3,StringUtil.getResourceStr(context, R.string.navigation_baidu),BAIDU_MAP_PACKAGE));
        }
        if (isAvilible(context,TENCENT_MAP_PACKAGE)) {
            mapNavigationTypes.add(new MapNavigationType(4,StringUtil.getResourceStr(context, R.string.navigation_tenxun),TENCENT_MAP_PACKAGE));
        }
        return mapNavigationTypes;
    }


    /* 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
     */
    public static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }


//    /**
//     * 启动到应用商店app详情界面(默认使用高德)
//     */
//    public static void launchAppDetail(Context mContext) {
//        try {
//            Uri uri = Uri.parse("market://details?id=" + MapNavigationType.GAODE_MAP.getPackageName());
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 启动Google地图
     * @param mContex
     * @param lat
     * @param lng
     */
    public static void gotoGoogleMap(Context mContex, String lat, String lng) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lng+"&mode=d");  //d:驾车线路  b：骑行线路  w:走路线路
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        mContex.startActivity(mapIntent);
    }


    /**
     * 启动高德地图导航
     *
     * @param mContext
     * @param lat      纬度
     * @param lng      经度
     */
    public static void gotoGaoDe(Context mContext, String lat, String lng, String adr) {
        try {
            String url = "amapuri://route/plan/?sid=BGVIS1&slat=&slon=&sname=&did=&dlat=" + lat + "&dlon=" + lng + "&dname=" + adr + "&dev=0&t=0";
            Intent intent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(url));
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 启动腾讯地图导航
     *
     * @param address 目的地
     * @param latStr  必填 纬度
     * @param lonStr  必填 经度
     */
    public static void gotoTengxun(Context context, String address, String latStr, String lonStr) {
        double lat = 0, lon = 0;
        if (!StringUtils.isEmpty(latStr)) {
            lat = Double.parseDouble(latStr);
        }
        if (!StringUtils.isEmpty(lonStr)) {
            lon = Double.parseDouble(lonStr);
        }
        // 启动路径规划页面
        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse("qqmap://map/routeplan?type=drive&from=&fromcoord=&to=" + address + "&tocoord=" + lat + "," + lon + "&policy=0&referer=appName"));
        context.startActivity(naviIntent);
    }

    /**
     * 启动百度地图导航
     *
     * @param address 目的地
     * @param latStr  必填 纬度
     * @param lonStr  必填 经度
     */
    public static void goToBaiduActivity(Context context, String address, String latStr, String lonStr) {
        double lat = 0, lon = 0;
        if (!StringUtils.isEmpty(latStr)) {
            lat = Double.parseDouble(latStr);
        }
        if (!StringUtils.isEmpty(lonStr)) {
            lon = Double.parseDouble(lonStr);
        }
        //启动路径规划页面
        String url = "baidumap://map/direction?origin=我的位置&destination=" + address + "&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
        Intent naviIntent = new Intent("android.intent.action.VIEW", android.net.Uri.parse(url));
        context.startActivity(naviIntent);
    }

}
