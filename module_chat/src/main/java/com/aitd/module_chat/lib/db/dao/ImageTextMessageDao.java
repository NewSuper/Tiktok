package com.aitd.module_chat.lib.db.dao;

import com.aitd.module_chat.lib.db.entity.TBImageTextMessage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

//图文表
@Dao
public interface ImageTextMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertImageTextMessage(TBImageTextMessage message);

    @Query("SELECT * FROM image_text_message WHERE message_id = :messageId")
    TBImageTextMessage getImageTextMessageByMessageId(String messageId);

    @Query("DELETE FROM image_text_message WHERE message_id = :messageId")
    int delete(String messageId);

    @Query("DELETE FROM image_text_message WHERE conversation_id = :conversationId")
    int deleteAll(String conversationId);

    @Transaction
    @Query("DELETE FROM image_text_message WHERE timestamp <= :timestamp AND conversation_id = :conversationId")
    int deleteByTimestamp(long timestamp, String conversationId);
}