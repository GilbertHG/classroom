package com.exomatik.classroom.classroom.Model;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 30/05/2019.
 */

public class ModelKelas {
    String namaKelas, descKelas, userNamePengajar;
    ArrayList<ModelNilai> modelNilaiKelas;
    boolean lihat;

    public ModelKelas() {
    }

    public ModelKelas(String namaKelas, String descKelas, String userNamePengajar, ArrayList<ModelNilai> modelNilaiKelas, boolean lihat) {
        this.namaKelas = namaKelas;
        this.descKelas = descKelas;
        this.userNamePengajar = userNamePengajar;
        this.modelNilaiKelas = modelNilaiKelas;
        this.lihat = lihat;
    }

    public boolean getLihat() {
        return lihat;
    }

    public void setLihat(boolean lihat) {
        this.lihat = lihat;
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

    public ArrayList<ModelNilai> getModelNilaiKelas() {
        return modelNilaiKelas;
    }

    public void setModelNilaiKelas(ArrayList<ModelNilai> modelNilaiKelas) {
        this.modelNilaiKelas = modelNilaiKelas;
    }
}
