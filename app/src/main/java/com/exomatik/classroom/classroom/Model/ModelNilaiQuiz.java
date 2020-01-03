package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 16/06/2019.
 */

public class ModelNilaiQuiz {
    String username;
    int nilai;

    public ModelNilaiQuiz() {
    }

    public ModelNilaiQuiz(String username, int nilai) {
        this.username = username;
        this.nilai = nilai;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNilai() {
        return nilai;
    }

    public void setNilai(int nilai) {
        this.nilai = nilai;
    }
}
