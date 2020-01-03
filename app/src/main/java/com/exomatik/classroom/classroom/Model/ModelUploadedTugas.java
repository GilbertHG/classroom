package com.exomatik.classroom.classroom.Model;

/**
 * Created by IrfanRZ on 10/06/2019.
 */

public class ModelUploadedTugas {
    String usernamePengajar, usernameSiswa, kelas, desc, namaTugas, urlFile, dateANDtime;

    public ModelUploadedTugas() {
    }

    public ModelUploadedTugas(String usernamePengajar, String usernameSiswa, String kelas, String desc, String urlFile, String dateANDtime, String namaTugas) {
        this.usernamePengajar = usernamePengajar;
        this.usernameSiswa = usernameSiswa;
        this.kelas = kelas;
        this.desc = desc;
        this.urlFile = urlFile;
        this.dateANDtime = dateANDtime;
        this.namaTugas = namaTugas;
    }

    public String getNamaTugas() {
        return namaTugas;
    }

    public void setNamaTugas(String namaTugas) {
        this.namaTugas = namaTugas;
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

    public String getUrlFile() {
        return urlFile;
    }

    public void setUrlFile(String urlFile) {
        this.urlFile = urlFile;
    }

    public String getDateANDtime() {
        return dateANDtime;
    }

    public void setDateANDtime(String dateANDtime) {
        this.dateANDtime = dateANDtime;
    }
}
