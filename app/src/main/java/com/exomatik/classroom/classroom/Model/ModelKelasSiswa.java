package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 02/06/2019.
 */

public class ModelKelasSiswa {
    String nama, email, username, uid, namaKelas, descKelas, userNamePengajar;
    boolean lihat;

    public ModelKelasSiswa() {
    }

    public ModelKelasSiswa(String nama, String email, String username, String uid, String namaKelas, String descKelas, String userNamePengajar, boolean lihat) {
        this.nama = nama;
        this.email = email;
        this.username = username;
        this.uid = uid;
        this.namaKelas = namaKelas;
        this.descKelas = descKelas;
        this.userNamePengajar = userNamePengajar;
        this.lihat = lihat;
    }

    public boolean getLihat() {
        return lihat;
    }

    public void setLihat(boolean lihat) {
        this.lihat = lihat;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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
}
