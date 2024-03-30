package com.raju.elderlycareapplication.helpers.user_models;


import java.io.Serializable;

public class ConnectedElderModel implements Serializable {
    private String elderPhone;
    private String elderName;
    private String elderProfile;

    public ConnectedElderModel(String elderPhone, String elderName,String elderProfile) {
        this.elderPhone = elderPhone;
        this.elderName = elderName;
        this.elderProfile = elderProfile;
    }

    public String getElderPhone() {
        return elderPhone;
    }

    public String getElderName() {
        return elderName;
    }

    public String getElderProfile() {
        return elderProfile;
    }
}
