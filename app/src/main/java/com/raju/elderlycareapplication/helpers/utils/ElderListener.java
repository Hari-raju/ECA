package com.raju.elderlycareapplication.helpers.utils;

import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;

public interface ElderListener {
    void onElderClicked(ConnectedElderModel elderModel,int value);
}
