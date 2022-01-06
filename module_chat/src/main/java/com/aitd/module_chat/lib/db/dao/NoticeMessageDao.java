package com.aitd.module_chat.lib.db.dao;

import com.aitd.module_chat.lib.db.entity.TBNoticeMessage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

//公告表
@Dao
public interface NoticeMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertNoticeMessage(TBNoticeMessage noticeMessage);

    @Query("SELECT * FROM notice_message WHERE message_id = :messageId")
    TBNoticeMessage getNoticeMessageByMessageId(String messageId);

    @Query("DELETE FROM notice_message WHERE message_id = :messageId")
    int delete(String messageId);;

    @Query("DELETE FROM notice_message WHERE conversation_id = :conversationId")
    int deleteAll(String conversationId);

    @Transaction
    @Query("DELETE FROM notice_message WHERE timestamp <= :timestamp AND conversation_id = :conversationId")
    int deleteByTimestamp(long timestamp, String conversationId);
}
