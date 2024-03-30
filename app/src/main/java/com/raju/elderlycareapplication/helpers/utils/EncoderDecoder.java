package com.raju.elderlycareapplication.helpers.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;

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

    //Time Picker to String
    public static String timePickerToString(MaterialTimePicker timePicker) {
        // Get the selected hour and minute from the TimePicker
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        // Format the hour and minute into a String
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
    }

    //String to Time Picker Obj
    public static MaterialTimePicker stringToTimePicker(String time) {
        // Split the time string into hour and minute components
        String[] timeComponents = time.split(":");
        int hour = Integer.parseInt(timeComponents[0]);
        int minute = Integer.parseInt(timeComponents[1]);

        // Create a MaterialTimePicker instance with the specified hour and minute
        MaterialTimePicker.Builder builder = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hour)
                .setMinute(minute);

        // You may set other properties if needed, such as title, etc.
        // builder.setTitle("Select Time");

        return builder.build();
    }

}
