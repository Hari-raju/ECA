package com.raju.elderlycareapplication.helpers.user_models;


import java.io.Serializable;

public class ConnectModel implements Serializable {
    private String elderPhone;
    private String caretakerPhone;
    private String elderProfile;

    private String elderName;
    private String caretakerName;


    public ConnectModel(String elderPhone, String caretakerPhone) {
        this.elderPhone = elderPhone;
        this.caretakerPhone = caretakerPhone;
    }

    public String getElderPhone() {
        return elderPhone;
    }

    public void setElderPhone(String elderPhone) {
        this.elderPhone = elderPhone;
    }

    public String getCaretakerPhone() {
        return caretakerPhone;
    }

    public void setCaretakerPhone(String caretakerPhone) {
        this.caretakerPhone = caretakerPhone;
    }

    public String getElderProfile() {
        return elderProfile;
    }

    public void setElderProfile(String elderProfile) {
        this.elderProfile = elderProfile;
    }

    public String getElderName() {
        return elderName;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public String getCaretakerName() {
        return caretakerName;
    }

    public void setCaretakerName(String caretakerName) {
        this.caretakerName = caretakerName;
    }

}
