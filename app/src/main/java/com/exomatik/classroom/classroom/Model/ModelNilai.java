package com.exomatik.classroom.classroom.Model;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 29/05/2019.
 */

public class ModelNilai {
    private String nama;
    private Integer jumlah;
    private int persentase;
    private boolean kehadiran;
    private boolean namaPelajar;

    public ModelNilai() {
    }

    public ModelNilai(String nama, Integer jumlah, int persentase, boolean kehadiran, boolean namaPelajar) {
        this.nama = nama;
        this.jumlah = jumlah;
        this.persentase = persentase;
        this.kehadiran = kehadiran;
        this.namaPelajar = namaPelajar;
    }

    public boolean isKehadiran() {
        return kehadiran;
    }

    public void setKehadiran(boolean kehadiran) {
        this.kehadiran = kehadiran;
    }

    public boolean isNamaPelajar() {
        return namaPelajar;
    }

    public void setNamaPelajar(boolean namaPelajar) {
        this.namaPelajar = namaPelajar;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Integer getJumlah() {
        return jumlah;
    }

    public void setJumlah(Integer jumlah) {
        this.jumlah = jumlah;
    }

    public int getPersentase() {
        return persentase;
    }

    public void setPersentase(int persentase) {
        this.persentase = persentase;
    }
}
