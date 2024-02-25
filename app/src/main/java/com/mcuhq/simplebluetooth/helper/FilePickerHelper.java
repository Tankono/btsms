package com.mcuhq.simplebluetooth.helper;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.util.Objects;

public class FilePickerHelper {
    static String PRIMARY_DOCUMENT = "primary";
    static String BLANK_DOCUMENT = "";

    static String EXTERNAL_STORAGE_DOCUMENT = "com.android.externalstorage.documents";
    static String DOWNLOADS_DOCUMENT = "com.android.providers.downloads.documents";
    static String MEDIA_DOCUMENT = "com.android.providers.media.documents";

    static String IMAGE = "image";
    static String VIDEO = "video";
    static String AUDIO = "audio";
    static String CONTENT = "content";
    static String FILE = "file";

    static String PUBLIC_DOWNLOADS_LOCATION = "content://downloads/public_downloads";
    public static String getPath(Context context, Uri uri) {
        //content://com.android.providers.media.documents/document/image%3A50395
        if (DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String documentId = DocumentsContract.getDocumentId(uri);
                String[] split = documentId.split(":");

//                        .dropLastWhile {
//                    it.isEmpty();
//                }.toTypedArray();

                String type = split[0];
                if (PRIMARY_DOCUMENT.equalsIgnoreCase(type)) {
                    return (BLANK_DOCUMENT + Environment.getDownloadCacheDirectory() + "/" + split[1]);
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse(PUBLIC_DOWNLOADS_LOCATION), Long.valueOf(id)
                );
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String documentId = DocumentsContract.getDocumentId(uri);
                String[] split = documentId.split(":");
//                String[] split = documentId.split(":".toRegex()).dropLastWhile {
//                    it.isEmpty()
//                }.toTypedArray()

                String type = split[0];
                Uri contentUri = null;
                if(type.equalsIgnoreCase(IMAGE)){
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if (type.equalsIgnoreCase(VIDEO)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if (type.equalsIgnoreCase(AUDIO)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = {split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (CONTENT.equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if (FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return "";
    }

    private static String getDataColumn(
            Context context, Uri uri,
            String selection, String[] selectionArgs
    ) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(
                    uri, projection,
                    selection, selectionArgs, null
                );
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(columnIndex);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
//            cursor.close();
        }
        return null;
    }

    private static boolean isMediaDocument(Uri uri) {
        return Objects.equals(MEDIA_DOCUMENT, uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return Objects.equals(DOWNLOADS_DOCUMENT, uri.getAuthority());
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return Objects.equals(EXTERNAL_STORAGE_DOCUMENT, uri.getAuthority());
    }
}
