package com.exomatik.classroom.classroom.Activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerLihatTugasPengajar;
import com.exomatik.classroom.classroom.Dialog.CustomDialogBuatTugas;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelTugas;
import com.exomatik.classroom.classroom.Model.ModelUploadedTugas;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class BuatTugas extends AppCompatActivity implements ItemClickSupport.OnItemClickListener {
    public static ModelKelas dataKelas;
    private ImageView back, btnHelp;
    private Spinner spinnerTugas;
    private RecyclerView rcTugas;
    private RelativeLayout btnBuatTugas, btnEdit;
    private TextView textNothing, textNothingUploaded;
    private NestedScrollView svTugas;
    private ArrayList<String> listTugas = new ArrayList<String>();
    private ArrayList<ModelUploadedTugas> uploadedTugas = new ArrayList<ModelUploadedTugas>();
    private ArrayList<ModelTugas> dataTugas = new ArrayList<ModelTugas>();
    private ModelTugas tugas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_tugas);

        back = (ImageView) findViewById(R.id.back);
        btnHelp = (ImageView) findViewById(R.id.btn_help);
        spinnerTugas = (Spinner) findViewById(R.id.spinner_tugas);
        rcTugas = (RecyclerView) findViewById(R.id.rc_tugas);
        btnBuatTugas = (RelativeLayout) findViewById(R.id.rl_buat);
        btnEdit = (RelativeLayout) findViewById(R.id.rl_edit);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        textNothingUploaded = (TextView) findViewById(R.id.text_nothing_uploade);
        svTugas = (NestedScrollView) findViewById(R.id.sv_tugas);

        tugas = null;
        getDataTugas();
        ItemClickSupport.addTo(rcTugas).setOnItemClickListener(this);

        spinnerTugas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                btnEdit.setVisibility(View.VISIBLE);
                String namaTugas = listTugas.get(position);
                getUploadedTugas(namaTugas);
                tugas = dataTugas.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buatTugas(tugas);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailKelas.dataKelas = dataKelas;
                dataKelas = null;
                startActivity(new Intent(BuatTugas.this, DetailKelas.class));
                finish();
            }
        });

        btnBuatTugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buatTugas(null);
            }
        });
    }

    private String alphabet(String desc) {
        String alphabetDesc = null;
        if (desc.length() == 1) {
            alphabetDesc = desc.substring(0, 1);
        } else if (desc.length() >= 2) {
            alphabetDesc = desc.substring(0, 2);
        }
        return alphabetDesc;
    }

    private void getDataTugas() {
        FirebaseDatabase.getInstance().getReference("tugas")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelTugas localDataUser = (ModelTugas) ((DataSnapshot) localIterator.next()).getValue(ModelTugas.class);
                                listTugas.add(localDataUser.getNamaTugas());
                                dataTugas.add(localDataUser);
                            }

                            ArrayAdapter<String> dataNilai = new ArrayAdapter<String>(BuatTugas.this,
                                    R.layout.spinner_text_putih, listTugas);
                            spinnerTugas.setAdapter(dataNilai);
                        } else {
                            spinnerTugas.setVisibility(View.GONE);
                            svTugas.setVisibility(View.GONE);
                            textNothing.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(BuatTugas.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getUploadedTugas(final String namaTugas) {
        Query query = FirebaseDatabase.getInstance().getReference("tugas_uploaded")
                .orderByChild("usernamePengajar")
                .equalTo(dataKelas.getUserNamePengajar());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    uploadedTugas.removeAll(uploadedTugas);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelUploadedTugas data = snapshot.getValue(ModelUploadedTugas.class);

                        if (data.getKelas().equals(dataKelas.getNamaKelas())
                                && data.getDesc().equals(dataKelas.getDescKelas())) {
                            if (data.getNamaTugas().equals(namaTugas)){
                                uploadedTugas.add(data);
                            }
                        }
                    }

                    if (uploadedTugas.size() == 0){
                        textNothingUploaded.setVisibility(View.VISIBLE);
                        svTugas.setVisibility(View.GONE);
                    }
                    else {
                        textNothingUploaded.setVisibility(View.GONE);
                        svTugas.setVisibility(View.VISIBLE);
                        RecyclerLihatTugasPengajar adapterLihatKelas = new RecyclerLihatTugasPengajar(uploadedTugas);
                        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(BuatTugas.this, 1, false);
                        rcTugas.setLayoutManager(localLinearLayoutManager);
                        rcTugas.setNestedScrollingEnabled(false);
                        rcTugas.setAdapter(adapterLihatKelas);
                    }
                }
                else {
                    svTugas.setVisibility(View.GONE);
                    textNothingUploaded.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                svTugas.setVisibility(View.GONE);
                textNothingUploaded.setVisibility(View.VISIBLE);
                Toast.makeText(BuatTugas.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buatTugas(ModelTugas tugas) {
        if (tugas != null){
            CustomDialogBuatTugas.tugas = tugas;
        }
        CustomDialogBuatTugas.dataKelas = dataKelas;
        DialogFragment newFragment = CustomDialogBuatTugas
                .newInstance();

        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onBackPressed() {
        DetailKelas.dataKelas = dataKelas;
        dataKelas = null;
        startActivity(new Intent(BuatTugas.this, DetailKelas.class));
        finish();
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        DetailTugasUploaded.dataTugas = uploadedTugas.get(position);
        startActivity(new Intent(BuatTugas.this, DetailTugasUploaded.class));
        finish();
    }
}
