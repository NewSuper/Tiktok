package com.aitd.module_chat.lib.provider

object CustomMessageManager {

    private var mProviderMap = HashMap<String, MessageProvider>()

    @JvmStatic
    fun registerMessageProvider(provider: MessageProvider) {
        mProviderMap[provider.providerTag] = provider
    }

    /**
     * 仅返回非notice的provider
     */
    @JvmStatic
    fun getMessageProvider(tag: String): MessageProvider? {
        if(mProviderMap[tag]?.isNotice() == false) {
            return  mProviderMap[tag]
        }
        return null
    }

    /**
     * 仅返回notice的provider
     */
    @JvmStatic
    fun getNoticeProvider(tag: String): MessageProvider? {
        if(mProviderMap[tag]?.isNotice() == true) {
            return  mProviderMap[tag]
        }
        return null
    }

    fun getBubbleStyle(tag : String) : MessageProvider.BubbleStyle?{
        return mProviderMap[tag]?.bubbleStyle
    }

}