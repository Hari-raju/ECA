package com.raju.elderlycareapplication.helpers.user_models;

import java.io.Serializable;

public class CaretakerModel implements Serializable{
    private String caretakerName;
    private String caretakerPhone;
    private String password;
    private String caretakerProfile;

    public CaretakerModel(String caretakerName, String caretakerPhone, String password, String caretakerProfile) {
        this.caretakerName = caretakerName;
        this.caretakerPhone = caretakerPhone;
        this.password = password;
        this.caretakerProfile = caretakerProfile;
    }

    public CaretakerModel(String caretakerName, String caretakerPhone, String password) {
        this.caretakerName = caretakerName;
        this.caretakerPhone = caretakerPhone;
        this.password = password;
    }

    public String getCaretakerName() {
        return caretakerName;
    }

    public void setCaretakerName(String caretakerName) {
        this.caretakerName = caretakerName;
    }

    public String getCaretakerPhone() {
        return caretakerPhone;
    }

    public void setCaretakerPhone(String caretakerPhone) {
        this.caretakerPhone = caretakerPhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaretakerProfile() {
        return caretakerProfile;
    }

    public void setCaretakerProfile(String caretakerProfile) {
        this.caretakerProfile = caretakerProfile;
    }
}
