package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 02/06/2019.
 */

public class ModelQuizInvite {
    String username, namaKelas, descKelas, userNamePengajar, namaQuiz, kodeQuiz;
    boolean ikutQuiz;
    ModelTemplateQuiz templateQuiz;

    public ModelQuizInvite() {
    }

    public ModelQuizInvite(String username, String namaKelas, String descKelas, String userNamePengajar, String namaQuiz, String kodeQuiz, boolean ikutQuiz, ModelTemplateQuiz templateQuiz) {
        this.username = username;
        this.namaKelas = namaKelas;
        this.descKelas = descKelas;
        this.userNamePengajar = userNamePengajar;
        this.namaQuiz = namaQuiz;
        this.kodeQuiz = kodeQuiz;
        this.ikutQuiz = ikutQuiz;
        this.templateQuiz = templateQuiz;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getDescKelas() {
        return descKelas;
    }

    public void setDescKelas(String descKelas) {
        this.descKelas = descKelas;
    }

    public String getUserNamePengajar() {
        return userNamePengajar;
    }

    public void setUserNamePengajar(String userNamePengajar) {
        this.userNamePengajar = userNamePengajar;
    }

    public String getNamaQuiz() {
        return namaQuiz;
    }

    public void setNamaQuiz(String namaQuiz) {
        this.namaQuiz = namaQuiz;
    }

    public String getKodeQuiz() {
        return kodeQuiz;
    }

    public void setKodeQuiz(String kodeQuiz) {
        this.kodeQuiz = kodeQuiz;
    }

    public boolean isIkutQuiz() {
        return ikutQuiz;
    }

    public void setIkutQuiz(boolean ikutQuiz) {
        this.ikutQuiz = ikutQuiz;
    }

    public ModelTemplateQuiz getTemplateQuiz() {
        return templateQuiz;
    }

    public void setTemplateQuiz(ModelTemplateQuiz templateQuiz) {
        this.templateQuiz = templateQuiz;
    }
}
