package com.aitd.library_common.statistics;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.aitd.library_common.app.BaseApplication;

public class SPUtils {
    private static final String  UPDATE_DB = "update_db";
    private static final String   UPDATE_KEY = "update_key";
    public static void update(String version, Activity activity){
        SharedPreferences preferences = activity.getSharedPreferences(UPDATE_DB, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString(UPDATE_KEY,version);
        editor.commit();
    }

    public static String update(Activity activity){
        SharedPreferences preferences = activity.getSharedPreferences(UPDATE_DB, Context.MODE_PRIVATE);
        return preferences.getString(UPDATE_KEY,"");
    }

    private static SPUtils instance = new SPUtils();
    private static SharedPreferences mSp;
    //单例
    private SPUtils(){

    }
    //得到单例
    public static SPUtils getInstance(){
        if(mSp == null){
            mSp = BaseApplication.getAppContext().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    //保存
    public void save(String key,Object value){
        if(value instanceof String){
            mSp.edit().putString(key, (String) value).commit();
        }else if(value instanceof Boolean){
            mSp.edit().putBoolean(key, (Boolean) value).commit();
        }else if(value instanceof  Integer){
            mSp.edit().putInt(key, (Integer) value).commit();
        }
    }
    //获取String类型数据
    public String getString(String key,String defValue){
        return mSp.getString(key,defValue);
    }
    //获取Boolean类型数据
    public Boolean getBoolean(String key,boolean defValue){
        return mSp.getBoolean(key,defValue);
    }//获取Int类型数据
    public int getInt(String key,int defValue){
        return mSp.getInt(key,defValue);
    }
}
