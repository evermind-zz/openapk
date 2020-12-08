package com.dkanada.openapk.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class MD5 {
    public static byte[] bufferToMD5(byte[] buffer) {
        InputStream inputStream = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(buffer, 0, buffer.length);
            byte [] md5Bytes = digest.digest();
            return md5Bytes;
        } catch (Exception e) {
            return null;
        }
    }

    public static byte[] fileToMD5(String filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] md5Bytes = digest.digest();
            return md5Bytes;
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) { }
            }
        }
    }

    public static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal.toUpperCase();
    }
}
