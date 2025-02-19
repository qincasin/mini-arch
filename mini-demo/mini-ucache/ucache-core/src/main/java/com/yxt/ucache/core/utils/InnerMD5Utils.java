package com.yxt.ucache.core.utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 util.
 */
@SuppressWarnings("PMD.ClassNamingShouldBeCamelRule")
public class InnerMD5Utils {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    private static final ThreadLocal<MessageDigest> MESSAGE_DIGEST_LOCAL = new ThreadLocal<MessageDigest>() {
        @Override
        protected MessageDigest initialValue() {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
        }
    };

    /**
     * Calculate MD5 hex string.
     *
     * @param bytes byte arrays
     * @return MD5 hex string of input
     * @throws NoSuchAlgorithmException if can't load md5 digest spi.
     */
    public static String md5Hex(byte[] bytes) throws NoSuchAlgorithmException {
        try {
            MessageDigest messageDigest = MESSAGE_DIGEST_LOCAL.get();
            if (messageDigest != null) {
                return encodeHexString(messageDigest.digest(bytes));
            }
            throw new NoSuchAlgorithmException("MessageDigest get MD5 instance error");
        } finally {
            MESSAGE_DIGEST_LOCAL.remove();
        }
    }

    /**
     * Calculate MD5 hex string with encode charset.
     *
     * @param value  value
     * @param encode encode charset of input
     * @return MD5 hex string of input
     */
    public static String md5Hex(String value, String encode) {
        try {
            return md5Hex(value.getBytes(encode));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将一个字节数组转化为可见的字符串.
     */
    public static String encodeHexString(byte[] bytes) {
        int l = bytes.length;

        char[] out = new char[l << 1];

        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & bytes[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & bytes[i]];
        }

        return new String(out);
    }

}
