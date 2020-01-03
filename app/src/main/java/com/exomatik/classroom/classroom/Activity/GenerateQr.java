package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerKelasAuth;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatKelas;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelAuthentikasi;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Iterator;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class GenerateQr extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static ModelKelas dataKelas;
    public static SwipeRefreshLayout refresh;
    public static ArrayList<ModelAuthentikasi> listAuthUser = new ArrayList<ModelAuthentikasi>();
    public static RecyclerKelasAuth adapter;
    private PhotoView imageView;
    private ImageView imgBack, btnHelp;
    private TextView textNothing;
    private RecyclerView rcAuth;
    private View view;
    private UserPreference userPreference;
    private RelativeLayout rl;

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        setContentView(R.layout.activity_generate_qr);
        view = findViewById(android.R.id.content);

        imageView = ((PhotoView) findViewById(R.id.imageQR));
        imgBack = ((ImageView) findViewById(R.id.back));
        textNothing = (TextView) findViewById(R.id.text_nothing);
        rcAuth = (RecyclerView) findViewById(R.id.rc_list_accept);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        btnHelp = (ImageView) findViewById(R.id.img_help);
        rl = (RelativeLayout) findViewById(R.id.rl_list_accept);

        userPreference = new UserPreference(this);
        rcAuth.setItemAnimator(new DefaultItemAnimator());
        refresh.setOnRefreshListener(this);

        btn_generate();
        getAuthUser();

        imgBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                dataKelas = null;
                startActivity(new Intent(GenerateQr.this, DetailKelas.class));
                finish();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialShowcaseView.resetSingleUse(GenerateQr.this, "Sequence Showcase");
                showTutorSequence(0);
            }
        });
    }

    public void onBackPressed() {
        dataKelas = null;
        startActivity(new Intent(GenerateQr.this, DetailKelas.class));
        finish();
    }

    private void btn_generate() {
        String str = dataKelas.getNamaKelas() + "__" + dataKelas.getDescKelas() + "__" + dataKelas.getUserNamePengajar();
        MultiFormatWriter localMultiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix localBitMatrix = localMultiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap localBitmap = new BarcodeEncoder().createBitmap(localBitMatrix);
            imageView.setImageBitmap(localBitmap);

            return;
        } catch (Exception localException) {
            Toast.makeText(GenerateQr.this, "Error " + localException, Toast.LENGTH_SHORT).show();
        }
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

    private void getAuthUser() {
        String alphabetDesc = alphabet(dataKelas.getDescKelas());

        FirebaseDatabase.getInstance().getReference().child("auth_masuk")
                .child(userPreference.getKEY_USERNAME())
                .child(dataKelas.getNamaKelas() + "_" + alphabetDesc)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                        if (paramAnonymousDataSnapshot.exists()) {
                            Iterator localIterator = paramAnonymousDataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelAuthentikasi localDataUser = (ModelAuthentikasi) ((DataSnapshot) localIterator.next()).getValue(ModelAuthentikasi.class);
                                listAuthUser.add(new ModelAuthentikasi(localDataUser.getDataKelas(), localDataUser.getDataUser()));
                            }
                            textNothing.setVisibility(View.GONE);
                            adapter = new RecyclerKelasAuth(listAuthUser, getApplicationContext());
                            RecyclerView.LayoutManager layoutAgenda = new LinearLayoutManager(GenerateQr.this);
                            rcAuth.setLayoutManager(layoutAgenda);
                            rcAuth.setAdapter(adapter);
                        } else {
                            textNothing.setVisibility(View.VISIBLE);
                        }
                    }

                    public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
                        rcAuth.setVisibility(View.GONE);
                        textNothing.setVisibility(View.VISIBLE);
                        Toast.makeText(GenerateQr.this, "Gagal Mengambil Data Authentikasi User", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRefresh() {
        listAuthUser.removeAll(listAuthUser);
        rcAuth.removeAllViews();

        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh.setRefreshing(false);
                getAuthUser();
            }
        }, 2000);
    }

    private void showTutorSequence(int paramInt) {
        ShowcaseConfig localShowcaseConfig = new ShowcaseConfig();
        localShowcaseConfig.setDelay(paramInt);
        MaterialShowcaseSequence localMaterialShowcaseSequence = new MaterialShowcaseSequence(GenerateQr.this, "Sequence Showcase");
        localMaterialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView paramAnonymousMaterialShowcaseView, int paramAnonymousInt) {
            }
        });
        localMaterialShowcaseSequence.setConfig(localShowcaseConfig);
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(GenerateQr.this)
                .setTarget(imageView).setDismissText("Selanjutnya")
                .setContentText("Ini adalah QR Code dari kelas ini").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(GenerateQr.this)
                .setTarget(rl).setDismissText("Baik")
                .setContentText("Siswa yang sudah scan QR Code, akan masuk ke sini dan memerlukan persetujuan anda, silahkan swipe ke atas untuk me refresh").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}