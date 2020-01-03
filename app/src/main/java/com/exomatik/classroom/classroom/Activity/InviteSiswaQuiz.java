package com.exomatik.classroom.classroom.Activity;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerPilihSiswa;
import com.exomatik.classroom.classroom.Featured.RandomString;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.Model.ModelTemplateQuiz;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class InviteSiswaQuiz extends AppCompatActivity {
    public static ModelKelas dataKelas;
    public static ModelTemplateQuiz dataQuiz;
    private ArrayList<ModelQuizInvite> listSiswa = new ArrayList<ModelQuizInvite>();
    private ImageView back, btnHelp;
    private TextView textNothing;
    private RecyclerView rcSiswa;
    private Button btnBuat;
    private CheckBox cbCheck;
    private RecyclerPilihSiswa adapterLihatKelas = new RecyclerPilihSiswa(listSiswa);
    private ProgressDialog progressDialog;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_siswa_quiz);

        back = (ImageView) findViewById(R.id.back);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        rcSiswa = (RecyclerView) findViewById(R.id.rc_siswa);
        btnHelp = (ImageView) findViewById(R.id.btn_help);
        btnBuat = (Button) findViewById(R.id.btn_buat);
        cbCheck = (CheckBox) findViewById(R.id.cb_check_all);
        view = (View) findViewById(android.R.id.content);

        getDataSiswa();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailKelas.dataKelas = dataKelas;
                listSiswa.removeAll(listSiswa);
                dataKelas = null;
                dataQuiz = null;
                startActivity(new Intent(InviteSiswaQuiz.this, DetailKelas.class));
                finish();
            }
        });

        btnBuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listSiswa.size() == 0){
                    progressDialog.dismiss();
                    Toast.makeText(InviteSiswaQuiz.this, "Anda tidak mempunyai siswa", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog = new ProgressDialog(InviteSiswaQuiz.this);
                    progressDialog.setMessage("Mohon Tunggu...");
                    progressDialog.setTitle("Proses");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    boolean cek = false;

                    for (int a = 0; a < listSiswa.size(); a++){
                        if (listSiswa.get(a).isIkutQuiz()){
                            cek = true;
                        }
                    }

                    if (cek){
                        sendData();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(InviteSiswaQuiz.this, "Anda tidak mempunyai siswa yang di pilih", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    for (int a = 0; a < listSiswa.size(); a++){
                        listSiswa.set(a, new ModelQuizInvite(listSiswa.get(a).getUsername()
                                , listSiswa.get(a).getNamaKelas(), listSiswa.get(a).getDescKelas()
                                , listSiswa.get(a).getUserNamePengajar(), listSiswa.get(a).getNamaQuiz()
                                , listSiswa.get(a).getKodeQuiz(), true, listSiswa.get(a).getTemplateQuiz()
                        ));
                    }
                }
                else {
                    for (int a = 0; a < listSiswa.size(); a++){
                        listSiswa.set(a, new ModelQuizInvite(listSiswa.get(a).getUsername()
                                , listSiswa.get(a).getNamaKelas(), listSiswa.get(a).getDescKelas()
                                , listSiswa.get(a).getUserNamePengajar(), listSiswa.get(a).getNamaQuiz()
                                , listSiswa.get(a).getKodeQuiz(), false, listSiswa.get(a).getTemplateQuiz()
                        ));
                    }
                }
                adapterLihatKelas.notifyDataSetChanged();
            }
        });
    }

    private void sendData() {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference
                .child("quiz_invitation")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas())
                .child(listSiswa.get(0).getKodeQuiz())
                .setValue(listSiswa)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(InviteSiswaQuiz.this, "Berhasil membuat Quiz", Toast.LENGTH_SHORT).show();
                            MulaiQuizPengajar.dataKelas = dataKelas;
                            MulaiQuizPengajar.kodeQuiz = listSiswa.get(0).getKodeQuiz();
                            startActivity(new Intent(InviteSiswaQuiz.this, MulaiQuizPengajar.class));
                            listSiswa.removeAll(listSiswa);
                            dataKelas = null;
                            dataQuiz = null;
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Snackbar.make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(InviteSiswaQuiz.this, "Error, " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        DetailKelas.dataKelas = dataKelas;
        listSiswa.removeAll(listSiswa);
        dataKelas = null;
        dataQuiz = null;
        startActivity(new Intent(InviteSiswaQuiz.this, DetailKelas.class));
        finish();
    }

    private void getDataSiswa() {
        Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                .orderByChild("userNamePengajar")
                .equalTo(dataKelas.getUserNamePengajar());
        query.addListenerForSingleValueEvent(valueEventListener2);
    }

    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            listSiswa.removeAll(listSiswa);
            RandomString session = new RandomString(5);
            String unique_code = session.nextString();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelKelasSiswa data = snapshot.getValue(ModelKelasSiswa.class);

                    if (dataKelas != null) {
                        if ((data.getNamaKelas().toString().equals(dataKelas.getNamaKelas()))
                                && (data.getDescKelas().toString().equals(dataKelas.getDescKelas()))) {

                            listSiswa.add(new ModelQuizInvite(data.getUsername(), data.getNamaKelas(), data.getDescKelas()
                                    , data.getUserNamePengajar(), dataQuiz.getNamaQuiz(),  unique_code
                                    , false, dataQuiz));
                        }
                    }
                }
                adapterLihatKelas.notifyDataSetChanged();
                LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(InviteSiswaQuiz.this, 1, false);
                rcSiswa.setLayoutManager(localLinearLayoutManager);
                rcSiswa.setNestedScrollingEnabled(false);
                rcSiswa.setAdapter(adapterLihatKelas);
            } else {
                textNothing.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(InviteSiswaQuiz.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    };
}
