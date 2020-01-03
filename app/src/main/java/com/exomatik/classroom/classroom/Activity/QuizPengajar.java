package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.Model.ModelSoal;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class QuizPengajar extends AppCompatActivity {
    public static ArrayList<ModelQuizInvite> listSiswa = new ArrayList<ModelQuizInvite>();
    private TextView textSoal, textIsi, textA, textB, textC, textD, textWaktu;
    private RoundCornerProgressBar progressBar;

    private int progressValue = 0;
    private float progress = 0;
    private int waktu = 0;
    private Handler handler = new Handler();
    private Handler handlerProgress = new Handler();

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
        progressBar = (RoundCornerProgressBar) findViewById(R.id.progress);

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

        hapusBeginData();
        HasilQuiz.usernamePengajar = listSiswa.get(0).getUserNamePengajar();
        HasilQuiz.namaKelas = listSiswa.get(0).getNamaKelas();
        HasilQuiz.kodeQuiz = listSiswa.get(0).getKodeQuiz();
        startActivity(new Intent(QuizPengajar.this, HasilQuiz.class));
        finish();
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            long timer = 30000;
            if (progressValue < listSiswa.get(0).getTemplateQuiz().getListSoal().size()){
                setSoal(listSiswa.get(0).getTemplateQuiz().getListSoal().get(progressValue), progressValue);

                timer = listSiswa.get(0).getTemplateQuiz().getListSoal().get(progressValue).getWaktu() * 1000;
                progress = 0;
                waktu = listSiswa.get(0).getTemplateQuiz().getListSoal().get(progressValue).getWaktu();

                progressBar.setMax(waktu);
                progressValue++;
            }
            else {
                stopRepeating();
            }

            handler.postDelayed(this, timer);
        }
    };

    private Runnable runProgress = new Runnable() {
        @Override
        public void run() {
            progressBar.setProgress(progress);
            textWaktu.setText("Waktu : " + Integer.toString(waktu));
            progress++;
            waktu--;
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

    private void hapusBeginData(){
        DatabaseReference db_remove = FirebaseDatabase.getInstance().getReference()
                .child("quiz_begin")
                .child(listSiswa.get(0).getUserNamePengajar())
                .child(listSiswa.get(0).getNamaKelas())
                .child(listSiswa.get(0).getKodeQuiz());
        db_remove.removeValue();
    }
}
