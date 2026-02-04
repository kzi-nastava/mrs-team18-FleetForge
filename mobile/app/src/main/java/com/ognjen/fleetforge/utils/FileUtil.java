package com.ognjen.fleetforge.utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        String mimeType = context.getContentResolver().getType(uri);

        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);

        if (extension == null) {
            extension = "tmp";
        }

        File tempFile = File.createTempFile("upload_", "." + extension, context.getCacheDir());
        tempFile.deleteOnExit();

        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return tempFile;
    }
}
