package com.raju.elderlycareapplication.helpers.utils;

public class Validation {
    public static Boolean isNameValid(String name) {
        return name.trim().length() >= 3;
    }

    public static Boolean isPassValid(String password) {
        String pattern = "^" + "(?=.*[a-zA-Z])" + "(?=\\S+$)" + ".{6,}" + "$";
        return !password.isEmpty() && password.matches(pattern);
    }

    public static Boolean isConfirmPassValid(String password, String confirmPass) {
        String pattern = "^" + "(?=.*[a-zA-Z])" + "(?=\\S+$)" + ".{6,}" + "$";
        return !password.isEmpty() && password.matches(pattern) && confirmPass.equals(password);
    }

}
