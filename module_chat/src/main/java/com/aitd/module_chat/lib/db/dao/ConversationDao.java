package com.aitd.module_chat.lib.db.dao;

import com.aitd.module_chat.lib.db.entity.ConversationEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

//会话表
@Dao
public interface ConversationDao {

    @Query("SELECT conversation_id FROM conversation WHERE conversation_type = :conversationType AND target_id = :targetId AND `owner_id` = :ownerId LIMIT 1")
    String isExistP2PConversation(String conversationType, String targetId, String ownerId);

    @Query("SELECT conversation_id FROM conversation WHERE conversation_type = :conversationType AND target_id = :targetId " +
            "AND `owner_id` = :ownerId LIMIT 1")
    String isExistGroupConversation(String conversationType, String targetId, String ownerId);

    /**
     * 插入会话
     *
     * @param conversationEntity
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    //REPLACE  冲突策略是取代旧数据同时继续事务
    long insertConversation(ConversationEntity conversationEntity);

    /**
     * 更新一对一 置顶
     *
     * @param isTop
     * @param conversationId
     * @return
     */
    @Query("UPDATE conversation SET `is_top` = :isTop WHERE   conversation_id = :conversationId ")
    int updateP2PTop(int isTop, String conversationId);

    @Query("UPDATE conversation SET `is_top` = :isTop WHERE  conversation_type = :sendType AND target_id = :targetId  AND `owner_id` = :ownerId  ")
    int updateP2PTop(String sendType, String targetId, int isTop, String ownerId);

    /**
     * 更新群会话置顶
     *
     * @param sendType
     * @param targetId
     * @param isTop
     * @param ownerId
     * @return
     */
    @Query("UPDATE conversation SET `is_top` = :isTop WHERE  conversation_type = :sendType AND target_id = :targetId AND `owner_id` = :ownerId  ")
    int updateGroupTop(String sendType, String targetId, int isTop, String ownerId);

    /**
     * 更新草稿
     *
     * @param draft
     * @param conversationId
     * @return
     */
    @Query("UPDATE conversation SET `draft` = :draft WHERE  conversation_id = :conversationId ")
    int updateDraft(String draft, String conversationId);

    /**
     * 更新会话名称
     *
     * @param type
     * @param targetId
     * @param title
     * @return
     */
    @Query("UPDATE conversation SET `target_name` = :title WHERE  target_id = :targetId AND conversation_type = :type")
    int updateConversationTitle(String type, String targetId, String title);

    /**
     * 更新会话图片
     *
     * @param type
     * @param targetId
     * @param icon
     * @return
     */
    @Query("UPDATE conversation SET `icon` = :icon WHERE  target_id = :targetId AND conversation_type = :type")
    int updateConversationIcon(String type, String targetId, String icon);

    /**
     * 同时更新会话显示的用户昵称，以及图片
     *
     * @param type
     * @param targetId
     * @param title
     * @param icon
     * @return
     */
    @Query("UPDATE conversation SET `target_name` = :title, `icon` = :icon WHERE  target_id = :targetId AND conversation_type = :type")
    int updateConversationTitleAndIcon(String type, String targetId, String title, String icon);

    /**
     * 添加会话未读数
     *
     * @param sendType
     * @param targetId
     * @param count
     * @param ownerId
     * @return
     */
    @Query("UPDATE conversation SET `un_read_count` = :count+`un_read_count`, `mentionedCount` = :mentionedCount+`mentionedCount` WHERE  conversation_type = :sendType AND" +
            " target_id = :targetId AND `owner_id` = :ownerId" )
    int addConversationUnReadCount(String sendType, String targetId, int count, String ownerId, int mentionedCount);

    /**
     * 更新会话未读数
     *
     * @param sendType
     * @param targetId
     * @param count
     * @param ownerId
     * @return
     */
    @Query("UPDATE conversation SET `un_read_count` = :count, `mentionedCount` = :mentionedCount WHERE  conversation_type = :sendType AND target_id = :targetId " +
            "AND `owner_id` = :ownerId")
    int updateConversationUnReadCount(String sendType, String targetId, int count, String ownerId, int mentionedCount);

    /**
     * 更新免打扰
     *
     * @param no_disturbing
     * @param conversation_id
     * @return
     */
    @Query("UPDATE conversation SET `no_disturbing` = :no_disturbing WHERE conversation_id = :conversation_id")
    int updateNoDisturbing(int no_disturbing, String conversation_id);

    @Query("UPDATE conversation SET `no_disturbing` = :isDisturbing WHERE  conversation_type = :sendType AND target_id = :targetId AND `owner_id` = :ownerId  ")
    int updateP2PNoDisturbing(String sendType, String targetId, int isDisturbing, String ownerId);

    @Query("UPDATE conversation SET `no_disturbing` = :isDisturbing WHERE  conversation_type = :sendType AND target_id = :targetId AND `owner_id` = :ownerId  ")
    int updateGroupNoDisturbing(String sendType, String targetId, int isDisturbing, String ownerId);

