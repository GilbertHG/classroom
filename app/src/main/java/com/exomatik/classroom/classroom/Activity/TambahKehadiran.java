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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerTambahKehadiran;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKehadiran;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.UserData;
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

public class TambahKehadiran extends AppCompatActivity {
    public static ModelKelas dataKelas;
    public static Spinner spinnerPertemuan;
    public static ArrayList<ModelKelasSiswa> listSiswa = new ArrayList<ModelKelasSiswa>();
    private ArrayList<ModelKehadiran> listKehadiranSiswa = new ArrayList<ModelKehadiran>();
    private ImageView back, btnHelp;
    private UserPreference userPreference;
    private RecyclerView rcKehadiran;
    private RelativeLayout rlSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kehadiran);

        back = (ImageView) findViewById(R.id.back);
        btnHelp = (ImageView) findViewById(R.id.img_help);
        spinnerPertemuan = (Spinner) findViewById(R.id.spinner_hadir);
        rcKehadiran = (RecyclerView) findViewById(R.id.rc_kehadiran);
        rlSimpan = (RelativeLayout) findViewById(R.id.rl_simpan);

        userPreference = new UserPreference(this);

        setSpinnerPertemuan();
        getDataKehadiran(0);

        spinnerPertemuan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getDataKehadiran(position);
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

        rlSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanData();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKelas = null;
                listSiswa.removeAll(listSiswa);
                listKehadiranSiswa.removeAll(listKehadiranSiswa);
                startActivity(new Intent(TambahKehadiran.this, DetailKelas.class));
                finish();
            }
        });
    }

    private void simpanData() {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference.child("nilai")
                .child("kehadiran")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child("Pertemuan " + Integer.toString(spinnerPertemuan.getSelectedItemPosition()))
                .setValue(listKehadiranSiswa)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dataKelas = null;
                        listSiswa.removeAll(listSiswa);
                        listKehadiranSiswa.removeAll(listKehadiranSiswa);
                        startActivity(new Intent(TambahKehadiran.this, DetailKelas.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TambahKehadiran.this, "Error, " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void getDataKehadiran(int position) {
        FirebaseDatabase.getInstance().getReference("nilai")
                .child("kehadiran")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child("Pertemuan " + Integer.toString(position))
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listKehadiranSiswa.removeAll(listKehadiranSiswa);

                if (dataSnapshot.exists()) {
                    Iterator localIterator = dataSnapshot.getChildren().iterator();
                    while (localIterator.hasNext()) {
                        ModelKehadiran localDataUser = (ModelKehadiran) ((DataSnapshot) localIterator.next()).getValue(ModelKehadiran.class);
                        listKehadiranSiswa.add(new ModelKehadiran(localDataUser.getUsernamePengajar()
                                , localDataUser.getUsernameSiswa(), localDataUser.getKelas()
                                , localDataUser.getDesc(), localDataUser.getHadir(), localDataUser.getPertemuan()));
                    }
                }
                else {
                    for (int a = 0; a < listSiswa.size(); a++){
                        listKehadiranSiswa.add(new ModelKehadiran(dataKelas.getUserNamePengajar()
                                , listSiswa.get(a).getUsername()
                                , dataKelas.getNamaKelas()
                                , dataKelas.getDescKelas()
                                , 0
                                , spinnerPertemuan.getSelectedItemPosition()
                        ));
                    }
                }

                setRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(TambahKehadiran.this, "Gagal Mengambil Data Kehadiran", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRecyclerView() {
        RecyclerTambahKehadiran adapterLihatKelas = new RecyclerTambahKehadiran(listKehadiranSiswa, TambahKehadiran.this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(TambahKehadiran.this, 1, false);
        rcKehadiran.setLayoutManager(localLinearLayoutManager);
        rcKehadiran.setNestedScrollingEnabled(false);
        rcKehadiran.setAdapter(adapterLihatKelas);
    }

    @Override
    public void onBackPressed() {
        dataKelas = null;
        listSiswa.removeAll(listSiswa);
        listKehadiranSiswa.removeAll(listKehadiranSiswa);
        startActivity(new Intent(TambahKehadiran.this, DetailKelas.class));
        finish();
    }

    private void setSpinnerPertemuan() {
        ArrayList<String> listPertemuan = new ArrayList<String>();
        int size = 0;

        for (int a = 0; a < dataKelas.getModelNilaiKelas().size(); a++){
            if (dataKelas.getModelNilaiKelas().get(a).isKehadiran()){
                size = dataKelas.getModelNilaiKelas().get(a).getJumlah();
            }
        }
        for (int jumlah = 1; jumlah <= size; jumlah++){
            listPertemuan.add("Pertemuan " + jumlah);
        }

        ArrayAdapter<String> dataNilai = new ArrayAdapter<String>(TambahKehadiran.this,
                R.layout.spinner_text_hitam, listPertemuan);
        spinnerPertemuan.setAdapter(dataNilai);
    }
}