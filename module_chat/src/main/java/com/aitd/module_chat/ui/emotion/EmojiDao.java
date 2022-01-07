package com.aitd.module_chat.ui.emotion;


import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class EmojiDao {
    private static final String TAG = "EmojiDao";
    private String path;
    private static EmojiDao dao;
    private static Context mContext;

    public static EmojiDao getInstance(Context context) {
        if (dao == null) {
            synchronized (EmojiDao.class) {
                if (dao == null) {
                    mContext = context;
                    dao = new EmojiDao();
                }
            }
        }
        return dao;
    }

    private EmojiDao() {
        try {
            path = CopySqliteFileFromRawToDatabases("emoji.db");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<EmojiBean> getEmojiBean() {
        List<EmojiBean> emojiBeanList = new ArrayList<EmojiBean>();
//        Log.e(TAG, "getEmojiBean: 表情数据库路径为：" + path);
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            Cursor cursor = db.query("emoji", new String[]{"unicodeInt", "_id"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                EmojiBean bean = new EmojiBean();
                int unicodeInt = cursor.getInt(0);
                int id = cursor.getInt(1);
                bean.setUnicodeInt(unicodeInt);
                bean.setId(id);
                emojiBeanList.add(bean);
            }
        } catch (Exception e) {
            Log.e(TAG, "getEmojiBean: 查询表情数据库出错>>" + e.toString());
        }
        return emojiBeanList;
    }


    /**
     * 将assets目录下的文件拷贝到database中
     *
     * @return 存储数据库的地址
     */
    /*public static String CopySqliteFileFromRawToDatabases(String SqliteFileName) throws IOException {
        // 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>
        //复制的话这里需要换成自己项目的包名
        File dir = new File("data/data/" + "com.hrl.chaui" + "/databases");

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }

        File file= new File(dir, SqliteFileName);
        InputStream inputStream = null;
        OutputStream outputStream =null;

        //通过IO流的方式，将assets目录下的数据库文件，写入到SD卡中。
        if (!file.exists()) {
            try {
                file.createNewFile();
                inputStream = MyApplication.mApplication.getClass().getClassLoader().getResourceAsStream("assets/" + SqliteFileName);
                outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len ;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer,0,len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return file.getPath();
    }*/
    public static String CopySqliteFileFromRawToDatabases(String SqliteFileName) throws IOException {
        // 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>
        //复制的话这里需要换成自己项目的包名
        /*File dir = new File(mContext.getDatabasePath() + "/databases");

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }*/

        File file = mContext.getDatabasePath(SqliteFileName);
        InputStream inputStream = null;
        OutputStream outputStream = null;

        //通过IO流的方式，将assets目录下的数据库文件，写入到SD卡中。
        if (!file.exists()) {
            try {
                file.createNewFile();
                AssetManager assetManager = mContext.getAssets();
                inputStream = assetManager.open(SqliteFileName);
                outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return file.getPath();
    }

}
