package com.aitd.module_chat.utils;

import android.content.Context;

import com.aitd.library_common.utils.GlobalContextManager;
import com.aitd.module_chat.SensitiveWordResult;
import com.aitd.module_chat.pojo.BeanSensitiveWord;
import com.aitd.module_chat.pojo.Sensitive;
import com.aitd.module_chat.pojo.SubSensitive;
import com.aitd.module_chat.utils.qlog.QLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SensitiveWordsUtils {
    private static BeanSensitiveWord[] mBeans;

    private static void init(Context context) {
        if(mBeans == null || mBeans.length == 0) {
            readSensitiveWord(context);
        }
    }

    /**
     * 从缓存中读取敏感词库
     *
     * @param context
     * @return
     * @throws Exception
     */
    private static Set<BeanSensitiveWord> readSensitiveWord(Context context) {
        Set<BeanSensitiveWord> set = new HashSet<>();

        mBeans = SharePreferencesUtil.Companion.getInstance(context).loadSensitiveWord();
        if(mBeans != null) {
            for (BeanSensitiveWord word : mBeans) {
                set.add(word);
            }
        }
        return set;
    }

    public static SensitiveWordResult checkSensitiveWord(String text, Context context) {
        long time = System.currentTimeMillis();
        init(context);
        QLog.d("SensitiveWordsUtils", "init time="+(System.currentTimeMillis() - time));
        Set<String> banSet = new HashSet<>();
        Map<String, String> replaceMap = new HashMap<String, String>();
        if(mBeans != null && mBeans.length > 0) {
            QLog.d("SensitiveWordsUtils", "替换 屏蔽 time="+(System.currentTimeMillis() - time));
            for (BeanSensitiveWord bean : mBeans) {
                if (bean.getType().equals(BeanSensitiveWord.Type.TYPE_REPLACE)) {
                    //替换
                    replaceMap.put(bean.getSensitiveWord(), bean.getReplaceWord());
                } else {
                    //屏蔽
                    banSet.add(bean.getSensitiveWord());
                }
            }
            QLog.d("SensitiveWordsUtils", "替换 屏蔽 time="+(System.currentTimeMillis() - time));
            boolean isBan = isBan(text);
            QLog.d("SensitiveWordsUtils", "isBan time="+(System.currentTimeMillis() - time));
            if (!isBan) {
                text = replace(text);
                QLog.d("SensitiveWordsUtils", "replace time="+(System.currentTimeMillis() - time));
            }
            QLog.d("SensitiveWordsUtils", "SensitiveWordResult time="+(System.currentTimeMillis() - time));
            return new SensitiveWordResult(isBan, text);
        }
        QLog.d("SensitiveWordsUtils", "time="+(System.currentTimeMillis() - time));

        return null;
    }

    static boolean isBan(String text) {
        Set<String> banSet = new HashSet<>();

        for (BeanSensitiveWord bean : mBeans) {
            if (bean.getType().equals(BeanSensitiveWord.Type.TYPE_BAN)) {
                //屏蔽
                banSet.add(bean.getSensitiveWord());
            }
        }

        Sensitive sensitive = new Sensitive(banSet);

        return sensitive.contains(text);
    }

    static String replace(String text) {
        Map<String, String> replaceMap = new HashMap<String, String>();

        for (BeanSensitiveWord bean : mBeans) {
            if (bean.getType().equals(BeanSensitiveWord.Type.TYPE_REPLACE)) {
                //替换
                replaceMap.put(bean.getSensitiveWord(), bean.getReplaceWord());
            }
        }


        Set<Map.Entry<String, String>> entries = replaceMap.entrySet();
        SubSensitive sensitive = new SubSensitive();
        sensitive.setSpecialCharacters(getSpecialCharacters());
        for (Map.Entry<String, String> entry : entries) {
            long start = System.currentTimeMillis();
            String key = entry.getKey();
            String value = entry.getValue();
            text = sensitive.setSensitiveWord(key).replaceSensitiveWord(text, value);
            QLog.d("SensitiveWordsUtils", "replace key"+ key+ ",time: "+(System.currentTimeMillis() - start));
        }

        return text;
    }

    private static char[] getSpecialCharacters() {
        return SharePreferencesUtil.Companion.getInstance(GlobalContextManager.getInstance().
                getContext()).loadSpecialCharacters().toCharArray();
    }

    interface SenstiveWordCallback {
        void onResult(SensitiveWordResult result);
    }
}
