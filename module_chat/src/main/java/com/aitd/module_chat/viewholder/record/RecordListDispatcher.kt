package com.aitd.module_chat.viewholder.record

import com.aitd.module_chat.viewholder.BaseContentViewDispatcher

//聊天记录content view 分发器
object RecordListDispatcher : BaseContentViewDispatcher() {
    override fun createTextHandler(): RecordBaseMessageHandler? {
        return RecordTextMessageHandler()
    }

    override fun createImageHandler(): RecordBaseMessageHandler? {
        return RecordImageMessageHandler()
    }

    override fun createImageTextHandler(): RecordBaseMessageHandler? {
        return RecordImageTextMessageHandler()
    }

    override fun createAudioHandler(): RecordBaseMessageHandler? {
        return RecordVoiceMessageHandler()
    }

    override fun createVideoHandler(): RecordBaseMessageHandler? {
        return RecordVideoMessageHandler()
    }

    override fun createCallHandler(): RecordBaseMessageHandler? {
        return RecordCallMessageHandler()
    }

    override fun createFileHandler(): RecordBaseMessageHandler? {
        return RecordFileMessageHandler()
    }

    override fun createGeoHandler(): RecordBaseMessageHandler? {
        return RecordGeoMessageHandler()
    }

    override fun createRecordHandler(): RecordBaseMessageHandler? {
        return RecordRecordMessageHandler()
    }

    override fun createCustomHandler(): RecordBaseMessageHandler? {
        return RecordCustomMessageHandler()
    }

}