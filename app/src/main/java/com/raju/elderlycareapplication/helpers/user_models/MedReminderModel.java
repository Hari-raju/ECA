package com.raju.elderlycareapplication.helpers.user_models;

import java.io.Serializable;

public class MedReminderModel implements Serializable {
    private String eldersMedPic;
    private String eldersMedPurpose;
    private String eldersMedName;
    private String eldersMedTime;

    public String getEldersMedPic() {
        return eldersMedPic;
    }

    public void setEldersMedPic(String eldersMedPic) {
        this.eldersMedPic = eldersMedPic;
    }

    public MedReminderModel(){}

    public MedReminderModel(String eldersMedPic, String eldersMedPurpose, String eldersMedName, String eldersMedTime) {
        this.eldersMedPic = eldersMedPic;
        this.eldersMedPurpose = eldersMedPurpose;
        this.eldersMedName = eldersMedName;
        this.eldersMedTime = eldersMedTime;
    }


    public String getEldersMedPurpose() {
        return eldersMedPurpose;
    }

    public void setEldersMedPurpose(String eldersMedPurpose) {
        this.eldersMedPurpose = eldersMedPurpose;
    }

    public String getEldersMedName() {
        return eldersMedName;
    }

    public void setEldersMedName(String eldersMedName) {
        this.eldersMedName = eldersMedName;
    }

    public String getEldersMedTime() {
        return eldersMedTime;
    }

    public void setEldersMedTime(String eldersMedTime) {
        this.eldersMedTime = eldersMedTime;
    }
}
