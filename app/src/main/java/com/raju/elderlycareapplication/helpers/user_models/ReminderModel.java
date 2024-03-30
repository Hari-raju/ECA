package com.raju.elderlycareapplication.helpers.user_models;

import java.io.Serializable;
import java.util.List;

public class ReminderModel implements Serializable {
    private String elderPhone;

    private String caretakerPhone;
    private String eldersNoOfMed;
    private List<MedReminderModel> listModel;

    public ReminderModel(){

    }
    public ReminderModel(String elderPhone, String caretakerPhone,String eldersNoOfMed, List<MedReminderModel> listModel) {
        this.elderPhone = elderPhone;
        this.eldersNoOfMed = eldersNoOfMed;
        this.listModel = listModel;
        this.caretakerPhone = caretakerPhone;
    }


    public String getCaretakerPhone() {
        return caretakerPhone;
    }

    public void setCaretakerPhone(String caretakerPhone) {
        this.caretakerPhone = caretakerPhone;
    }

    public String getElderPhone() {
        return elderPhone;
    }

    public void setElderPhone(String elderPhone) {
        this.elderPhone = elderPhone;
    }

    public String getEldersNoOfMed() {
        return eldersNoOfMed;
    }

    public void setEldersNoOfMed(String eldersNoOfMed) {
        this.eldersNoOfMed = eldersNoOfMed;
    }

    public List<MedReminderModel> getListModel() {
        return listModel;
    }

    public void setListModel(List<MedReminderModel> listModel) {
        this.listModel = listModel;
    }
}
