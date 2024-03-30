package com.raju.elderlycareapplication.helpers.utils;

import java.util.HashMap;

public class Constants {
    public static String KEY_PREFERENCE_NAME = "elderlyCareAppPreference";
    public static String KEY_PASSWORD = "password";

    //Elders Collection
    public static String KEY_ELDER_COLLECTION = "elders";
    public static String KEY_ELDER_NAME = "elderName";
    public static String KEY_ELDER_PHONE = "elderPhone";
    public static String KEY_ELDER_AGE = "elderAge";
    public static String KEY_ELDER_DESCRIPTION = "elderDescription";
    public static String KEY_ELDER_DOB = "date_of_birth";
    public static String KEY_IS_ELDER_SIGNED_IN = "isElderSignedIn";
    public static String KEY_ELDER_ID="elderId";
    public static String KEY_ELDER_PROFILE = "elderProfile";

    //Caretakers Collection
    public static String KEY_CARETAKER_COLLECTION = "caretakers";
    public static String KEY_CARETAKER_NAME = "caretakerName";
    public static String KEY_CARETAKER_PHONE = "caretakerPhone";
    public static String KEY_CARETAKER_PROFILE = "caretakerProfile";
    public static String KEY_CARETAKER_ID = "caretakerId";
    public static String KEY_IS_CARETAKER_SIGNED_IN = "isCaretakerSignedIn";

    //Reminders Collection

    public static String KEY_CONNECT_COLLECTION = "connected";
    public static String KEY_CONNECTED_CARETAKER = "connectedCaretaker";

    public static String KEY_MEDICATION_PIC = "eldersMedPic";
    public static String KEY_MEDICATION_NAME = "eldersMedName";
    public static String KEY_MEDICATION_PURPOSE = "eldersMedPurpose";
    public static String KEY_NO_OF_MEDICATION = "eldersNoOfMed";
    public static String KEY_ELDER_MED_REM_COLLECTION = "reminder";
    public static String KEY_MED_REM_TIME = "eldersMedTime";
    public static String KEY_MED_FOLDER = "medicineFolder";
    public static String KEY_CHECK_START_TIME = "checkStartTime";
    public static String KEY_CHECK_END_TIME = "checkEndTime";
    public static String KEY_CHECK_COLLECTION = "checkReminder";
    public static String KEY_ELDER_MEDICINE ="elderMedicine";

    public static String KEY_FCM_TOKEN = "fcmToken";
    public static String KEY_CARETAKER_FCM_TOKEN = "caretakerToken";

    //Transmitting Values
    public static String KEY_USER_ID = "userId";
    public static String KEY_NAME = "name";
    public static final String KEY_ALERT_MESSAGE="message";
    public static final String KEY_WHAT_TO_DO = "whatToDo";


    //Retrofit Server Keys
    public static final String REMOTE_MSG_AUTHORIZATION="Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE="Content-Type";
    public static final String REMOTE_MSG_DATA="data";
    public static final String REMOTE_MSG_REGISTRATION_IDS="registration_ids";
    public static HashMap<String,String> remoteMsgHeaders=null;


    public static HashMap<String,String> getRemoteMsgHeaders(){
        if(remoteMsgHeaders==null){
            remoteMsgHeaders=new HashMap<>();
            remoteMsgHeaders.put(
                    Constants.REMOTE_MSG_AUTHORIZATION,
                    "key=AAAAOwXeD6Q:APA91bF8w2qkhrIMMpNZFQuHX8vJBfbZ8sggVJWHb6dcGd70DKPL5QmDif6FlUB6ycokZqaveGHTtvYJG2gHqEuTUMAMWXzlFJoD0SVfunYGNFCT7xrg0pHggaMjeFvmZ6aigUg65hnU"
            );
            remoteMsgHeaders.put(
                    Constants.REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );
        }
        return remoteMsgHeaders;
    }

}
