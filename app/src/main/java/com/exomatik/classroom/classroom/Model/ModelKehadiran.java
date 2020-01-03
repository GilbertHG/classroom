package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 04/06/2019.
 */

public class ModelKehadiran {
    String usernamePengajar, usernameSiswa, kelas, desc;
    int hadir, pertemuan;

    public ModelKehadiran() {
    }

    public ModelKehadiran(String usernamePengajar, String usernameSiswa, String kelas, String desc, int hadir, int pertemuan) {
        this.usernamePengajar = usernamePengajar;
        this.usernameSiswa = usernameSiswa;
        this.kelas = kelas;
        this.desc = desc;
        this.hadir = hadir;
        this.pertemuan = pertemuan;
    }

    public int getPertemuan() {
        return pertemuan;
    }

    public void setPertemuan(int pertemuan) {
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

    public int getHadir() {
        return hadir;
    }

    public void setHadir(int hadir) {
        this.hadir = hadir;
    }
}
