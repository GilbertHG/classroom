package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 06/06/2019.
 */

public class ModelPenilaian {
    String usernamePengajar, usernameSiswa, kelas, desc, jenis;
    int nilai, pertemuan;

    public ModelPenilaian() {
    }

    public ModelPenilaian(String usernamePengajar, String usernameSiswa, String kelas, String desc, String jenis, int nilai, int pertemuan) {
        this.usernamePengajar = usernamePengajar;
        this.usernameSiswa = usernameSiswa;
        this.kelas = kelas;
        this.desc = desc;
        this.jenis = jenis;
        this.nilai = nilai;
        this.pertemuan = pertemuan;
    }

    public String getUsernamePengajar() {
        return usernamePengajar;
    }

    public void setUsernamePengajar(String usernamePengajar) {
        this.usernamePengajar = usernamePengajar;
    }

    public String getUsernameSiswa() {
        return usernameSiswa;
    }

    public void setUsernameSiswa(String usernameSiswa) {
        this.usernameSiswa = usernameSiswa;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getJenis() {
        return jenis;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public int getNilai() {
        return nilai;
    }

    public void setNilai(int nilai) {
        this.nilai = nilai;
    }

    public int getPertemuan() {
        return pertemuan;
    }

    public void setPertemuan(int pertemuan) {
        this.pertemuan = pertemuan;
    }
}
