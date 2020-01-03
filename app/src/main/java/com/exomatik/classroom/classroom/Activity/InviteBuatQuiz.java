package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerLihatQuiz;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelTemplateQuiz;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class InviteBuatQuiz extends AppCompatActivity implements ItemClickSupport.OnItemClickListener{
    public static ModelKelas dataKelas;
    private ImageView back, btnHelp;
    private TextView textNothing;
    private RecyclerView rcQuiz;
    private ArrayList<ModelTemplateQuiz> listQuiz = new ArrayList<ModelTemplateQuiz>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_buat_quiz);

        back = (ImageView) findViewById(R.id.back);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        rcQuiz = (RecyclerView) findViewById(R.id.rc_quiz);
        btnHelp = (ImageView) findViewById(R.id.btn_help);

        ItemClickSupport.addTo(rcQuiz).setOnItemClickListener(this);
        getDataQuiz();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailKelas.dataKelas = dataKelas;
                startActivity(new Intent(InviteBuatQuiz.this, DetailKelas.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DetailKelas.dataKelas = dataKelas;
        startActivity(new Intent(InviteBuatQuiz.this, DetailKelas.class));
        finish();
    }

    private void getDataQuiz() {
        FirebaseDatabase.getInstance()
                .getReference("template_quiz")
                .child(dataKelas.getUserNamePengajar())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelTemplateQuiz localDataUser = (ModelTemplateQuiz) ((DataSnapshot) localIterator.next()).getValue(ModelTemplateQuiz.class);

                                listQuiz.add(localDataUser);
                            }

                            RecyclerLihatQuiz adapterLihatKelas = new RecyclerLihatQuiz(listQuiz);
                            LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(InviteBuatQuiz.this, 1, false);
                            rcQuiz.setLayoutManager(localLinearLayoutManager);
                            rcQuiz.setNestedScrollingEnabled(false);
                            rcQuiz.setAdapter(adapterLihatKelas);
                        }
                        else {
                            textNothing.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(InviteBuatQuiz.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        InviteSiswaQuiz.dataKelas = dataKelas;
        InviteSiswaQuiz.dataQuiz = listQuiz.get(position);
        startActivity(new Intent(InviteBuatQuiz.this, InviteSiswaQuiz.class));
        finish();
    }
}
