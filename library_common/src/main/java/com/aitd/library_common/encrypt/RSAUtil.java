package com.aitd.library_common.encrypt;

import android.util.Base64;

import com.blankj.utilcode.util.EncryptUtils;

/**
 * Author : palmer
 * Date   : 2021/7/1
 * E-Mail : lxlfpeng@163.com
 * Desc   : RSA加密封装类
 */

public class RSAUtil {
    private static final int KEYSIZE = 1024;
    private static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    //测试公钥
    public static final String test_pubick = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDHc+PP8LuTlBL1zCX+lh9kcur\n" +
            "gHHIXFnV/tDK789DaJuhwZvQ1lu5Zdcn+ULbNUKkB6b5tCP0sZxlpoCVKMyKHtde\n" +
            "h/YGXwBD8sMc+XcRs0eh3/tyr4EoBu3bomzHWDGmHjH/F5GotFTrGcB6xQwAROy4\n" +
            "mT5SketlQ3c7tucI+QIDAQAB";
    //测试私钥
    public static final String test_private = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMMdz48/wu5OUEvX\n" +
            "MJf6WH2Ry6uAcchcWdX+0Mrvz0Nom6HBm9DWW7ll1yf5Qts1QqQHpvm0I/SxnGWm\n" +
            "gJUozIoe116H9gZfAEPywxz5dxGzR6Hf+3KvgSgG7duibMdYMaYeMf8Xkai0VOsZ\n" +
            "wHrFDABE7LiZPlKR62VDdzu25wj5AgMBAAECgYBKcdxYrp5EaHLwjNlIk0ciGfeY\n" +
            "pvhC1yGbqY6mb1soQAhpbkJyKudyVG4EHXGpy6dyiEzoJxg063NdwWp7/sYTHk/N\n" +
            "13UzGTudIKuNacnJk0WKu4owQticC71ZIqUjSZgN0CiEKQ6YfoGOFTzeMqzVYQjI\n" +
            "mPzGdLK74y3YYlmigQJBAObzhzYlWjOypx3klmrPTu2KXPg3ATTEB8kN/isY8bYu\n" +
            "ikVdd2yUd0AvaC7PPwEEjGmsSrEeXw1tsVfZ8VkBaikCQQDYR0+8VzGLdgIFQc/6\n" +
            "+IY5fQlEt/Hc7qsi7JT4o+f+BGJlAT7+OeDMThavKdWq1UvZDyCKdtYRfxQ1jj7D\n" +
            "4yJRAkBrG6InkGcm9sHecTb5Ti+ypqq7Svc6O3fI3L51ylm/PhJOXSyXpLsxf0r3\n" +
            "+pGjrTJZh9gUEJvQpIDM13zA5JERAkBI2zTsED9baIRjuvjR5Xhp00oVARYTw76Y\n" +
            "xDOm0qgq9NUki1fqEhs9F60ikqgspS+oziS7IC8as8FeDS3tlQ0RAkA5OdDvhQRQ\n" +
            "PI75ULyHazTEm4Rak8TKmKl64pmnwcw4GS9fKWs7jRAuem1OtwA8HAqjaDeXC8Cd\n" +
            "6fDfq7z5bZnE";

    /**
     * 获取 RSA公钥加密后的字符串
     *
     * @param data
     * @return
     */
    public static String encryptByPublicKeyString(String data, String publicKey) {
        byte[] bytes = EncryptUtils.encryptRSA(
                data.getBytes(),
                Base64.decode(publicKey, Base64.DEFAULT),
                KEYSIZE,
                TRANSFORMATION
        );
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    /**
     * 获取 RSA公钥加密后的数组
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) {
        return EncryptUtils.encryptRSA(
                data,
                Base64.decode(publicKey, Base64.DEFAULT),
                KEYSIZE,
                TRANSFORMATION
        );
    }

    /**
     * 获取私钥解密后的Base64字符串
     *
     * @param data
     * @param privateKey
     * @return
     */
    public static String decryptByPrivateKey(String data, String privateKey) {
        byte[] decrypt = EncryptUtils.decryptRSA(
                Base64.decode(data, Base64.DEFAULT),
                Base64.decode(privateKey, Base64.DEFAULT),
                KEYSIZE,
                TRANSFORMATION
        );
        return new String(decrypt);
    }
}
