package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerHasilQuiz;
import com.exomatik.classroom.classroom.Model.ModelNilaiQuiz;
import com.exomatik.classroom.classroom.Model.ModelQuiz;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class HasilQuiz extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    public static String usernamePengajar, namaKelas, kodeQuiz;
    private ImageView back;
    private RecyclerView rcHasil;
    private SwipeRefreshLayout refreshLayout;
    ArrayList<ModelNilaiQuiz> dataNilai = new ArrayList<ModelNilaiQuiz>();
    private RecyclerHasilQuiz adapterLihatKelas = new RecyclerHasilQuiz(dataNilai);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_quiz);

        back = (ImageView) findViewById(R.id.back);
        rcHasil = (RecyclerView) findViewById(R.id.rc_quiz);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        refreshLayout.setOnRefreshListener(this);
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(HasilQuiz.this, 1, false);
        rcHasil.setLayoutManager(localLinearLayoutManager);
        rcHasil.setNestedScrollingEnabled(false);
        rcHasil.setAdapter(adapterLihatKelas);

        getDataQuiz();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HasilQuiz.this, MainActivity.class));
                finish();
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(false);
                getDataQuiz();
                Toast.makeText(HasilQuiz.this, "Refresh", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    private void getDataQuiz() {
        FirebaseDatabase.getInstance()
                .getReference("hasil_quiz")
                .child(usernamePengajar)
                .child(namaKelas)
                .child(kodeQuiz)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            dataNilai.removeAll(dataNilai);
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            int position = 0;
                            while (localIterator.hasNext()) {
                                DataSnapshot localDataSnapshot = (DataSnapshot) localIterator.next();
                                Iterator localIterator2 = localDataSnapshot.getChildren().iterator();
                                ArrayList<ModelQuiz> hasilQuiz = new ArrayList<ModelQuiz>();

                                while (localIterator2.hasNext()) {
                                    ModelQuiz localDataUser = (ModelQuiz) ((DataSnapshot) localIterator2.next()).getValue(ModelQuiz.class);
                                    hasilQuiz.add(localDataUser);
                                }
                                int benar = 0;
                                for (int a = 0; a < hasilQuiz.size(); a++){
                                    if (hasilQuiz.get(a).isJawab()){
                                        benar++;
                                    }
                                }

                                int nilai = benar * 100 / hasilQuiz.size();

                                dataNilai.add(new ModelNilaiQuiz(hasilQuiz.get(position).getUsername(), nilai));
                                position++;
                            }

                            adapterLihatKelas.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(HasilQuiz.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HasilQuiz.this, MainActivity.class));
        finish();
    }
}