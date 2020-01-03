package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerTambahKehadiran;
import com.exomatik.classroom.classroom.Adapter.RecyclerTambahPenilaian;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKehadiran;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.ModelNilai;
import com.exomatik.classroom.classroom.Model.ModelPenilaian;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class TambahPenilaian extends AppCompatActivity {
    public static ModelKelas dataKelas;
    public static ArrayList<ModelKelasSiswa> listSiswa = new ArrayList<ModelKelasSiswa>();
    private ArrayList<ModelPenilaian> listPenilaianSiswa = new ArrayList<ModelPenilaian>();
    private ArrayList<ModelNilai> listNilaiEditSiswa = new ArrayList<ModelNilai>();
    private ImageView back, btnHelp;
    private Spinner spinnerNilai, spinnerKe;
    private RecyclerView rcNilai;
    private RelativeLayout rlSimpan;
    private UserPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_penilaian);

        back = (ImageView) findViewById(R.id.back);
        btnHelp = (ImageView) findViewById(R.id.img_help);
        spinnerNilai = (Spinner) findViewById(R.id.spinner_nilai);
        spinnerKe = (Spinner) findViewById(R.id.spinner_nilai_ke);
        rcNilai = (RecyclerView) findViewById(R.id.rc_nilai);
        rlSimpan = (RelativeLayout) findViewById(R.id.rl_simpan);

        userPreference = new UserPreference(this);
        setSpinnerNilai();

        rlSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanData();
            }
        });

        spinnerNilai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDataNilai(listNilaiEditSiswa.get(spinnerNilai.getSelectedItemPosition()).getNama(), 0);
                setSpinnerNilaiKe(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerKe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDataNilai(listNilaiEditSiswa.get(spinnerNilai.getSelectedItemPosition()).getNama(), position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKelas = null;
                listSiswa.removeAll(listSiswa);
                startActivity(new Intent(TambahPenilaian.this, DetailKelas.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        dataKelas = null;
        listSiswa.removeAll(listSiswa);
        startActivity(new Intent(TambahPenilaian.this, DetailKelas.class));
        finish();
    }

    private String alphabet(String desc){
        String alphabetDesc = null;
        if (desc.length() == 1) {
            alphabetDesc = desc.substring(0, 1);
        } else if (desc.length() >= 2) {
            alphabetDesc = desc.substring(0, 2);
        }

        return alphabetDesc;
    }

    private void setSpinnerNilai() {
        ArrayList<String> listPertemuan = new ArrayList<String>();

        for (int a = 0; a < dataKelas.getModelNilaiKelas().size(); a++) {
            if (!dataKelas.getModelNilaiKelas().get(a).isNamaPelajar() && !dataKelas.getModelNilaiKelas().get(a).isKehadiran()) {
                listPertemuan.add(dataKelas.getModelNilaiKelas().get(a).getNama());
                listNilaiEditSiswa.add(new ModelNilai(dataKelas.getModelNilaiKelas().get(a).getNama()
                        , dataKelas.getModelNilaiKelas().get(a).getJumlah()
                        , dataKelas.getModelNilaiKelas().get(a).getPersentase()
                        , dataKelas.getModelNilaiKelas().get(a).isKehadiran()
                        , dataKelas.getModelNilaiKelas().get(a).isNamaPelajar()
                ));
            }
        }

        setSpinnerNilaiKe(0);
        getDataNilai(listNilaiEditSiswa.get(0).getNama(), 0);

        ArrayAdapter<String> dataNilai = new ArrayAdapter<String>(TambahPenilaian.this,
                R.layout.spinner_text_putih, listPertemuan);
        spinnerNilai.setAdapter(dataNilai);
    }

    private void setSpinnerNilaiKe(int position) {
        ArrayList<String> listNilaiKe = new ArrayList<String>();
        int beginTampil = 1;

        if (listNilaiEditSiswa.get(position).getJumlah() == 1){
            spinnerKe.setVisibility(View.GONE);
            listNilaiKe.add("0");
        }
        else {
            spinnerKe.setVisibility(View.VISIBLE);
            for (int a = 0; a < listNilaiEditSiswa.get(position).getJumlah(); a++) {
                listNilaiKe.add(Integer.toString(beginTampil));
                beginTampil++;
            }
        }

        ArrayAdapter<String> dataNilai = new ArrayAdapter<String>(TambahPenilaian.this,
                R.layout.spinner_text_putih, listNilaiKe);
        spinnerKe.setAdapter(dataNilai);
    }

    private void getDataNilai(String jenisNilai, int nilaiKe) {
        FirebaseDatabase.getInstance().getReference("nilai")
                .child(jenisNilai)
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child("Nilai Ke-" + Integer.toString(nilaiKe))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listPenilaianSiswa.removeAll(listPenilaianSiswa);

                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelPenilaian localDataUser = (ModelPenilaian) ((DataSnapshot) localIterator.next()).getValue(ModelPenilaian.class);
                                listPenilaianSiswa.add(new ModelPenilaian(localDataUser.getUsernamePengajar()
                                        , localDataUser.getUsernameSiswa(), localDataUser.getKelas()
                                        , localDataUser.getDesc(), localDataUser.getJenis(), localDataUser.getNilai()
                                        , localDataUser.getPertemuan()));
                            }
                        }
                        else {
                            for (int a = 0; a < listSiswa.size(); a++){
                                listPenilaianSiswa.add(new ModelPenilaian(dataKelas.getUserNamePengajar()
                                        , listSiswa.get(a).getUsername()
                                        , dataKelas.getNamaKelas()
                                        , dataKelas.getDescKelas()
                                        , listNilaiEditSiswa.get(spinnerNilai.getSelectedItemPosition()).getNama()
                                        , 404
                                        , spinnerKe.getSelectedItemPosition()
                                ));
                            }
                        }

                        setRecyclerView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(TambahPenilaian.this, "Gagal Mengambil Data Kehadiran", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setRecyclerView() {
        RecyclerTambahPenilaian adapterLihatKelas = new RecyclerTambahPenilaian(listPenilaianSiswa, TambahPenilaian.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(TambahPenilaian.this, 1, false);
        rcNilai.setLayoutManager(localLinearLayoutManager);
        rcNilai.setNestedScrollingEnabled(false);
        rcNilai.setAdapter(adapterLihatKelas);
    }

    private void simpanData() {

        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference.child("nilai")
                .child(listNilaiEditSiswa.get(spinnerNilai.getSelectedItemPosition()).getNama())
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child("Nilai Ke-" + Integer.toString(spinnerKe.getSelectedItemPosition()))
                .setValue(listPenilaianSiswa)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataKelas = null;
                        listSiswa.removeAll(listSiswa);
                        listPenilaianSiswa.removeAll(listPenilaianSiswa);
                        startActivity(new Intent(TambahPenilaian.this, DetailKelas.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahPenilaian.this, "Error, " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}