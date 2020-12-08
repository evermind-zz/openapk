package com.dkanada.openapk.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZIPByteCompressor {

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(data);
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    public static byte[] decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 1024;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);

        byte[] buffer = new byte[BUFFER_SIZE];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int len;
        while ((len = gis.read(buffer)) > 0) {
            out.write(buffer, 0, len);
        }

        gis.close();
        out.close();
        return out.toByteArray();
    }
}
