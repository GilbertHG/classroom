package com.exomatik.classroom.classroom.Model;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 14/06/2019.
 */

public class ModelTemplateQuiz {
    String namaQuiz, username;
    ArrayList<ModelSoal> listSoal;

    public ModelTemplateQuiz() {
    }

    public ModelTemplateQuiz(String namaQuiz, String username, ArrayList<ModelSoal> listSoal) {
        this.namaQuiz = namaQuiz;
        this.username = username;
        this.listSoal = listSoal;
    }

    public String getNamaQuiz() {
        return namaQuiz;
    }

    public void setNamaQuiz(String namaQuiz) {
        this.namaQuiz = namaQuiz;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<ModelSoal> getListSoal() {
        return listSoal;
    }

    public void setListSoal(ArrayList<ModelSoal> listSoal) {
        this.listSoal = listSoal;
    }
}
