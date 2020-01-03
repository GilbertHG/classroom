package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 10/06/2019.
 */

public class ModelTugas {
    private String usernamePengajar, namaKelas, descKelas;
    private String namaTugas, descTugas, tanggalTugas, waktuTugas;

    public ModelTugas() {
    }

    public ModelTugas(String usernamePengajar, String namaKelas, String descKelas, String namaTugas, String descTugas, String tanggalTugas, String waktuTugas) {
        this.usernamePengajar = usernamePengajar;
        this.namaKelas = namaKelas;
        this.descKelas = descKelas;
        this.namaTugas = namaTugas;
        this.descTugas = descTugas;
        this.tanggalTugas = tanggalTugas;
        this.waktuTugas = waktuTugas;
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

    public String getDescKelas() {
        return descKelas;
    }

    public void setDescKelas(String descKelas) {
        this.descKelas = descKelas;
    }

    public String getNamaTugas() {
        return namaTugas;
    }

    public void setNamaTugas(String namaTugas) {
        this.namaTugas = namaTugas;
    }

    public String getDescTugas() {
        return descTugas;
    }

    public void setDescTugas(String descTugas) {
        this.descTugas = descTugas;
    }

    public String getTanggalTugas() {
        return tanggalTugas;
    }

    public void setTanggalTugas(String tanggalTugas) {
        this.tanggalTugas = tanggalTugas;
    }

    public String getWaktuTugas() {
        return waktuTugas;
    }

    public void setWaktuTugas(String waktuTugas) {
        this.waktuTugas = waktuTugas;
    }
}