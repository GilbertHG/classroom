package com.exomatik.classroom.classroom.Activity;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerLihatKelas;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatSoalQuiz;
import com.exomatik.classroom.classroom.Dialog.CustomDialogBuatSoalQuiz;
import com.exomatik.classroom.classroom.Dialog.CustomDialogBuatTugas;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelSoal;
import com.exomatik.classroom.classroom.Model.ModelTemplateQuiz;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class BuatQuiz extends AppCompatActivity {
    public static ArrayList<ModelSoal> listSoal = new ArrayList<ModelSoal>();
    private ImageView back;
    private UserPreference userPreference;
    private Button btnTambahSoal, btnSimpan;
    private RecyclerView rcQuiz;
    private EditText etNamaQuiz;
    private TextView textNothing;
    private View view;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_quiz);

        back = (ImageView) findViewById(R.id.back);
        btnTambahSoal = (Button) findViewById(R.id.btn_tambah_soal);
        btnSimpan = (Button) findViewById(R.id.btn_simpan);
        rcQuiz = (RecyclerView) findViewById(R.id.rc_soal);
        etNamaQuiz = (EditText) findViewById(R.id.et_nama_quiz);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        view = (View) findViewById(android.R.id.content);

        userPreference = new UserPreference(this);

        setRecyclerView(listSoal);

        btnTambahSoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = CustomDialogBuatSoalQuiz
                        .newInstance();

                newFragment.setCancelable(false);
                newFragment.show(getFragmentManager(), "dialog");
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaQuiz = etNamaQuiz.getText().toString();
                if (listSoal == null || namaQuiz.isEmpty()) {
                    if (namaQuiz.isEmpty()) {
                        etNamaQuiz.setError("Nama quiz tidak boleh kosong");
                    }
                    if (listSoal == null) {
                        Snackbar.make(view, "Anda harus membuat soal terlebih dulu", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    progressDialog = new ProgressDialog(BuatQuiz.this);
                    progressDialog.setMessage("Mohon Tunggu...");
                    progressDialog.setTitle("Proses");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    simpanQuiz(listSoal, namaQuiz);
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listSoal.removeAll(listSoal);
                startActivity(new Intent(BuatQuiz.this, MainActivity.class));
                finish();
            }
        });
    }

    private void simpanQuiz(final ArrayList<ModelSoal> soal, String namaQuiz) {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ModelTemplateQuiz data = new ModelTemplateQuiz(namaQuiz, userPreference.getKEY_USERNAME(), soal);

        localDatabaseReference
                .child("template_quiz")
                .child(userPreference.getKEY_USERNAME())
                .child(namaQuiz)
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            listSoal.removeAll(listSoal);
                            Toast.makeText(BuatQuiz.this, "Berhasil membuat quiz", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BuatQuiz.this, MainActivity.class));
                            finish();
                            return;
                        }
                        progressDialog.dismiss();
                        Snackbar.make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void setRecyclerView(ArrayList<ModelSoal> listSoal) {
        if (listSoal != null) {
            textNothing.setVisibility(View.GONE);
            rcQuiz.setVisibility(View.VISIBLE);

            RecyclerLihatSoalQuiz adapterLihatKelas = new RecyclerLihatSoalQuiz(listSoal);
            LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(BuatQuiz.this, 1, false);
            rcQuiz.setLayoutManager(localLinearLayoutManager);
            rcQuiz.setNestedScrollingEnabled(false);
            rcQuiz.setAdapter(adapterLihatKelas);
        } else {
            textNothing.setVisibility(View.VISIBLE);
            rcQuiz.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        listSoal.removeAll(listSoal);
        startActivity(new Intent(BuatQuiz.this, MainActivity.class));
        finish();
    }
}