    @Query("UPDATE conversation SET `time_indicator` = :timeIndicator, `top_time` = :topTime WHERE  conversation_id =" +
            " :conversationId AND `owner_id` = :ownerId ")
    int updateTimeIndicator(String conversationId, long timeIndicator, long topTime, String ownerId);

    @Query("UPDATE conversation SET `time_indicator` = :timeIndicator WHERE  conversation_id = :conversationId AND " +
            "`owner_id` = :ownerId ")
    int updateTimeIndicator(String conversationId, long timeIndicator, String ownerId);

    @Query("UPDATE conversation SET `time_indicator` = :timeIndicator, `top_time` = :topTime WHERE  conversation_type" +
            " = :sendType AND target_id = :targetId AND `owner_id` = :ownerId")
    int updateTimeIndicator(String sendType,
                            String targetId,
                            long topTime,
                            long timeIndicator,
                            String ownerId);

    @Query("UPDATE conversation SET `time_indicator` = :timeIndicator, `top_time` = :topTime WHERE  conversation_type" +
            " = :sendType AND `owner_id` = :owner_id AND target_id = :targetId")
    int updateGroupTimeIndicator(String sendType, String targetId, String owner_id, long topTime, long timeIndicator);

    /**
     * 获取所有会话
     *
     * @param owner_id
     * @return
     */
    @Query("SELECT * FROM conversation WHERE owner_id = :owner_id AND deleted = 0")
    List<ConversationEntity> getAllConversation(String owner_id);

    @Query("SELECT * FROM conversation WHERE owner_id = :owner_id AND deleted = 0 AND conversation_type in (:region)")
    List<ConversationEntity> getConversationInRegion(String owner_id, List<String> region);

    /**
     * 搜索会话
     *
     * @param keyWord
     * @param conversationTypes
     * @param ownerId
     * @return
     */
    @Query("SELECT * FROM conversation WHERE target_name LIKE :keyWord AND conversation_type IN (:conversationTypes) AND owner_id = :ownerId ORDER BY timestamp DESC")
    List<ConversationEntity> searchConversations(String keyWord, String[] conversationTypes, String ownerId);

    @Query("SELECT * FROM conversation WHERE conversation_id = :conversionId LIMIT 1")
    ConversationEntity getConversationById(String conversionId);

    @Query("SELECT * FROM conversation WHERE target_id = :targetId AND owner_id = :ownerId AND conversation_type = :conversationType LIMIT 1")
    ConversationEntity getConversationByTargetId(String conversationType, String targetId, String ownerId);

    @Query("UPDATE conversation SET deleted = :deleted WHERE conversation_id = :conversationId")
    int markConversationDelete(int deleted, String conversationId);

    @Query("UPDATE conversation SET deleted = :deleted WHERE target_id = :targetId AND conversation_type = :type AND owner_id = :ownerId")
    int markConversationDelete(int deleted, String type, String targetId, String ownerId);

    @Transaction
    @Query("DELETE FROM conversation WHERE owner_id = :ownerId")
    int deleteAllConversation(String ownerId);

    @Query("UPDATE conversation SET `timestamp` = :timestamp  WHERE  conversation_type" +
            " = :sendType AND target_id = :targetId AND `owner_id` = :ownerId")
    int refreshConversationInfo(String sendType, String targetId, long timestamp, String ownerId);

    @Query("UPDATE conversation SET `at_to` = :at_to WHERE  conversation_type = :sendType AND target_id = :targetId AND " +
            "`owner_id` = :ownerId")
    int updateConversationAtTO(String sendType, String targetId, String at_to, String ownerId);

    @Query("UPDATE conversation SET delete_time = :deleteTime WHERE conversation_id = :conversationId")
    int updateDeleteTime(long deleteTime, String conversationId);

    @Query("UPDATE conversation SET background = :url WHERE conversation_id = :conversationId")
    int updateBackground(String url, String conversationId);

    @Query("SELECT SUM(un_read_count) FROM conversation WHERE owner_id = :ownerId  AND no_disturbing = 0 AND conversation_type in (:region)")
    int getAllUnReadCount(String ownerId, List<String> region);

    @Query("SELECT SUM(un_read_count) FROM conversation WHERE conversation_id = :conversationId AND no_disturbing = 0 AND owner_id = :ownerId")
    int getUnReadCount(String conversationId, String ownerId);

    @Query("SELECT SUM(un_read_count) FROM conversation WHERE conversation_id = :conversationId AND owner_id = :ownerId")
    int getUnReadCountIgnoreDisturbing(String conversationId, String ownerId);

    @Query("SELECT SUM(mentionedCount) FROM conversation WHERE conversation_id = :conversationId AND owner_id = :ownerId")
    int getMentionedCount(String conversationId, String ownerId);
}
