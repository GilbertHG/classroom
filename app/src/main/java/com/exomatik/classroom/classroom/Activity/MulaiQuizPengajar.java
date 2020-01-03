package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerKelasAuth;
import com.exomatik.classroom.classroom.Adapter.RecyclerQuizPengajar;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

public class MulaiQuizPengajar extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    public static String kodeQuiz;
    public static ModelKelas dataKelas;
    private ImageView back;
    private Button btnMulai;
    private RecyclerView rcSiswa;
    private SwipeRefreshLayout refreshLayout;
    private TextView textNothing, textKode;
    private ArrayList<ModelQuizInvite> list = new ArrayList<ModelQuizInvite>();
    private RecyclerQuizPengajar adapter;
    private ProgressDialog progressDialog;
    private View view;
    private ImageView btnHelp;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mulai_quiz_pengajar);

        back = (ImageView) findViewById(R.id.back);
        btnMulai = (Button) findViewById(R.id.btn_buat);
        rcSiswa = (RecyclerView) findViewById(R.id.rc_siswa);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        textKode = (TextView) findViewById(R.id.text_kode);
        view = (View) findViewById(android.R.id.content);
        btnHelp = (ImageView) findViewById(R.id.btn_help);
        relativeLayout = (RelativeLayout) findViewById(R.id.sv_profile);

        refreshLayout.setOnRefreshListener(this);
        adapter = new RecyclerQuizPengajar(list, getApplicationContext());
        RecyclerView.LayoutManager layoutAgenda = new LinearLayoutManager(MulaiQuizPengajar.this);
        rcSiswa.setLayoutManager(layoutAgenda);
        rcSiswa.setAdapter(adapter);

        textKode.setText("Kode quiz : " + kodeQuiz);
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
                MaterialShowcaseView.resetSingleUse(MulaiQuizPengajar.this, "Sequence Showcase");
                showTutorSequence(0);
            }
        });

        btnMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.size() != 0){
                    progressDialog = new ProgressDialog(MulaiQuizPengajar.this);
                    progressDialog.setMessage("Mohon Tunggu...");
                    progressDialog.setTitle("Proses");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    hapusInvitationData();
                    hapusAcceptData();
                    sendData(list);

                }
                else {
                    Toast.makeText(MulaiQuizPengajar.this, "Belum ada siswa yang join dalam Quiz ini", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getSiswaJoin() {
        FirebaseDatabase.getInstance()
                .getReference("quiz_accept")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas())
                .child(kodeQuiz)
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
                            Toast.makeText(MulaiQuizPengajar.this, "Belum ada siswa yang join", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(MulaiQuizPengajar.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        alertBack();
    }

    private void alertBack() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MulaiQuizPengajar.this);
        alert.setTitle("Kembali");
        alert.setMessage("Apakah anda yakin ingin membatalkan Quiz ini ?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                hapusInvitationData();
                hapusAcceptData();

                dialog.dismiss();
                startActivity(new Intent(MulaiQuizPengajar.this, DetailKelas.class));
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

    private void hapusInvitationData(){
        DatabaseReference db_remove_kelas = FirebaseDatabase.getInstance().getReference()
                        .child("quiz_invitation")
                        .child(dataKelas.getUserNamePengajar())
                        .child(dataKelas.getNamaKelas())
                        .child(kodeQuiz);
                db_remove_kelas.removeValue();
    }

    private void hapusAcceptData(){
        DatabaseReference db_remove = FirebaseDatabase.getInstance().getReference()
                .child("quiz_accept")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas())
                .child(kodeQuiz);
        db_remove.removeValue();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                getSiswaJoin();
                Toast.makeText(MulaiQuizPengajar.this, "Refresh", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private void sendData(final ArrayList<ModelQuizInvite> data) {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference
                .child("quiz_begin")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas())
                .child(data.get(0).getKodeQuiz())
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            Toast.makeText(MulaiQuizPengajar.this, "Quiz dimulai", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();

                            QuizPengajar.listSiswa = data;
                            startActivity(new Intent(MulaiQuizPengajar.this, QuizPengajar.class));
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Snackbar.make(view, "Error tidak diketahui", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();

                Toast.makeText(MulaiQuizPengajar.this, "Error, " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTutorSequence(int paramInt) {
        ShowcaseConfig localShowcaseConfig = new ShowcaseConfig();
        localShowcaseConfig.setDelay(paramInt);
        MaterialShowcaseSequence localMaterialShowcaseSequence = new MaterialShowcaseSequence(MulaiQuizPengajar.this, "Sequence Showcase");
        localMaterialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView paramAnonymousMaterialShowcaseView, int paramAnonymousInt) {
            }
        });
        localMaterialShowcaseSequence.setConfig(localShowcaseConfig);
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(MulaiQuizPengajar.this)
                .setTarget(btnMulai).setDismissText("Selanjutnya")
                .setContentText("Untuk memulai quiz, informasi tambahan pastikan siswa anda tidak menutup aplikasinya dan silahkan minta siswa anda swipe ke atas untuk memulai quiz").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(MulaiQuizPengajar.this)
                .setTarget(relativeLayout).setDismissText("Baik")
                .setContentText("Memperlihatkan siswa yang sudah Join di Quiz ini, swipe ke atas untuk refresh").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}
