package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelQuiz;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.Model.ModelSoal;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 16/06/2019.
 */

public class QuizSiswa extends AppCompatActivity {
    public static ArrayList<ModelQuizInvite> listSiswa = new ArrayList<ModelQuizInvite>();
    private TextView textSoal, textIsi, textA, textB, textC, textD, textWaktu, textJawaban;
    private RoundCornerProgressBar progressBar;
    private int progressValue = 0;
    private float progress = 0;
    private int waktu = 0;
    private int jawab = 0;
    private Handler handler = new Handler();
    private Handler handlerProgress = new Handler();
    private Button btnJawab;
    private UserPreference userPreference;
    private ProgressDialog progressDialog;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_pengajar);

        textSoal = (TextView) findViewById(R.id.text_soal);
        textIsi = (TextView) findViewById(R.id.text_isi);
        textA = (TextView) findViewById(R.id.text_jawabA);
        textB = (TextView) findViewById(R.id.text_jawabB);
        textC = (TextView) findViewById(R.id.text_jawabC);
        textD = (TextView) findViewById(R.id.text_jawabD);
        textWaktu = (TextView) findViewById(R.id.text_waktu);
        textJawaban = (TextView) findViewById(R.id.text_jawaban);
        btnJawab = (Button) findViewById(R.id.btn_jawab);
        progressBar = (RoundCornerProgressBar) findViewById(R.id.progress);
        view = (View) findViewById(android.R.id.content);

        userPreference = new UserPreference(this);
        textJawaban.setVisibility(View.VISIBLE);
        btnJawab.setVisibility(View.VISIBLE);

        textA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textJawaban.setText("Jawaban Anda : A");
                jawab = 1;
            }
        });

        textB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textJawaban.setText("Jawaban Anda : B");
                jawab = 2;
            }
        });

        textC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textJawaban.setText("Jawaban Anda : C");
                jawab = 3;
            }
        });

        textD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textJawaban.setText("Jawaban Anda : D");
                jawab = 4;
            }
        });

        progressValue = 0;
        startRepeating();
    }

    private void startRepeating() {
        progressValue = 0;
        handler.post(run);
        handlerProgress.post(runProgress);
    }

    private void stopRepeating() {
        handler.removeCallbacks(run);
        handlerProgress.removeCallbacks(runProgress);
        HasilQuiz.usernamePengajar = listSiswa.get(0).getUserNamePengajar();
        HasilQuiz.namaKelas = listSiswa.get(0).getNamaKelas();
        HasilQuiz.kodeQuiz = listSiswa.get(0).getKodeQuiz();
        startActivity(new Intent(QuizSiswa.this, HasilQuiz.class));
        finish();
        this.finish();
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            long timer = 30000;
            if (progressValue < listSiswa.get(0).getTemplateQuiz().getListSoal().size()){

                textJawaban.setText("");
                btnJawab.setVisibility(View.VISIBLE);
                setSoal(listSiswa.get(0).getTemplateQuiz().getListSoal().get(progressValue), progressValue);

                timer = listSiswa.get(0).getTemplateQuiz().getListSoal().get(progressValue).getWaktu() * 1000;
                progress = 0;

                waktu = listSiswa.get(0).getTemplateQuiz().getListSoal().get(progressValue).getWaktu();

                final int soal = progressValue;

                btnJawab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cekJawaban(soal);
                    }
                });

                progressBar.setMax(waktu);
                progressValue++;
            }
            else {
                stopRepeating();
            }

            handler.postDelayed(this, timer);
        }
    };

    private void cekJawaban(int posisiSoal) {
        String jawaban = textJawaban.getText().toString();

        if (jawaban.isEmpty()){
            Toast.makeText(this, "Anda belum memilih jawaban", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog = new ProgressDialog(QuizSiswa.this);
            progressDialog.setMessage("Mohon Tunggu...");
            progressDialog.setTitle("Proses");
            progressDialog.setCancelable(false);
            progressDialog.show();

            btnJawab.setVisibility(View.GONE);
            int posisi = 0;
            boolean benar = false;

            for (int posisiSiswa = 0; posisiSiswa < listSiswa.size(); posisiSiswa++){
                if (listSiswa.get(posisiSiswa).getUsername().equals(userPreference.getKEY_USERNAME())){
                    posisi = posisiSiswa;
                }
            }

            if (listSiswa.get(posisi).getTemplateQuiz().getListSoal().get(posisiSoal).getJawaban() == jawab){
                benar = true;
            }

            hasilQuiz(benar, posisiSoal);
        }
    }

    private void hasilQuiz(boolean benar, int posisiSoal) {
        ModelQuiz hasil = new ModelQuiz(userPreference.getKEY_USERNAME(), listSiswa.get(0).getUserNamePengajar()
                , listSiswa.get(0).getNamaKelas(), listSiswa.get(0).getKodeQuiz()
                , posisiSoal, benar);

        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference
                .child("hasil_quiz")
                .child(hasil.getUsernamePengajar())
                .child(hasil.getNamaKelas())
                .child(hasil.getKodeQuiz())
                .child(hasil.getUsername())
                .child(Integer.toString(hasil.getSoal()))
                .setValue(hasil)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(QuizSiswa.this, "Berhasil mengirim jawaban", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        progressDialog.dismiss();
                        Snackbar.make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private Runnable runProgress = new Runnable() {
        @Override
        public void run() {
            progressBar.setProgress(progress);
            textWaktu.setVisibility(View.VISIBLE);
            Log.e("Kerja", Integer.toString(waktu));
            textWaktu.setText("Waktu : " + Integer.toString(waktu));

            progress++;
            if (waktu >= 0){
                waktu--;
            }
            handler.postDelayed(this, 1000);
        }
    };

    private void setSoal(ModelSoal modelSoal, int nomor) {
        int tampilNomor = nomor + 1;
        textSoal.setText("Soal " + Integer.toString(tampilNomor));
        textIsi.setText(modelSoal.getSoal());
        textA.setText(modelSoal.getJawabA());
        textB.setText(modelSoal.getJawabB());
        textC.setText(modelSoal.getJawabC());
        textD.setText(modelSoal.getJawabD());
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Maaf anda tidak dapat membatalkan quiz", Toast.LENGTH_SHORT).show();
    }
}
