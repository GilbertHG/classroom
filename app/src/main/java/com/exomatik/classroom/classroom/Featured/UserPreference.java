package com.exomatik.classroom.classroom.Featured;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreference {
    private String KEY_UMUR = "umur";
    private String KEY_ALAMAT = "alamat";
    private String KEY_JK = "jk";
    private String KEY_NAME = "name";
    private String KEY_PHONE = "phone";
    private String KEY_USERNAME = "username";
    private String KEY_UID = "uid";
    private String KEY_EMAIL = "email";
    private String KEY_FOTO = "foto";
    private SharedPreferences preferences;

    public UserPreference(Context paramContext) {
        this.preferences = paramContext.getSharedPreferences("UserPref", 0);
    }

    public int getKEY_UMUR() {
        return this.preferences.getInt(this.KEY_UMUR, 0);
    }

    public String getKEY_ALAMAT() {
        return this.preferences.getString(this.KEY_ALAMAT, null);
    }

    public String getKEY_FOTO() {
        return this.preferences.getString(this.KEY_FOTO, null);
    }

    public String getKEY_EMAIL() {
        return this.preferences.getString(this.KEY_EMAIL, null);
    }

    public String getKEY_JK() {
        return this.preferences.getString(this.KEY_JK, null);
    }

    public String getKEY_NAME() {
        return this.preferences.getString(this.KEY_NAME, null);
    }

    public String getKEY_UID() {
        return this.preferences.getString(this.KEY_UID, null);
    }

    public String getKEY_PHONE() {
        return this.preferences.getString(this.KEY_PHONE, null);
    }

    public String getKEY_USERNAME() {
        return this.preferences.getString(this.KEY_USERNAME, null);
    }

    public void setKEY_UMUR(int paramInt) {
        Editor localEditor = this.preferences.edit();
        localEditor.putInt(this.KEY_UMUR, paramInt);
        localEditor.apply();
    }

    public void setKEY_EMAIL(String paramString) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_EMAIL, paramString);
        localEditor.apply();
    }

    public void setKEY_FOTO(String paramString) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_FOTO, paramString);
        localEditor.apply();
    }

    public void setKEY_ALAMAT(String paramString) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_ALAMAT, paramString);
        localEditor.apply();
    }

    public void setKEY_JK(String paramBoolean) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_JK, paramBoolean);
        localEditor.apply();
    }

    public void setKEY_NAME(String paramString) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_NAME, paramString);
        localEditor.apply();
    }

    public void setKEY_UID(String paramString) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_UID, paramString);
        localEditor.apply();
    }

    public void setKEY_PHONE(String paramString) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_PHONE, paramString);
        localEditor.apply();
    }

    public void setKEY_USERNAME(String paramString) {
        Editor localEditor = this.preferences.edit();
        localEditor.putString(this.KEY_USERNAME, paramString);
        localEditor.apply();
    }
}
