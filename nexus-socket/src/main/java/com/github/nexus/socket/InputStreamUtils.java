package com.github.nexus.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class InputStreamUtils {

    //buffer size in bytes
    private static final int BUFFER_SIZE = 8192;

    public static byte[] readAllBytes(final InputStream is) throws IOException {

        final ByteArrayOutputStream os = new ByteArrayOutputStream(BUFFER_SIZE);

        int read = BUFFER_SIZE;

        while(read == BUFFER_SIZE) {

            final byte[] buff = new byte[BUFFER_SIZE];
            read = is.read(buff);

            os.write(Arrays.copyOf(buff, read));
        }

        return os.toByteArray();
    }

}
