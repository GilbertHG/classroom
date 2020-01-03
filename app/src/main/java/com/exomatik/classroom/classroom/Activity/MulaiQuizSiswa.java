package com.exomatik.classroom.classroom.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerQuizPengajar;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class MulaiQuizSiswa extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    public static ModelKelas dataKelas;
    public static ModelQuizInvite dataQuiz;
    private ImageView back;
    private Button btnMulai;
    private RecyclerView rcSiswa;
    private TextView textNothing;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerQuizPengajar adapter;
    private ArrayList<ModelQuizInvite> list = new ArrayList<ModelQuizInvite>();
    private ArrayList<ModelQuizInvite> listMulai = new ArrayList<ModelQuizInvite>();
    private RelativeLayout relativeLayout;
    private ImageView btnHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mulai_quiz_pengajar);

        back = (ImageView) findViewById(R.id.back);
        btnMulai = (Button) findViewById(R.id.btn_buat);
        rcSiswa = (RecyclerView) findViewById(R.id.rc_siswa);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        relativeLayout = (RelativeLayout) findViewById(R.id.sv_profile);
        btnHelp = (ImageView) findViewById(R.id.btn_help);

        btnMulai.setVisibility(View.GONE);

        refreshLayout.setOnRefreshListener(this);
        adapter = new RecyclerQuizPengajar(list, getApplicationContext());
        RecyclerView.LayoutManager layoutAgenda = new LinearLayoutManager(MulaiQuizSiswa.this);
        rcSiswa.setLayoutManager(layoutAgenda);
        rcSiswa.setAdapter(adapter);

        getSiswaJoin();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertBack();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialShowcaseView.resetSingleUse(MulaiQuizSiswa.this, "Sequence Showcase");
                showTutorSequence(0);
            }
        });
    }

    @Override
    public void onBackPressed() {
        alertBack();
    }

    private void alertBack() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MulaiQuizSiswa.this);
        alert.setTitle("Kembali");
        alert.setMessage("Apakah anda yakin ingin keluar dari quiz?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference db_remove_kelas = FirebaseDatabase.getInstance().getReference()
                        .child("quiz_accept")
                        .child(dataKelas.getUserNamePengajar())
                        .child(dataKelas.getNamaKelas())
                        .child(dataQuiz.getKodeQuiz())
                        .child(dataQuiz.getUsername())
                        ;
                db_remove_kelas.removeValue();

                dialog.dismiss();
                startActivity(new Intent(MulaiQuizSiswa.this, MainActivity.class));
                finish();
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                getSiswaJoin();
                cekBeginQuiz();
                Toast.makeText(MulaiQuizSiswa.this, "Refresh", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private void cekBeginQuiz() {
        FirebaseDatabase.getInstance()
                .getReference("quiz_begin")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas())
                .child(dataQuiz.getKodeQuiz())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean cek = false;
                        listMulai.removeAll(listMulai);

                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();

                            while (localIterator.hasNext()) {
                                ModelQuizInvite data = (ModelQuizInvite) ((DataSnapshot) localIterator.next()).getValue(ModelQuizInvite.class);

                                listMulai.add(data);
                                cek = true;
                            }
                        }

                        if (cek){
                            btnMulai.setVisibility(View.VISIBLE);
                            btnMulai.setText("Masuk Quiz");

                            btnMulai.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    QuizSiswa.listSiswa = listMulai;
                                    startActivity(new Intent(MulaiQuizSiswa.this, QuizSiswa.class));
                                    finish();
                                }
                            });
                        }
                        else {
                            Toast.makeText(MulaiQuizSiswa.this, "Quiz belum dimulai", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MulaiQuizSiswa.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getSiswaJoin() {
        FirebaseDatabase.getInstance()
                .getReference("quiz_accept")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas())
                .child(dataQuiz.getKodeQuiz())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();

                            while (localIterator.hasNext()) {
                                ModelQuizInvite data = (ModelQuizInvite) ((DataSnapshot) localIterator.next()).getValue(ModelQuizInvite.class);

                                list.add(data);
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else {
                            Toast.makeText(MulaiQuizSiswa.this, "Error database tidak diketahui", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MulaiQuizSiswa.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showTutorSequence(int paramInt) {
        ShowcaseConfig localShowcaseConfig = new ShowcaseConfig();
        localShowcaseConfig.setDelay(paramInt);
        MaterialShowcaseSequence localMaterialShowcaseSequence = new MaterialShowcaseSequence(MulaiQuizSiswa.this, "Sequence Showcase");
        localMaterialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView paramAnonymousMaterialShowcaseView, int paramAnonymousInt) {
            }
        });
        localMaterialShowcaseSequence.setConfig(localShowcaseConfig);
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(MulaiQuizSiswa.this)
                .setTarget(btnMulai).setDismissText("Selanjutnya")
                .setContentText("Untuk memulai quiz, silahkan tunggu pengajar anda untuk memulainya, swipe ke atas untuk mengeceknya").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(MulaiQuizSiswa.this)
                .setTarget(relativeLayout).setDismissText("Baik")
                .setContentText("Memperlihatkan siswa yang sudah Join di Quiz ini, swipe ke atas untuk refresh").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}
