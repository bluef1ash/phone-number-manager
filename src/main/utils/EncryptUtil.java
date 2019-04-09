package utils;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * 加解密工具类
 *
 * @author 廿二月的天
 */
public class EncryptUtil {
    private static final String KEY_SHA = "SHA";
    private static final String KEY_MD5 = "MD5";
    /**
     * MAC算法可选以下多种算法
     * <p>
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     */
    private static final String KEY_MAC = "HmacMD5";

    /**
     * Base64编码表。
     */
    private static final char[] BASE64CODE = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    /**
     * Base64解码表。注意两个63，为兼容SMP，“/”和“-”都翻译成63。注意两个0：“A”和“=”都翻译成0。
     */
    private static final byte[] BASE64DECODE = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 63, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, 0, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1};

    private static final int HEX_255 = 0x0000ff;

    private static final int HEX_16515072 = 0xfc0000;

    private static final int HEX_258048 = 0x3f000;

    private static final int HEX_4032 = 0xfc0;

    private static final int HEX_63 = 0x3f;

    private static final int HEX_16711680 = 0xff0000;

    private static final int HEX_65280 = 0x00ff00;

    private static final int NUMBER_TWO = 2;

    private static final int NUMBER_THREE = 3;

    private static final int NUMBER_FOUR = 4;

    private static final int NUMBER_SIX = 6;

    private static final int NUMBER_EIGHT = 8;

    private static final int NUMBER_TWELVE = 12;

    private static final int NUMBER_SIXTEEN = 16;

    private static final int NUMBER_EIGHTEEN = 18;

    /**
     * Base64编码。将字节数组中字节3个一组编码成4个可见字符。
     *
     * @param b 需要被编码的字节数据。
     * @return 编码后的Base64字符串。
     */
    public static String encryptBASE64(byte[] b) {
        int code = 0;

        // 按实际编码后长度开辟内存，加快速度
        StringBuilder sb = new StringBuilder(((b.length - 1) / NUMBER_THREE) << NUMBER_TWO + NUMBER_FOUR);

        // 进行编码
        for (int i = 0; i < b.length; i++) {
            code |= (b[i] << (NUMBER_SIXTEEN - i % NUMBER_THREE * NUMBER_EIGHT))
                & (HEX_255 << (NUMBER_SIXTEEN - i % NUMBER_THREE * NUMBER_EIGHT));
            if (i % NUMBER_THREE == NUMBER_TWO || i == b.length - 1) {
                sb.append(BASE64CODE[(code & HEX_16515072) >>> NUMBER_EIGHTEEN]);
                sb.append(BASE64CODE[(code & HEX_258048) >>> NUMBER_TWELVE]);
                sb.append(BASE64CODE[(code & HEX_4032) >>> NUMBER_SIX]);
                sb.append(BASE64CODE[code & HEX_63]);
                code = 0;
            }
        }
        // 对于长度非3的整数倍的字节数组，编码前先补0，编码后结尾处编码用=代替，=的个数和短缺的长度一致，以此来标识出数据实际长度
        if (b.length % NUMBER_THREE > 0) {
            sb.setCharAt(sb.length() - 1, '=');
        }
        if (b.length % NUMBER_THREE == 1) {
            sb.setCharAt(sb.length() - NUMBER_TWO, '=');
        }
        return sb.toString();
    }

    /**
     * Base64解码。
     *
     * @param code 用Base64编码的ASCII字符串
     * @return 解码后的字节数据
     */
    public static byte[] decryptBASE64(String code) {
        // 检查参数合法性
        if (code == null) {
            return null;
        }
        int len = code.length();
        if (len % NUMBER_FOUR != 0) {
            throw new IllegalArgumentException("Base64 string length must be 4*n");
        }
        if (code.length() == 0) {
            return new byte[0];
        }
        // 统计填充的等号个数
        int pad = 0;
        if (code.charAt(len - 1) == '=') {
            pad++;
        }
        if (code.charAt(len - NUMBER_TWO) == '=') {
            pad++;
        }

        // 根据填充等号的个数来计算实际数据长度
        int retLen = len / NUMBER_FOUR * NUMBER_THREE - pad;

        // 分配字节数组空间
        byte[] ret = new byte[retLen];

        // 查表解码
        char ch1, ch2, ch3, ch4;
        int i;
        for (i = 0; i < len; i += NUMBER_FOUR) {
            int j = i / NUMBER_FOUR * NUMBER_THREE;
            ch1 = code.charAt(i);
            ch2 = code.charAt(i + 1);
            ch3 = code.charAt(i + NUMBER_TWO);
            ch4 = code.charAt(i + NUMBER_THREE);
            int tmp = (BASE64DECODE[ch1] << NUMBER_EIGHTEEN) | (BASE64DECODE[ch2] << NUMBER_TWELVE)
                | (BASE64DECODE[ch3] << NUMBER_SIX) | (BASE64DECODE[ch4]);
            ret[j] = (byte) ((tmp & HEX_16711680) >> NUMBER_SIXTEEN);
            if (i < len - NUMBER_FOUR) {
                ret[j + 1] = (byte) ((tmp & HEX_65280) >> NUMBER_EIGHT);
                ret[j + NUMBER_TWO] = (byte) ((tmp & HEX_255));

            } else {
                if (j + 1 < retLen) {
                    ret[j + 1] = (byte) ((tmp & HEX_65280) >> NUMBER_EIGHT);
                }
                if (j + NUMBER_TWO < retLen) {
                    ret[j + NUMBER_TWO] = (byte) ((tmp & HEX_255));
                }
            }
        }
        return ret;
    }

    /**
     * MD5加密
     *
     * @param data 要加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String encryptMD5(String data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance(KEY_MD5);
        md5.update(data.getBytes());
        return new String(md5.digest());
    }

    /**
     * SHA加密
     *
     * @param data 要加密的字符串
     * @return 加密后的字符串
     * @throws Exception
     */
    public static byte[] encryptSHA(byte[] data) throws Exception {
        MessageDigest sha = MessageDigest.getInstance(KEY_SHA);
        sha.update(data);
        return sha.digest();
    }

    /**
     * 初始化HMAC密钥
     *
     * @return BASE64加密后的字符串
     * @throws Exception
     */
    public static String initMacKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_MAC);
        SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }

    /**
     * HMAC加密
     *
     * @param data 要加密的字符数组
     * @param key  加密的密钥
     * @return 加密后的字符串
     * @throws Exception
     */
    public static byte[] encryptHMAC(byte[] data, String key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), KEY_MAC);
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }
}
