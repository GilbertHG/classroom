package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 16/06/2019.
 */

public class ModelQuiz {
    String username, usernamePengajar, namaKelas, kodeQuiz;
    int soal;
    boolean jawab;

    public ModelQuiz() {
    }

    public ModelQuiz(String username, String usernamePengajar, String namaKelas, String kodeQuiz, int soal, boolean jawab) {
        this.username = username;
        this.usernamePengajar = usernamePengajar;
        this.namaKelas = namaKelas;
        this.kodeQuiz = kodeQuiz;
        this.soal = soal;
        this.jawab = jawab;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernamePengajar() {
        return usernamePengajar;
    }

    public void setUsernamePengajar(String usernamePengajar) {
        this.usernamePengajar = usernamePengajar;
    }

    public String getNamaKelas() {
        return namaKelas;
    }

    public void setNamaKelas(String namaKelas) {
        this.namaKelas = namaKelas;
    }

    public String getKodeQuiz() {
        return kodeQuiz;
    }

    public void setKodeQuiz(String kodeQuiz) {
        this.kodeQuiz = kodeQuiz;
    }

    public int getSoal() {
        return soal;
    }

    public void setSoal(int soal) {
        this.soal = soal;
    }

    public boolean isJawab() {
        return jawab;
    }

    public void setJawab(boolean jawab) {
        this.jawab = jawab;
    }
}
