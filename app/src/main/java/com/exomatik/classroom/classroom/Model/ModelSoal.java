package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 13/06/2019.
 */

public class ModelSoal {
    String soal, jawabA, jawabB, jawabC, jawabD;
    int waktu, jawaban;

    public ModelSoal() {
    }

    public ModelSoal(String soal, String jawabA, String jawabB, String jawabC, String jawabD, int jawaban, int waktu) {
        this.soal = soal;
        this.jawabA = jawabA;
        this.jawabB = jawabB;
        this.jawabC = jawabC;
        this.jawabD = jawabD;
        this.jawaban = jawaban;
        this.waktu = waktu;
    }

    public String getSoal() {
        return soal;
    }

    public void setSoal(String soal) {
        this.soal = soal;
    }

    public String getJawabA() {
        return jawabA;
    }

    public void setJawabA(String jawabA) {
        this.jawabA = jawabA;
    }

    public String getJawabB() {
        return jawabB;
    }

    public void setJawabB(String jawabB) {
        this.jawabB = jawabB;
    }

    public String getJawabC() {
        return jawabC;
    }

    public void setJawabC(String jawabC) {
        this.jawabC = jawabC;
    }

    public String getJawabD() {
        return jawabD;
    }

    public void setJawabD(String jawabD) {
        this.jawabD = jawabD;
    }

    public int getJawaban() {
        return jawaban;
    }

    public void setJawaban(int jawaban) {
        this.jawaban = jawaban;
    }

    public int getWaktu() {
        return waktu;
    }

    public void setWaktu(int waktu) {
        this.waktu = waktu;
    }
}
