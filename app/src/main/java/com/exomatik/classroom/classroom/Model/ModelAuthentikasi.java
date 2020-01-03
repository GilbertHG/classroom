package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 01/06/2019.
 */

public class ModelAuthentikasi {
    ModelKelas dataKelas;
    UserData dataUser;

    public ModelAuthentikasi() {
    }

    public ModelAuthentikasi(ModelKelas dataKelas, UserData dataUser) {
        this.dataKelas = dataKelas;
        this.dataUser = dataUser;
    }

    public ModelKelas getDataKelas() {
        return dataKelas;
    }

    public void setDataKelas(ModelKelas dataKelas) {
        this.dataKelas = dataKelas;
    }

    public UserData getDataUser() {
        return dataUser;
    }

    public void setDataUser(UserData dataUser) {
        this.dataUser = dataUser;
    }
}
