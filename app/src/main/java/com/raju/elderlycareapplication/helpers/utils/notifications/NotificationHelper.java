package com.raju.elderlycareapplication.helpers.utils.notifications;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.raju.elderlycareapplication.authentication.elder.ElderHomeActivity;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationHelper {
    public static void sendNotification(Context context,String message){
        PreferenceManager preferenceManager = new PreferenceManager(context);
        String receiverToken = preferenceManager.getString(Constants.KEY_CARETAKER_FCM_TOKEN);
        if(receiverToken!=null){
            Log.d("Notification","Not null");
            try{
                Log.d("Notification","Inside Try");
                JSONArray tokens = new JSONArray();
                tokens.put(receiverToken);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID,preferenceManager.getString(Constants.KEY_ELDER_ID));
                data.put(Constants.KEY_NAME,preferenceManager.getString(Constants.KEY_ELDER_NAME));
                data.put(Constants.KEY_FCM_TOKEN,preferenceManager.getString(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_ALERT_MESSAGE,message);
                data.put(Constants.KEY_ELDER_PHONE,preferenceManager.getString(Constants.KEY_ELDER_PHONE));

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA,data);
                body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

                Log.d("Notification Body JSON",body.toString());
                sendingNotification(body.toString(),context);
            }
            catch (Exception e){
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                Log.e("Notification",e.getLocalizedMessage());
            }
        }
        else{
            Log.d("Notification","null");
        }
    }

    private static void sendingNotification(String body,Context context){
        ApiClient.getClient()
                .create(ApiService.class)
                .sendMessages(
                        Constants.getRemoteMsgHeaders(),
                        body
                ).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if(response.isSuccessful()){
                            Log.d("Notification","Success");
                            try{
                                Log.d("Notification","Inside Try at Send Notification");
                                if(response.body()!=null){
                                    Log.d("Notification","Body not null");
                                    JSONObject responseJson = new JSONObject();
                                    JSONArray results = responseJson.getJSONArray("results");
                                    if(responseJson.getInt("failure")==1){
                                        JSONObject error = (JSONObject) results.get(0);
                                        Log.d("Notification","Failure Error at Send Notification");
                                        return;
                                    }
                                }
                                else{
                                    Log.d("Notification","Body Null");
                                }
                            }
                            catch (Exception e){
                                Log.e("Notification","Error at Send Notification:"+e.getLocalizedMessage());
                            }
                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.d("Notification","Not Success"+response.code());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                        Log.d("Notification","Send Notification Failed :"+t.getLocalizedMessage());
                    }
                });
    }
}
