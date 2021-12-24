package com.aitd.library_common.encrypt.aes;


import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * 加密处理
 *
 * @author wuxie
 */
public abstract class EncryptHandle implements DataEncrypt, DataDecrypt {

    protected final String cryptoType;
    /**
     * 算法
     */
    protected final String algorithm;
    protected final String encoding;

    public EncryptHandle(String cryptoType, String algorithm, String encoding) {
        this.cryptoType = cryptoType;
        this.algorithm = algorithm;
        this.encoding = encoding;
    }

    protected abstract Key buildKey(byte[] secretKey);

    /**
     * 加解密处理
     */
    protected byte[] doFinal(byte[] bytes, int mode, byte[] secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(this.algorithm);
            cipher.init(mode, buildKey(secretKey));
            return cipher.doFinal(bytes);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptException(e);
        } catch (BadPaddingException e) {
            throw new EncryptException(e);
        } catch (NoSuchPaddingException e) {
            throw new EncryptException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptException(e);
        } catch (InvalidKeyException e) {
            throw new EncryptException(e);
        }
    }

    @Override
    public byte[] encrypt(byte[] sourceBytes, byte[] secretKey) {
        return doFinal(sourceBytes, Cipher.ENCRYPT_MODE, secretKey);
    }

    @Override
    public String encrypt(String sourceStr, String secretKey) {
        try {
            byte[] sourceBytes = sourceStr.getBytes(encoding);
            return Base64Utils.encode(encrypt(sourceBytes, secretKey.getBytes(this.encoding)), encoding);
        } catch (Exception e) {
            return sourceStr;
        }
    }

    @Override
    public String decrypt(String encryptStr, String secretKey) {
        try {
            byte[] encryptBytes = Base64Utils.decode(encryptStr, encoding);
            return new String(decrypt(encryptBytes, secretKey.getBytes(this.encoding)), encoding);
        } catch (Exception e) {
            return encryptStr;
        }
    }

    @Override
    public byte[] decrypt(byte[] encryptBytes, byte[] secretKey) {
        return doFinal(encryptBytes, Cipher.DECRYPT_MODE, secretKey);
    }
}
