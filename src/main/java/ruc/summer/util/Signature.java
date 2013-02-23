package ruc.summer.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Signature {

    public static byte[] calculate(byte[] data) {
        if (data == null) {
            return MD5Hash.digest("null".getBytes()).getDigest();
        } else {
            return MD5Hash.digest(data).getDigest();
        }
    }

    public static String getMd5String(byte[] data) {
        return asHex(calculate(data));
    }
    
    public static String getMd5String(String data) {
        try {
            return asHex(calculate(data.getBytes("utf8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return asHex(MD5Hash.digest("null".getBytes()).getDigest());
        }
    }
    
    private static final char[] HEX_CHARS = {'0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F',};

    /**
     * Turns array of bytes into string representing each byte as
     * unsigned hex number.
     * 
     * @param hash Array of bytes to convert to hex-string
     * @return Generated hex string
     */
    public static String asHex (byte hash[]) {
        char buf[] = new char[hash.length * 2];
        for (int i = 0, x = 0; i < hash.length; i++) {
            buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
            buf[x++] = HEX_CHARS[hash[i] & 0xf];
        }
        return new String(buf);
    }

    //SHA1 Processing
    public static String sha1(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(inStr.getBytes());
            outStr = bytetoString(digest);
        } catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }


    /**
     * @ TODO Why method bytetoString is different with asHex
     * @param digest
     * @return
     */
    public static String bytetoString(byte[] digest) {
        String str = "";
        String tempStr = "";

        for (int i = 1; i < digest.length; i++) {
            tempStr = (Integer.toHexString(digest[i] & 0xff));
            if (tempStr.length() == 1) {
                str = str + "0" + tempStr;
            } else {
                str = str + tempStr;
            }
        }
        return str.toLowerCase();
    }

    public static void main(String[] args) {
        System.out.println(sha1("11111eeefef1dsfsdfdsfdsfdsfdsfdsfds1").length());
        System.out.println(sha1("test"));
    }

}
