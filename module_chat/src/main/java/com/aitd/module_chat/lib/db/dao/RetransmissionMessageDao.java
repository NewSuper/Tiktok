package com.aitd.module_chat.lib.db.dao;

import com.aitd.module_chat.lib.db.entity.TBRetransmissionMessage;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

//重传消息表
@Dao
public interface RetransmissionMessageDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insert(TBRetransmissionMessage retransmissionMessage);

    @Query("SELECT * FROM retransmission_message WHERE message_id = :messageId AND owner_id = :ownerId")
    TBRetransmissionMessage getByMessageId(String messageId, String ownerId);

    @Query("DELETE FROM retransmission_message WHERE message_id = :messageId")
    int delete(String messageId);

    @Query("DELETE FROM retransmission_message WHERE conversation_id = :conversationId")
    int deleteAll(String conversationId);

}
