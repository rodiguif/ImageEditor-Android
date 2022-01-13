package com.xinlan.imageeditlibrary.editimage.utils

import android.text.TextUtils
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.webkit.MimeTypeMap
import android.provider.MediaStore
import com.xinlan.imageeditlibrary.R
import java.io.File
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider


/**
 * Created by panyi on 16/10/23.
 */
object FileUtil {
    fun checkFileExist(path: String?): Boolean {
        if (TextUtils.isEmpty(path)) return false
        val file = File(path)
        return file.exists()
    }

    /**
     * 将图片文件加入到相册
     *
     * @param context
     * @param dstPath
     */
    @JvmStatic
    fun ablumUpdate(context: Context?, dstPath: String?) {
        if (TextUtils.isEmpty(dstPath) || context == null) return
        val file = File(dstPath)
        //System.out.println("panyi  file.length() = "+file.length());
        if (!file.exists() || file.length() == 0L) { //文件若不存在  则不操作
            return
        }
        val values = ContentValues(2)
        val extensionName = MimeTypeMap.getFileExtensionFromUrl(dstPath)
        values.put(
            MediaStore.Images.Media.MIME_TYPE, "image/" + if (TextUtils.isEmpty(extensionName)
                || extensionName == "jpg"
            ) "jpeg" else extensionName
        )
        values.put(MediaStore.Images.Media.DATA, dstPath)
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }

    fun albumUpdate(context: Context, dstPath: String) {
        if (TextUtils.isEmpty(dstPath)) return
        val imageFile = File(dstPath)
        val bmOptions = BitmapFactory.Options()
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, bmOptions)

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val date = System.currentTimeMillis()
        //val extension = Utils.getImageExtension(format)
        val extension = MimeTypeMap.getFileExtensionFromUrl(dstPath)
        val mimeType = if (TextUtils.isEmpty(extension) || extension == "jpg") "jpeg" else extension

        val newImage = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/$mimeType")
            put(MediaStore.MediaColumns.DATE_ADDED, date)
            put(MediaStore.MediaColumns.DATE_MODIFIED, date)
            put(MediaStore.MediaColumns.SIZE, bitmap.byteCount)
            put(MediaStore.MediaColumns.WIDTH, bitmap.width)
            put(MediaStore.MediaColumns.HEIGHT, bitmap.height)
        }

        val uri = context.contentResolver.insert(collection, newImage)

        context.contentResolver.openOutputStream(uri!!, "w").use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    fun saveImage(context: Context, bitmap: Bitmap, format: Bitmap.CompressFormat, dstPath: String) {
        if (TextUtils.isEmpty(dstPath)) return
        val imageFile = File(dstPath)

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val date = System.currentTimeMillis()
        val extension = getImageExtension(format)

        val newImage = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/$extension")
            put(MediaStore.MediaColumns.DATE_ADDED, date)
            put(MediaStore.MediaColumns.DATE_MODIFIED, date)
            put(MediaStore.MediaColumns.SIZE, bitmap.byteCount)
            put(MediaStore.MediaColumns.WIDTH, bitmap.width)
            put(MediaStore.MediaColumns.HEIGHT, bitmap.height)
        }

        val uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".fileProvider",
            imageFile)

        context.contentResolver.delete(uri, null, null)
        imageFile.delete()

        val newImageUri = context.contentResolver.insert(collection, newImage)

        context.contentResolver.openOutputStream(newImageUri!!, "rw").use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
        }


        //context.contentResolver.update(newImageUri, newImage, null, null)
    }

    fun getImageExtension(format: Bitmap.CompressFormat): String {
        return when (format) {
            Bitmap.CompressFormat.PNG -> {
                "png"
            }
            Bitmap.CompressFormat.JPEG -> {
                "jpeg"
            }
            else -> {
                "jpeg"
            }
        }
    }
    fun getImageFormat(type: String): Bitmap.CompressFormat {
        return when (type) {
            Bitmap.CompressFormat.PNG.name -> {
                Bitmap.CompressFormat.PNG
            }
            Bitmap.CompressFormat.JPEG.name -> {
                Bitmap.CompressFormat.JPEG
            }
            else -> {
                Bitmap.CompressFormat.JPEG
            }
        }
    }
} //end class
