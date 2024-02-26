package com.raju.elderlycareapplication.helpers.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EncoderDecoder {
    public static byte[] getBitmapBytes(Bitmap bitmap,int quality){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();
    }

    public static String encodeImage(Uri url, Context context){
        Bitmap bitmap = getBitmap(url,context);
        byte[] bArray = getBitmapBytes(bitmap,100);
        return Base64.encodeToString(bArray,Base64.DEFAULT);
    }

    public static Bitmap decodeImage(String img){
        byte[] bArray = Base64.decode(img,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bArray,0,bArray.length);
    }

    public static Bitmap getBitmap(Uri imgUrl, Context context){
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(imgUrl);
        } catch (FileNotFoundException e) {
            Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        return BitmapFactory.decodeStream(inputStream);
    }
}
