package com.aitd.library_common.encrypt.aes;

/**
 * 数据解密接口
 *
 * @author wuxie
 */
public interface DataDecrypt {

    /**
     * 解密字符串
     */
    String decrypt(String encryptStr, String secretKey);

    /**
     * 解密字节数组
     */
    byte[] decrypt(byte[] encryptBytes, byte[] secretKey);
}
