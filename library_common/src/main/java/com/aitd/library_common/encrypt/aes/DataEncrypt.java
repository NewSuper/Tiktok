package com.aitd.library_common.encrypt.aes;

/**
 * 数据解密接口
 *
 * @author wuxie
 */
public interface DataEncrypt {

    /**
     * 加密字符串
     */
    String encrypt(String sourceStr, String secretKey);

    /**
     * 加密字符数组
     */
    byte[] encrypt(byte[] sourceBytes, byte[] secretKey);
}
