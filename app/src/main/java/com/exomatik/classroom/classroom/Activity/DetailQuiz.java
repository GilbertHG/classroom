package com.exomatik.classroom.classroom.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.exomatik.classroom.classroom.Adapter.RecyclerLihatQuiz;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatSoalDetail;
import com.exomatik.classroom.classroom.Model.ModelTemplateQuiz;
import com.exomatik.classroom.classroom.R;

public class DetailQuiz extends AppCompatActivity {
    public static ModelTemplateQuiz templateQuiz;
    private ImageView back;
    private TextView textNamaQuiz;
    private RecyclerView rcSoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_quiz);

        back = (ImageView) findViewById(R.id.back);
        textNamaQuiz = (TextView) findViewById(R.id.text_nama_isi);
        rcSoal = (RecyclerView) findViewById(R.id.rc_soal);

        textNamaQuiz.setText(templateQuiz.getNamaQuiz());
        RecyclerLihatSoalDetail adapterLihatKelas = new RecyclerLihatSoalDetail(templateQuiz.getListSoal());
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(DetailQuiz.this, 1, false);
        rcSoal.setLayoutManager(localLinearLayoutManager);
        rcSoal.setNestedScrollingEnabled(false);
        rcSoal.setAdapter(adapterLihatKelas);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                templateQuiz = null;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        templateQuiz = null;
        finish();
    }
}