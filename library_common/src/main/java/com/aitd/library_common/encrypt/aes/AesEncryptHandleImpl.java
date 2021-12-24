package com.aitd.library_common.encrypt.aes;


import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * aes加解密
 *
 * @author wuxie
 */
public class AesEncryptHandleImpl extends EncryptHandle {

    private final String keySeed;


    public AesEncryptHandleImpl(String keySeed, String algorithm, String encoding) {
        super(EncryptConstants.TYPE_AES, algorithm, encoding);
        this.keySeed = keySeed;
    }

    public AesEncryptHandleImpl(String algorithm) {
        this(null, algorithm, Charsets.UTF8);
    }

    public AesEncryptHandleImpl() {
        this(null, EncryptConstants.ALGORITHM_AES_ECB_PKCS5, Charsets.UTF8);
    }

    public static void main(String[] args) throws Exception {
        String key = "aesiptvgswwcode1";
        // 1234567898765432
        // "d7b85f6e214abcda";
        // DAF3100DCD39CAEB5DD35E9651712A86
        String content = "jpc";
        System.out.println("加密前：" + content);

        TimeRecorder timeRecorder = TimeRecorder.start();

        AesEncryptHandleImpl aesEncrypt = new AesEncryptHandleImpl(EncryptConstants.ALGORITHM_AES_ECB_PKCS5);

        //System.out.println(aesEncrypt.generateKey());

        String encrypt = aesEncrypt.encrypt(content, key);
        System.out.println(encrypt.length() + ":加密后：" + encrypt);

        String decrypt = aesEncrypt.decrypt(encrypt, key);
        System.out.println("解密后：" + decrypt);

        timeRecorder.end();
        System.out.println("用时：" + String.valueOf(timeRecorder.getRunMillis()));
    }

    @Override
    protected Key buildKey(byte[] secretKey) {
        return new SecretKeySpec(secretKey, cryptoType);
    }

    public byte[] generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(EncryptConstants.TYPE_AES);
            if (this.keySeed != null && this.keySeed.length() > 0) {
                keyGenerator.init(128, new SecureRandom(keySeed.getBytes(this.encoding)));
            } else {
                keyGenerator.init(128);
            }
            SecretKey original_key = keyGenerator.generateKey();
            byte[] raw = original_key.getEncoded();
            return raw;
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptException(e);
        } catch (UnsupportedEncodingException e) {
            throw new EncryptException(e);
        }
    }
}
