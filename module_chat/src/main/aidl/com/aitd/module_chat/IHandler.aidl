// IHandler.aidl
package com.aitd.module_chat;

// Declare any non-default types here with import statements
import com.aitd.module_chat.IConnectStringCallback;
import com.aitd.module_chat.ISendMessageCallback;
import com.aitd.module_chat.IConversationListener;
import com.aitd.module_chat.IMessageReceiptListener;
import com.aitd.module_chat.IOnChatNoticeReceivedListener;
import com.aitd.module_chat.IOnChatRoomMessageReceiveListener;
import com.aitd.module_chat.IOnReceiveMessageListener;
import com.aitd.module_chat.IConnectionStatusListener;
import com.aitd.module_chat.ICallReceiveMessageListener;
import com.aitd.module_chat.IResultCallback;
import com.aitd.module_chat.Message;
import com.aitd.module_chat.Conversation;
import com.aitd.module_chat.SensitiveWordResult;
import com.aitd.module_chat.ICustomEventProvider;
import com.aitd.module_chat.UserProperty;
import com.aitd.module_chat.RTCCandidate;
import com.aitd.module_chat.RTCJoin;
import com.aitd.module_chat.RTCJoined;
import com.aitd.module_chat.RTCOffer;
import com.aitd.module_chat.RTCSdp;
import com.aitd.module_chat.RTCVideoParam;
import com.aitd.module_chat.SearchConversationResult;
import com.aitd.module_chat.RTCServerConfig;
import com.aitd.module_chat.IRTCMessageListener;

interface IHandler {

     void connectServer(String token, String imServerUrl, IConnectStringCallback callback);
     void disconnect();

         void setConversationNoDisturbing(in String conversationId, boolean isNoDisturbing, IResultCallback
         callback);

         void startCall(String conversationType, String targetId, String roomId, String callType, in List<String> userIds,
                 IResultCallback callback);

         void acceptCall(String roomId, IResultCallback callback);
         void cancelCall(String roomId, IResultCallback callback);
         void refuseCall(String roomId, IResultCallback callback);
         void hangUp(String roomId, String userId,IResultCallback callback);
         void setChatRoom(String chatRoomId, String name, String value, int autDel, IResultCallback callback);
         void delChatRoom(String chatRoomId, String name, IResultCallback callback);
         void getChatRoom(String chatRoomId, String name, IResultCallback callback);
         void joinChatRoom(String chatRoomId, IResultCallback callback);
         void exitChatRoom(String chatRoomId, IResultCallback callback);
         void saveOnly(inout Message message, ISendMessageCallback callback);
         void sendOnly(inout Message message, ISendMessageCallback callback);
         int updateMessageState(String messageId , int state);
         int updateMessageStateAndTime(String messageId ,long timestamp, int state);
         void getConversationProperty(inout Conversation conversation, in IResultCallback callback);
         void sendHeartBeat();
         void sendLogout(String pushName,IResultCallback callback);
         void sendMessageReadReceipt(String conversationType, String targetId, IResultCallback callback);
         void  deleteRemoteMessageByMessageId(String conversationType, String  targetId, in List<String> messageIds, IResultCallback callback);
         void deleteRemoteMessageByTimestamp(String conversationType, String targetId, long timestamp, IResultCallback
         callback);
         int deleteLocalMessageByTimestamp(String conversationType , String targetId, long timestamp);
         int updateOriginPath(inout Message message);
         int updateLocalPath(inout Message message);
         int updateHearUrl(String messageId, String headUrl);
         void sendRecall(inout Message message, IResultCallback callback);
         int deleteLocalMessageById(in String[] messageIds);
         void setConversationTop(String conversationId, boolean isTop, IResultCallback callback);
         int deleteConversation(String conversationId);
         int deleteConversationByTargetId(String type, String targetId);
         int deleteAllConversation();
         List<Message> searchTextMessage(String content);
         List<Message> searchTextMessageByConversationId(String content, String conversationId);
         List<Message> getMessages(String conversationType, String targetId, int offset,
                                      int pageSize);

         List<Message> getMessagesByTimestamp(String conversationType, String targetId, long timestamp, int searchType, int pageSzie);
         List<Message> getMessagesByType(String conversationId, in List<String> types, int offset,
                                      int pageSize,boolean isAll, boolean isDesc);

         List<Message> getUnReadAtMessages(String conversationId);
         Message getFirstUnReadMessage(String conversationId);
         Conversation getConversation(String conversationType, String targetId);
         List<Conversation>  getAllConversation();
         List<Conversation>  getConversationInRegion(in List<String> region);
         int updateConversationDraft(String conversationId, String draft);
         int updateConversationTitle(String type,String targetId, String title);
         int clearMessages(String conversationId);

         void setReceiveMessageListener(in IOnReceiveMessageListener listener);
         void setMessageReceiptListener(in IMessageReceiptListener listener);
         void addOnChatNoticeReceivedListener(in IOnChatNoticeReceivedListener listener);
         void setOnChatRoomMessageReceiveListener(in IOnChatRoomMessageReceiveListener listener);
         void setConversationListener(in IConversationListener listener);
         void setConnectionStatusListener(in IConnectionStatusListener listener);
         void setCallReceiveMessageListener(in ICallReceiveMessageListener listener);
         void switchAudioCall(String roomId, IResultCallback callback);
         String getServerHost();
         String getCurUserId();
         String getHttpHost();
         String getRSAKey();
         void sendCallError(String roomId, String targetId,IResultCallback callback);
         List<SearchConversationResult> searchConversations(String keyWord, in String[] conversationTypes, in String[] messageTypes);
         int getAllUnReadCount(in List<String> region);
         int getConversationUnReadCount(String conversationId, boolean isIgnoreNoDisturbing);

         SensitiveWordResult checkSensitiveWord(String text);
         boolean registerCustomEventProvider(in ICustomEventProvider provider);
         boolean isMessageExist(String messageId);
         List<Message> checkSendingMessage();
         int updateConversationBackground(String url, String conversationId);
         void setUserProperty(in UserProperty property,IResultCallback callback);

         void rtcJoin(in RTCJoin join,IResultCallback callback);
         void rtcJoined(in RTCJoined joined,IResultCallback callback);
         void rtcOffer(in RTCOffer offer,IResultCallback callback);
         void rtcAnswer(in RTCOffer offer,IResultCallback callback);
         void rtcCandidate(in RTCCandidate candidate,IResultCallback callback);
         void setRTCSignalMessageListener(in IRTCMessageListener lisntener);
         RTCServerConfig getRTCConfig();

         int updateAtMessageReadState(String messageId, String conversationId, int read);
         int clearAtMessage(String conversationId);
         int updateCustomMessage(String conversationId, String messageId, String content, String extra);

         void rtcVideoParam(in RTCVideoParam param,IResultCallback callback);
         void openDebugLog();
}