package com.aitd.module_chat.lib.db.dao;

import com.aitd.module_chat.lib.db.entity.TBCustomMessage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

//自定义消息表（比如红包，分享等）
@Dao
public interface CustomMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCustomMessage(TBCustomMessage image) ;

    @Query("SELECT * FROM custom_message WHERE message_id = :messageId")
    TBCustomMessage getCustomMessageByMessageId(String messageId);

    @Query("UPDATE custom_message SET content = :content, extra = :extra WHERE message_id = :messageId AND conversation_id = :conversationId")
    int update(String content, String extra, String messageId, String conversationId);

    @Query("DELETE FROM custom_message WHERE message_id = :messageId")
     int delete(String messageId);

    @Query("DELETE FROM custom_message WHERE conversation_id = :conversationId")
    int deleteAll(String conversationId);

    @Transaction
    @Query("DELETE FROM custom_message WHERE timestamp <= :timestamp AND conversation_id = :conversationId")
    int deleteByTimestamp(long timestamp, String conversationId );
}