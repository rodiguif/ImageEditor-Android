package com.xinlan.imageeditlibrary.editimage.utils;

import static android.webkit.MimeTypeMap.getFileExtensionFromUrl;
import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;
import android.text.TextUtils;
import java.io.File;

/**
 * Created by panyi on 16/10/23.
 */
public class FileUtil {
    public static boolean checkFileExist(final String path) {
        if (TextUtils.isEmpty(path))
            return false;

        File file = new File(path);
        return file.exists();
    }

    /**
     * 将图片文件加入到相册
     *
     * @param context
     * @param dstPath
     */
    public static void albumUpdate(final Context context, final String dstPath) {
        if (TextUtils.isEmpty(dstPath) || context == null)
            return;

        File file = new File(dstPath);
        //System.out.println("panyi  file.length() = "+file.length());
        if (!file.exists() || file.length() == 0) {//文件若不存在  则不操作
            return;
        }

        ContentValues values = new ContentValues(2);
        String extensionName = getFileExtensionFromUrl(dstPath);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/" + (TextUtils.isEmpty(extensionName)
                || extensionName.equals("jpg") ? "jpeg" : extensionName));
        values.put(MediaStore.Images.Media.DATA, dstPath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
}//end class
