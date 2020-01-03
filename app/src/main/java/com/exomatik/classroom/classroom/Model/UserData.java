package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 25/05/2019.
 */

public class UserData {
    String nama, email, username, uid, alamat, hp, jenis_kelamin, foto;
    int age;
    public UserData() {
    }

    public UserData(String nama, String email, String username, String uid, String alamat, String hp, String jenis_kelamin, int age, String foto) {
        this.nama = nama;
        this.email = email;
        this.username = username;
        this.uid = uid;
        this.alamat = alamat;
        this.hp = hp;
        this.jenis_kelamin = jenis_kelamin;
        this.age = age;
        this.foto = foto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
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

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
