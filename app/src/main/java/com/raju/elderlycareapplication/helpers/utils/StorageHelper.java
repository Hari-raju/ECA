package com.raju.elderlycareapplication.helpers.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

public class StorageHelper {
    public static void uploadImageToStorage(Uri url, String elderId, final Callback callback, Context context) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String randomId = UUID.randomUUID().toString();
        Bitmap bitmap = getBitmap(url,context);
        byte[] image = getBitmapBytes(bitmap);
        StorageReference reference = storage.getReference()
                .child(Constants.KEY_MED_FOLDER)
                .child(elderId)
                .child(randomId);

        UploadTask task = reference.putBytes(image);
        task.addOnSuccessListener(
                        success -> reference.getDownloadUrl().addOnSuccessListener(
                                downloadedUrl -> callback.onSuccess(downloadedUrl.toString())
                        )
                )
                .addOnFailureListener(fail ->
                        Log.e("Error", Objects.requireNonNull(fail.getLocalizedMessage()))
                );
    }

    private static Bitmap getBitmap(Uri url,Context context) {
        Bitmap bitmap = null;
        try {
            InputStream inputStream =context.getContentResolver().openInputStream(url);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("Error", Objects.requireNonNull(e.getLocalizedMessage()));
        }
        return bitmap;
    }

    private static byte[] getBitmapBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }
}
