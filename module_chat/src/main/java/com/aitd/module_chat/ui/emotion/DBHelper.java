package com.aitd.module_chat.ui.emotion;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.aitd.module_chat.lib.QXIMKit;
import com.aitd.module_chat.ui.emotion.db.Sticker;
import com.aitd.module_chat.ui.emotion.db.TBCollection;
import com.aitd.module_chat.ui.emotion.db.TBRecord;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.List;

import GreenDao.DaoMaster;
import GreenDao.DaoSession;
import GreenDao.StickerDao;
import GreenDao.TBCollectionDao;
import GreenDao.TBRecordDao;

public class DBHelper {

    private DaoSession daoSession;

    private DBHelper() {
    }

    public static void initDB(Application application) {
        DBHelper.getInstance().init(application);
    }

    private void init(Application application) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(application, "imapp_db");
        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(sqLiteDatabase);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public List<StickerItem> getAllSticker(String loginUserId) {
        List<StickerItem> stickerItems = new ArrayList<>();
        if (!TextUtils.isEmpty(loginUserId)) {
            List<Sticker> data = daoSession.getStickerDao().queryBuilder()
                    .where(StickerDao.Properties.Belong.eq(loginUserId))
                    .orderDesc(StickerDao.Properties.Index).list();

            for (Sticker dao : data) {
                if (dao != null) {
                    StickerItem stickerItem = new StickerItem(StickerManager.STICKER_DEFAULT, dao.getName(),
                            dao.getLocalPath(), dao.getOriginUrl(), dao.getWidth(), dao.getHeight(), dao.getIndex());
                    stickerItems.add(stickerItem);
                }
            }
        }
        return stickerItems;
    }

    public long insertCollection(CollectionItem item) {
        TBCollection entity = TBCollection.obtain(item);
        return daoSession.getTBCollectionDao().insert(entity);
    }

    public CollectionItem queryCollection(String url, String ownerId) {
        TBCollection tbCollection = daoSession.getTBCollectionDao().queryBuilder().
                where(TBCollectionDao.Properties.OriginUrl.eq(url)).
                where(TBCollectionDao.Properties.OwnerId.eq(ownerId)).limit(1).unique();
        if (tbCollection == null) {
            return null;
        }
        return new CollectionItem(tbCollection.getMessageId(), tbCollection.getLocalPath(), tbCollection.getOriginUrl(), tbCollection.getType(), ownerId);
    }

    public long insertRecord(RecordItem item) {
        TBRecord entity = TBRecord.obtain(item);
        return daoSession.getTBRecordDao().insert(entity);
    }

    public RecordItem queryRecord(String url, String ownerId) {
        TBRecord tbRecord = daoSession.getTBRecordDao().queryBuilder().
                where(TBRecordDao.Properties.OriginUrl.eq(url)).
                where(TBRecordDao.Properties.OwnerId.eq(ownerId)).limit(1).unique();
        if (tbRecord == null) {
            return null;
        }
        return new RecordItem(tbRecord.getMessageId(), tbRecord.getLocalPath(), tbRecord.getOriginUrl(), tbRecord.getType(), ownerId);
    }

    public boolean isHasSticker(String originUrl) {
        String loginUserId = QXIMKit.getInstance().getCurUserId();
        Query query = daoSession.getStickerDao().queryBuilder().where(StickerDao.Properties.Belong.eq(loginUserId), StickerDao.Properties.OriginUrl.eq(originUrl)).build();
        if (query.list().size() > 0) {
            return true;
        }
        return false;
    }

    public static DBHelper getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        static DBHelper instance = new DBHelper();

        private Holder() {
        }
    }
}