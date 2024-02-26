package com.raju.elderlycareapplication.helpers.user_models;

import java.io.Serializable;

public class Elder_Model  implements Serializable {
    private String elderName;
    private String elderPhone;
    private String password;
    private String date_of_birth;
    private String elderAge;

    private String elderDescription;
    private String elderProfile;

    public Elder_Model(){
      //Empty Constructor
    }



    public Elder_Model(String elderPhone, String password, String elderName, String date_of_birth, String elderDescription, String elderProfile) {
        this.elderPhone=elderPhone;
        this.elderName=elderName;
        this.password=password;
        this.date_of_birth=date_of_birth;
        this.elderDescription = elderDescription;
        this.elderProfile=elderProfile;
    }
    public Elder_Model(String elderPhone,String password,String elderName) {
        this.elderPhone=elderPhone;
        this.elderName=elderName;
        this.password=password;
    }

    public Elder_Model(String date_of_birth,String elderDescription) {
        this.date_of_birth=date_of_birth;
        this.elderDescription = elderDescription;
    }

    public Elder_Model(String elderProfile){
        this.elderProfile=elderProfile;
    }

    public void setElderDescription(String elderDescription) {
        this.elderDescription = elderDescription;
    }

    public String getElderDescription() {
        return elderDescription;
    }

    public void setElderName(String elderName) {
        this.elderName = elderName;
    }

    public void setElderProfile(String elderProfile) {
        this.elderProfile = elderProfile;
    }

    public String getElderProfile() {
        return elderProfile;
    }

    public void setElderPhone(String elderPhone) {
        this.elderPhone = elderPhone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDate_of_birth(String date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public void setElderAge(String elderAge) {
        this.elderAge = elderAge;
    }

    public String getElderName() {
        return elderName;
    }

    public String getElderPhone() {
        return elderPhone;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getPassword() {
        return password;
    }

    public String getElderAge() {
        return elderAge;
    }
}
