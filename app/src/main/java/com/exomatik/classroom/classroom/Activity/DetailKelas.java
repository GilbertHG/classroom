package com.exomatik.classroom.classroom.Activity;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Dialog.CustomDialogExcel;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKehadiran;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.ModelPenilaian;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class DetailKelas extends AppCompatActivity {
    public static ModelKelas dataKelas;
    private ImageView back, btnHelp;
    private CircleImageView btnHapus, btnGenerate, btnNilai, btnHadir, btnTugas, btnExport, btnQuiz;
    private UserPreference userPreference;
    private View view;
    private TextView textDesc, textJumlah, textPengajar, textKelas, textNothing;
    private TableLayout tableLihatKelas;
    private RadioGroup rgLihat;
    private int nilai[] = new int[dataKelas.getModelNilaiKelas().size()];
    private boolean cekNilaiAkhir = false;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kelas);

        back = (ImageView) findViewById(R.id.back);
        btnHapus = (CircleImageView) findViewById(R.id.img_hapus);
        btnGenerate = (CircleImageView) findViewById(R.id.img_generate_qr);
        btnTugas = (CircleImageView) findViewById(R.id.img_tugas);
        btnNilai = (CircleImageView) findViewById(R.id.img_penilaian);
        btnHadir = (CircleImageView) findViewById(R.id.img_kehadiran);
        btnExport = (CircleImageView) findViewById(R.id.img_export);
        btnQuiz = (CircleImageView) findViewById(R.id.img_quiz);
        textDesc = (TextView) findViewById(R.id.text_desc_isi);
        textJumlah = (TextView) findViewById(R.id.text_jumlah_isi);
        textPengajar = (TextView) findViewById(R.id.text_pengajar_isi);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        textKelas = (TextView) findViewById(R.id.text_kelas);
        view = (View) findViewById(android.R.id.content);
        tableLihatKelas = (TableLayout) findViewById(R.id.tb_lihat_kelas);
        rgLihat = (RadioGroup) findViewById(R.id.rg_lihat);
        btnHelp = (ImageView) findViewById(R.id.img_help);

        userPreference = new UserPreference(this);

        textKelas.setText(dataKelas.getNamaKelas());
        textDesc.setText(dataKelas.getDescKelas());
        getDataPengajar(dataKelas.getUserNamePengajar());

        if (dataKelas.getLihat()) {
            rgLihat.check(R.id.rb_true);
        } else {
            rgLihat.check(R.id.rb_false);
        }

        previewTitleTable();
        getDataSiswa();

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialShowcaseView.resetSingleUse(DetailKelas.this, "Sequence Showcase");
                showTutorSequence(0);
            }
        });

        rgLihat.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final int id = group.getCheckedRadioButtonId();
                final String alphabetDesc = alphabet(dataKelas.getDescKelas());
                final RadioButton rb = findViewById(id);
                boolean lihat = false;

                if (rb.getText().toString().equalsIgnoreCase("Perlihatkan")) {
                    lihat = true;
                } else {
                    lihat = false;
                }

                DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
                localDatabaseReference.child("kelas")
                        .child(userPreference.getKEY_USERNAME())
                        .child(dataKelas.getNamaKelas() + "_" + alphabetDesc)
                        .child("lihat")
                        .setValue(lihat);

                Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                        .orderByChild("userNamePengajar")
                        .equalTo(userPreference.getKEY_USERNAME());
                query.addListenerForSingleValueEvent(valueEventListener);

                Toast.makeText(DetailKelas.this, "Berhasil Memperbarui Kelas", Toast.LENGTH_SHORT).show();
            }
        });

        btnTugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityTugas();
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusKelas();
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportToExcel();
            }
        });

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buatQuiz();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQrCode(dataKelas.getNamaKelas(), dataKelas.getDescKelas(), dataKelas.getUserNamePengajar(), dataKelas.getLihat());
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKelas = null;
                MainActivity.menu = true;
                startActivity(new Intent(DetailKelas.this, MainActivity.class));
                finish();
            }
        });
    }

    private void buatQuiz() {
        InviteBuatQuiz.dataKelas = dataKelas;
        startActivity(new Intent(DetailKelas.this, InviteBuatQuiz.class));
        finish();
    }

    private void activityTugas() {
        BuatTugas.dataKelas = dataKelas;
        startActivity(new Intent(DetailKelas.this, BuatTugas.class));
        finish();
    }

    private void exportToExcel() {
        CustomDialogExcel.tableLayout = tableLihatKelas;
        CustomDialogExcel.dataKelas = dataKelas;
        DialogFragment newFragment = CustomDialogExcel
                .newInstance();

        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTextTabel(int width, String text, TableRow tableRow, int index, int color) {
        TextView textView = new TextView(this);

        if (index % 2 == 0){
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        }
        else{
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih_gelap));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        textView.setWidth(width);
        textView.setGravity(1);
        textView.setPadding(5, 5, 5, 5);

        textView.setTextColor(getResources().getColor(color));
        textView.setTextSize(14);
        textView.setText(text);

        tableRow.addView(textView, index);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTextNama(int width, String text, TableRow tableRow, int index, int color, final String usernameSiswa) {
        final TextView textView = new TextView(this);

        if (index % 2 == 0){
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        }
        else{
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih_gelap));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        textView.setWidth(width);
        textView.setGravity(1);
        textView.setPadding(5, 5, 5, 5);

        textView.setTextColor(getResources().getColor(color));
        textView.setTextSize(14);
        textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textView.setText(text);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailProfile.usernameSiswa = usernameSiswa;
                startActivity(new Intent(DetailKelas.this, DetailProfile.class));
            }
        });
        tableRow.addView(textView, index);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTextTabelGabungSamping(int width, String text, TableRow tableRow, int index, int color, int jumlahGabung) {
        TextView textView = new TextView(this);

        TableRow.LayoutParams params = new TableRow.LayoutParams(300
                , TableLayout.LayoutParams.WRAP_CONTENT);
        params.span = jumlahGabung;
        textView.setLayoutParams(params); // causes layout update

        if (index % 2 == 0){
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        }
        else{
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih_gelap));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        textView.setWidth(width);
        textView.setGravity(1);
        textView.setPadding(5, 5, 5, 5);

        textView.setTextColor(getResources().getColor(color));
        textView.setTextSize(14);
        textView.setText(text);

        tableRow.addView(textView, index);
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

    private void getDataSiswa() {
        Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                .orderByChild("userNamePengajar")
                .equalTo(dataKelas.getUserNamePengajar());
        query.addListenerForSingleValueEvent(valueEventListener2);
    }

    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final ArrayList<ModelKelasSiswa> listSiswa = new ArrayList<ModelKelasSiswa>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelKelasSiswa data = snapshot.getValue(ModelKelasSiswa.class);

                    if (dataKelas != null) {
                        if ((data.getNamaKelas().toString().equals(dataKelas.getNamaKelas()))
                                && (data.getDescKelas().toString().equals(dataKelas.getDescKelas()))) {
                            listSiswa.add(data);
                        }
                    }
                }

                btnNilai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String jumlah = textJumlah.getText().toString();

                        if (jumlah.isEmpty()) {
                            Toast.makeText(DetailKelas.this, "Maaf, anda belum memiliki siswa di kelas ini", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Integer.parseInt(jumlah) > 0) {
                                TambahPenilaian.dataKelas = dataKelas;
                                TambahPenilaian.listSiswa = listSiswa;
                                startActivity(new Intent(DetailKelas.this, TambahPenilaian.class));
                                finish();
                            } else {
                                Toast.makeText(DetailKelas.this, "Maaf, anda belum memiliki siswa di kelas ini", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                btnHadir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String jumlah = textJumlah.getText().toString();

                        if (Integer.parseInt(jumlah) > 0) {
                            TambahKehadiran.dataKelas = dataKelas;
                            TambahKehadiran.listSiswa = listSiswa;
                            startActivity(new Intent(DetailKelas.this, TambahKehadiran.class));
                            finish();
                        } else {
                            Toast.makeText(DetailKelas.this, "Maaf, anda belum memiliki siswa di kelas ini", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                setTabelSiswa(listSiswa);
                textJumlah.setText(Integer.toString(listSiswa.size()));
            } else {
                textJumlah.setText("0");
            }

            if (listSiswa.size() == 0) {
                textNothing.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            textJumlah.setText("0");
            Toast.makeText(DetailKelas.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelKelasSiswa data = snapshot.getValue(ModelKelasSiswa.class);

                    if (data.getNamaKelas().equals(dataKelas.getNamaKelas()) && data.getDescKelas().equals(dataKelas.getDescKelas())) {
                        String key = snapshot.getKey();
                        boolean lihat2 = false;
                        RadioButton rb = findViewById(rgLihat.getCheckedRadioButtonId());

                        DatabaseReference localDatabaseReference2 = FirebaseDatabase.getInstance().getReference();

                        if (rb.getText().toString().equalsIgnoreCase("Perlihatkan")) {
                            lihat2 = true;
                        } else {
                            lihat2 = false;
                        }

                        localDatabaseReference2.child("kelas_siswa")
                                .child(key)
                                .child("lihat")
                                .setValue(lihat2);
                    }
                }
            } else {
                Toast.makeText(DetailKelas.this, "null", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(DetailKelas.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void generateQrCode(String namaKelas, String descKelas, String userNamePengajar, boolean lihat) {
        GenerateQr.dataKelas = new ModelKelas(namaKelas, descKelas, userNamePengajar, null, lihat);
        startActivity(new Intent(DetailKelas.this, GenerateQr.class));
        finish();
    }

    private void getDataPengajar(final String username) {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator localIterator = dataSnapshot.getChildren().iterator();
                    while (localIterator.hasNext()) {
                        UserData localDataUser = (UserData) ((DataSnapshot) localIterator.next()).getValue(UserData.class);
                        if (localDataUser.getUsername().toString().equalsIgnoreCase(username)) {
                            textPengajar.setText(localDataUser.getNama());
                        }
                    }
                } else {
                    Toast.makeText(DetailKelas.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailKelas.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hapusKelas() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DetailKelas.this);

        alert.setTitle("Hapus");
        alert.setMessage("Apakah anda yakin ingin menghapus kelas ini?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference db_remove_kelas = FirebaseDatabase.getInstance().getReference().child("kelas")
                        .child(dataKelas.getUserNamePengajar())
                        .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()));
                db_remove_kelas.removeValue();

                DatabaseReference db_remove_auth = FirebaseDatabase.getInstance().getReference().child("auth_masuk")
                        .child(dataKelas.getUserNamePengajar())
                        .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()));
                db_remove_auth.removeValue();

                for (int a = 0; a < dataKelas.getModelNilaiKelas().size(); a++){
                    DatabaseReference db_remove_nilai = FirebaseDatabase.getInstance().getReference().child("nilai")
                            .child(dataKelas.getModelNilaiKelas().get(a).getNama())
                            .child(dataKelas.getUserNamePengajar())
                            .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()));
                    db_remove_nilai.removeValue();
                }

                Query db_remove_kelas_siswa = FirebaseDatabase.getInstance().getReference()
                        .child("kelas_siswa")
                        .orderByChild("userNamePengajar")
                        .equalTo(dataKelas.getUserNamePengajar());

                db_remove_kelas_siswa.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ModelKelasSiswa> list = new ArrayList<ModelKelasSiswa>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ModelKelasSiswa data = snapshot.getValue(ModelKelasSiswa.class);

                                if (data.getDescKelas().equals(dataKelas.getDescKelas())){
                                    list.add(data);
                                    DatabaseReference remove = FirebaseDatabase.getInstance().getReference()
                                            .child("kelas_siswa")
                                            .child(dataKelas.getUserNamePengajar()
                                                    + "_" + dataKelas.getNamaKelas()
                                                    + "_" + alphabet(dataKelas.getDescKelas())
                                                    + "_" + data.getUsername());
                                    remove.removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DetailKelas.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.dismiss();
                startActivity(new Intent(DetailKelas.this, MainActivity.class));
                finish();
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    @Override
    public void onBackPressed() {
        dataKelas = null;
        MainActivity.menu = true;
        startActivity(new Intent(DetailKelas.this, MainActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTabelSiswa(final ArrayList<ModelKelasSiswa> listSiswa) {
        int nomor = 1;
        for (int posisiSiswa = 0; posisiSiswa < listSiswa.size(); posisiSiswa++) {
            final TableRow tableRow = new TableRow(this);
            tableRow.removeAllViews();
            tableRow.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));

            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            setTextTabel(75, Integer.toString(nomor), tableRow, 0, R.color.hitam);
            nomor++;

            int indexSamping = 1;
            boolean hadir = false;

            for (int jenisNilai = 0; jenisNilai < dataKelas.getModelNilaiKelas().size(); jenisNilai++) {
                if (dataKelas.getModelNilaiKelas().get(jenisNilai).isNamaPelajar()) {
                    setTextNama(500, listSiswa.get(posisiSiswa).getNama(), tableRow, indexSamping, R.color.blue1, listSiswa.get(posisiSiswa).getUsername());
                    indexSamping++;
                } else if (dataKelas.getModelNilaiKelas().get(jenisNilai).isKehadiran()) {
                    hadir = true;
                    for (int pertemuan = 0; pertemuan < dataKelas.getModelNilaiKelas().get(jenisNilai).getJumlah(); pertemuan++) {
                        getDataKehadiran(pertemuan, indexSamping, listSiswa, tableRow, listSiswa.get(posisiSiswa).getUsername());
                        indexSamping++;
                    }
                } else {
                    for (int banyakNilai = 0; banyakNilai < dataKelas.getModelNilaiKelas().get(jenisNilai).getJumlah(); banyakNilai++) {
                        getDataPenilaian(banyakNilai, indexSamping, listSiswa, tableRow, listSiswa.get(posisiSiswa).getUsername()
                                , jenisNilai, dataKelas.getModelNilaiKelas().size(), jenisNilai);
                        indexSamping++;
                    }
                }
            }
            if (hadir) {
                btnHadir.setVisibility(View.VISIBLE);
            }

            int ind = 2;

            TextView textView = new TextView(this);
            if (ind % 2 == 0){
                textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
            }
            else {
                textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih_gelap));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            textView.setWidth(200);
            textView.setGravity(1);
            textView.setPadding(5, 5, 5, 5);

            textView.setTextColor(getResources().getColor(R.color.hitam));
            textView.setTextSize(14);
            textView.setText("");

            tableRow.addView(textView, ind);

            ind++;

            TextView textAkhir = new TextView(this);
            if (ind % 2 == 0){
                textAkhir.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
            }
            else {
                textAkhir.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih_gelap));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textAkhir.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            textAkhir.setWidth(200);
            textAkhir.setGravity(1);
            textAkhir.setPadding(5, 5, 5, 5);

            textAkhir.setTextColor(getResources().getColor(R.color.hitam));
            textAkhir.setTextSize(14);
            textAkhir.setText("");

            tableRow.addView(textAkhir, ind);

            tableLihatKelas.addView(tableRow);
        }
    }

    private void getDataPenilaian(final int penilaian, final int indexSamping, final ArrayList<ModelKelasSiswa> listSiswa
            , final TableRow tableRow, final String username, final int index, final int banyakJenisNilai, final int nilaiJenis) {
        FirebaseDatabase.getInstance().getReference("nilai")
                .child(dataKelas.getModelNilaiKelas().get(index).getNama())
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child("Nilai Ke-" + Integer.toString(penilaian))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ModelPenilaian dataPenilaianSiswa = null;

                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelPenilaian localDataUser = (ModelPenilaian) ((DataSnapshot) localIterator.next()).getValue(ModelPenilaian.class);

                                if (localDataUser.getUsernameSiswa().toString().equals(username)) {
                                    dataPenilaianSiswa = new ModelPenilaian(localDataUser.getUsernamePengajar()
                                            , localDataUser.getUsernameSiswa(), localDataUser.getKelas()
                                            , localDataUser.getDesc(), localDataUser.getJenis()
                                            , localDataUser.getNilai(), localDataUser.getPertemuan());
                                }
                            }
                        } else {
                            for (int a = 0; a < listSiswa.size(); a++) {
                                dataPenilaianSiswa = new ModelPenilaian(dataKelas.getUserNamePengajar()
                                        , listSiswa.get(a).getUsername()
                                        , dataKelas.getNamaKelas()
                                        , dataKelas.getDescKelas()
                                        , "nilai"
                                        , 404
                                        , penilaian);
                            }
                        }

                        setTextPenilaian(tableRow, indexSamping, dataPenilaianSiswa, banyakJenisNilai, nilaiJenis);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DetailKelas.this, "Gagal Mengambil Nilai", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTextPenilaian(TableRow tableRow, int posisiTabel, ModelPenilaian listNilai, int banyakJenisNilai, int nilaiJenis) {
        if (listNilai.getNilai() == 404) {
            setTextTabel(200, "", tableRow, posisiTabel, R.color.hitam);
            cekNilaiAkhir = true;
        } else if (listNilai.getNilai() >= 0 && listNilai.getNilai() <= 50) {
            setTextTabel(200, Integer.toString(listNilai.getNilai()), tableRow, posisiTabel, R.color.red);
        } else if (listNilai.getNilai() >= 51 && listNilai.getNilai() <= 70) {
            setTextTabel(200, Integer.toString(listNilai.getNilai()), tableRow, posisiTabel, R.color.orange);
        } else if (listNilai.getNilai() >= 71 && listNilai.getNilai() <= 80) {
            setTextTabel(200, Integer.toString(listNilai.getNilai()), tableRow, posisiTabel, R.color.yellow);
        } else if (listNilai.getNilai() >= 81 && listNilai.getNilai() <= 100) {
            setTextTabel(200, Integer.toString(listNilai.getNilai()), tableRow, posisiTabel, R.color.green);
        }

        if (nilaiJenis < banyakJenisNilai){
            if (cekNilaiAkhir){

            }else {
                getData();
            }
        }
    }

    private void getDataKehadiran(final int pertemuan, final int posisiTabel, final ArrayList<ModelKelasSiswa> listSiswa
            , final TableRow tableRow, final String usernameSiswa) {
        if (dataKelas != null){
            FirebaseDatabase.getInstance().getReference("nilai")
                    .child("kehadiran")
                    .child(dataKelas.getUserNamePengajar())
                    .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                    .child("Pertemuan " + Integer.toString(pertemuan))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ModelKehadiran dataKehadiranSiswa = null;

                            if (dataSnapshot.exists()) {
                                Iterator localIterator = dataSnapshot.getChildren().iterator();
                                while (localIterator.hasNext()) {
                                    ModelKehadiran localDataUser = (ModelKehadiran) ((DataSnapshot) localIterator.next()).getValue(ModelKehadiran.class);

                                    if (localDataUser.getUsernameSiswa().toString().equals(usernameSiswa)) {
                                        dataKehadiranSiswa = new ModelKehadiran(localDataUser.getUsernamePengajar()
                                                , localDataUser.getUsernameSiswa(), localDataUser.getKelas()
                                                , localDataUser.getDesc(), localDataUser.getHadir(), localDataUser.getPertemuan());
                                    }
                                }
                            } else {
                                for (int a = 0; a < listSiswa.size(); a++) {
                                    dataKehadiranSiswa = new ModelKehadiran(dataKelas.getUserNamePengajar()
                                            , listSiswa.get(a).getUsername()
                                            , dataKelas.getNamaKelas()
                                            , dataKelas.getDescKelas()
                                            , 4
                                            , pertemuan);
                                }
                            }

                            setTextKehadiran(tableRow, posisiTabel, dataKehadiranSiswa);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(DetailKelas.this, "Gagal Mengambil Data Kehadiran", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTextKehadiran(TableRow tableRow, int posisiTabel, ModelKehadiran listKehadiran) {
        if (listKehadiran.getHadir() == 0) {
            setTextTabel(200, "Tidak Hadir", tableRow, posisiTabel, R.color.red);
        } else if (listKehadiran.getHadir() == 1) {
            setTextTabel(200, "Hadir", tableRow, posisiTabel, R.color.green);
        } else if (listKehadiran.getHadir() == 2) {
            setTextTabel(200, "Sakit", tableRow, posisiTabel, R.color.yellow);
        } else if (listKehadiran.getHadir() == 3) {
            setTextTabel(200, "Izin", tableRow, posisiTabel, R.color.orange);
        } else if (listKehadiran.getHadir() == 4) {
            setTextTabel(200, "", tableRow, posisiTabel, R.color.hitam);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void previewTitleTable() {
        tableLihatKelas.removeAllViews();
        for (int a = 0; a < 2; a++) {
            TableRow tableRow = new TableRow(this);
            tableRow.removeAllViews();
            tableRow.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));

            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            if (a == 0) {
                int totalNilai = dataKelas.getModelNilaiKelas().size();
                setTextTabel(75, "No. ", tableRow, 0, R.color.hitam);

                int number = 1;
                for (int index = 0; index < dataKelas.getModelNilaiKelas().size(); index++) {
                    if (dataKelas.getModelNilaiKelas().get(index).isNamaPelajar()) {
                        setTextTabelGabungSamping(500, dataKelas.getModelNilaiKelas().get(index).getNama()
                                , tableRow, number, R.color.hitam, dataKelas.getModelNilaiKelas().get(index).getJumlah());
                    } else {
                        setTextTabelGabungSamping(500, dataKelas.getModelNilaiKelas().get(index).getNama()
                                        + "(" + Integer.toString(dataKelas.getModelNilaiKelas().get(index).getPersentase()) + " %)"
                                , tableRow, number, R.color.hitam, dataKelas.getModelNilaiKelas().get(index).getJumlah());
                    }
                    number++;
                }
                totalNilai++;
                setTextTabel(300, "Nilai Akhir Angka", tableRow, totalNilai, R.color.hitam);
                totalNilai++;
                setTextTabel(300, "Nilai Akhir Huruf", tableRow, totalNilai, R.color.hitam);

                tableLihatKelas.addView(tableRow);
            } else if (a == 1) {
                setTextTabel(75, "", tableRow, 0, R.color.hitam);
                int number = 1;
                int indexData = 0;
                boolean stop = true;

                while (indexData < dataKelas.getModelNilaiKelas().size()) {
                    int looping = 0;
                    int textTampil = 1;
                    do {
                        if (dataKelas.getModelNilaiKelas().get(indexData).getJumlah() == 1) {
                            setTextTabel(200, "", tableRow, number, R.color.hitam);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= dataKelas.getModelNilaiKelas().get(indexData).getJumlah()) {
                                stop = false;
                            }
                        } else if (dataKelas.getModelNilaiKelas().get(indexData).getJumlah() == 2) {
                            setTextTabel(150, Integer.toString(textTampil), tableRow, number, R.color.hitam);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= dataKelas.getModelNilaiKelas().get(indexData).getJumlah()) {
                                stop = false;
                            }
                        } else if (dataKelas.getModelNilaiKelas().get(indexData).getJumlah() == 3) {
                            setTextTabel(100, Integer.toString(textTampil), tableRow, number, R.color.hitam);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= dataKelas.getModelNilaiKelas().get(indexData).getJumlah()) {
                                stop = false;
                            }
                        } else {
                            setTextTabel(50, Integer.toString(textTampil), tableRow, number, R.color.hitam);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= dataKelas.getModelNilaiKelas().get(indexData).getJumlah()) {
                                stop = false;
                            }
                        }
                    } while (stop);
                    stop = true;
                    indexData++;
                }
                int totalNilai = number;

                setTextTabel(300, "", tableRow, totalNilai, R.color.hitam);
                totalNilai++;
                setTextTabel(300, "", tableRow, totalNilai, R.color.hitam);
                totalNilai++;

                tableLihatKelas.addView(tableRow);
            }
        }
    }

    private void getData() {
        if (dataKelas != null){
            Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                    .orderByChild("userNamePengajar")
                    .equalTo(dataKelas.getUserNamePengajar());
            query.addListenerForSingleValueEvent(valueEventListener3);
        }
    }

    ValueEventListener valueEventListener3 = new ValueEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final ArrayList<ModelKelasSiswa> listSiswa = new ArrayList<ModelKelasSiswa>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelKelasSiswa data = snapshot.getValue(ModelKelasSiswa.class);

                    if (dataKelas != null) {
                        if ((data.getNamaKelas().toString().equals(dataKelas.getNamaKelas()))
                                && (data.getDescKelas().toString().equals(dataKelas.getDescKelas()))) {
                            listSiswa.add(data);
                        }
                    }
                }

                getNilai(listSiswa);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(DetailKelas.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void getNilai(ArrayList<ModelKelasSiswa> listSiswa) {
        int indexSiswa = 0;
        do {
            if (dataKelas != null){
                int indexSamping = 1;
                int index = 0;

                for (int jenisNilai = 0; jenisNilai < dataKelas.getModelNilaiKelas().size(); jenisNilai++) {
                    for (int z = 0; z < dataKelas.getModelNilaiKelas().get(jenisNilai).getJumlah(); z++) {
                        indexSamping++;
                    }
                    if (dataKelas.getModelNilaiKelas().get(jenisNilai).isNamaPelajar()) {
                    } else if (dataKelas.getModelNilaiKelas().get(jenisNilai).isKehadiran()) {
                        getNilaiKehadiran(listSiswa, indexSiswa, jenisNilai, index, indexSamping);
                        index++;
                    } else {
                        getNilaiPenilaian(listSiswa, indexSiswa, jenisNilai, index, indexSamping);
                        index++;
                    }
                }
                indexSiswa++;
            }
        } while (indexSiswa < listSiswa.size());
    }

    private void getNilaiKehadiran(final ArrayList<ModelKelasSiswa> listSiswa, final int siswa
            , final int jenisNilai, final int index, final int indexSamping) {
        FirebaseDatabase.getInstance().getReference("nilai")
                .child("kehadiran")
                .child(userPreference.getKEY_USERNAME())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator localIterator1 = dataSnapshot.getChildren().iterator();
                        ArrayList<ModelKehadiran> listNilaiKehadiran = new ArrayList<ModelKehadiran>();
                        int kehadiran = 0;

                        while (localIterator1.hasNext()) {
                            DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                            Iterator local = localDataSnapshot.getChildren().iterator();
                            while (local.hasNext()) {
                                DataSnapshot dataDS = (DataSnapshot) local.next();
                                ModelKehadiran data = (ModelKehadiran) ((DataSnapshot) dataDS).getValue(ModelKehadiran.class);

                                if (data != null) {
                                    if (data.getUsernameSiswa().toString().equals(listSiswa.get(siswa).getUsername())) {
                                        listNilaiKehadiran.add(data);
                                    }
                                }
                            }
                        }

                        for (int a = 0; a < listNilaiKehadiran.size(); a++) {
                            if (listNilaiKehadiran.get(a).getHadir() == 0) {
                                kehadiran = kehadiran + 0;
                            } else if (listNilaiKehadiran.get(a).getHadir() == 1) {
                                kehadiran = kehadiran + 100;
                            } else if (listNilaiKehadiran.get(a).getHadir() == 2) {
                                kehadiran = kehadiran + 50;
                            } else if (listNilaiKehadiran.get(a).getHadir() == 3) {
                                kehadiran = kehadiran + 50;
                            }
                        }
                        int nilaiAkhir = (kehadiran / dataKelas.getModelNilaiKelas().get(jenisNilai).getJumlah()
                                * dataKelas.getModelNilaiKelas().get(jenisNilai).getPersentase()) / 100;
                        nilai[index] = nilaiAkhir;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DetailKelas.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getNilaiPenilaian(final ArrayList<ModelKelasSiswa> listSiswa, final int siswa, final int jenisNilai
            , final int index, final int indexSamping) {
        FirebaseDatabase.getInstance().getReference("nilai")
                .child(dataKelas.getModelNilaiKelas().get(jenisNilai).getNama())
                .child(userPreference.getKEY_USERNAME())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator localIterator1 = dataSnapshot.getChildren().iterator();
                        ArrayList<ModelPenilaian> listNilaiPenilaian = new ArrayList<ModelPenilaian>();
                        int penilaian = 0;

                        while (localIterator1.hasNext()) {
                            DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                            Iterator local = localDataSnapshot.getChildren().iterator();
                            while (local.hasNext()) {
                                DataSnapshot dataDS = (DataSnapshot) local.next();
                                ModelPenilaian data = (ModelPenilaian) ((DataSnapshot) dataDS).getValue(ModelPenilaian.class);

                                if (data != null) {
                                    if (data.getUsernameSiswa().toString().equals(listSiswa.get(siswa).getUsername())) {
                                        listNilaiPenilaian.add(data);
                                    }
                                }
                            }
                        }

                        for (int a = 0; a < listNilaiPenilaian.size(); a++) {
                            penilaian = penilaian + listNilaiPenilaian.get(a).getNilai();
                        }

                        int nilaiAkhir = (penilaian / dataKelas.getModelNilaiKelas().get(jenisNilai).getJumlah()
                                * dataKelas.getModelNilaiKelas().get(jenisNilai).getPersentase()) / 100;

                        nilai[index] = nilaiAkhir;

                        int jenis = jenisNilai + 1;
                        if (jenis == dataKelas.getModelNilaiKelas().size()) {
                            nil(true, indexSamping, siswa);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DetailKelas.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void nil(boolean lastIndex, int index, int posisiSiswa) {
        int posisi = posisiSiswa + 2;
        int hasilNilai = 0;
        for (int a = 0; a < nilai.length; a++) {
            hasilNilai = hasilNilai + nilai[a];
        }

        View view = tableLihatKelas.getChildAt(posisi);
        TableRow row = (TableRow) view;
        row.removeViewAt(index);
        TextView textView = new TextView(this);
        if (index % 2 == 0){
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        }
        else{
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih_gelap));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        textView.setWidth(300);
        textView.setGravity(1);
        textView.setPadding(5, 5, 5, 5);
        textView.setTextColor(getResources().getColor(R.color.hitam));
        textView.setTextSize(14);
        textView.setText(Integer.toString(hasilNilai));

        row.addView(textView, index);

        index++;
        row.removeViewAt(index);

        TextView textHuruf = new TextView(this);
        if (index % 2 == 0){
            textHuruf.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        }
        else{
            textHuruf.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih_gelap));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            textHuruf.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        textHuruf.setWidth(300);
        textHuruf.setGravity(1);
        textHuruf.setPadding(5, 5, 5, 5);

        textHuruf.setTextColor(getResources().getColor(R.color.hitam));
        textHuruf.setTextSize(14);

        textHuruf.setText(Character.toString(nilaiKarakter(hasilNilai)));

        row.addView(textHuruf, index);
    }

    private char nilaiKarakter(int nilai) {
        char karakter = 'e';

        if (nilai > 0 && nilai < 50) {
            karakter = 'E';
        } else if (nilai > 50 && nilai < 60) {
            karakter = 'D';
        } else if (nilai > 60 && nilai < 70) {
            karakter = 'C';
        } else if (nilai > 70 && nilai < 80) {
            karakter = 'B';
        } else if (nilai > 80 && nilai <= 100) {
            karakter = 'A';
        }

        return karakter;
    }

    private void showTutorSequence(int paramInt) {
        ShowcaseConfig localShowcaseConfig = new ShowcaseConfig();
        localShowcaseConfig.setDelay(paramInt);
        MaterialShowcaseSequence localMaterialShowcaseSequence = new MaterialShowcaseSequence(DetailKelas.this, "Sequence Showcase");
        localMaterialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView paramAnonymousMaterialShowcaseView, int paramAnonymousInt) {
            }
        });
        localMaterialShowcaseSequence.setConfig(localShowcaseConfig);
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(rgLihat).setDismissText("Selanjutnya")
                .setContentText("Untuk menyembunyikan nilai dari siswa").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(btnHadir).setDismissText("Selanjutnya")
                .setContentText("Untuk mengabsen siswa dengan persentase Hadir = 100, Izin = 50, Sakit = 50 dan Tidak Hadir = 0").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(btnNilai).setDismissText("Selanjutnya")
                .setContentText("Untuk memberikan penilaian pada siswa").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(btnGenerate).setDismissText("Selanjutnya")
                .setContentText("Untuk membuat QR Code yang nantinya QR Code ini di scan oleh siswa untuk masuk ke kelas ini").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(btnExport).setDismissText("Selanjutnya")
                .setContentText("Untuk export tabel di bawah ke format excel").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(btnTugas).setDismissText("Selanjutnya")
                .setContentText("Untuk membuat Tugas untuk siswa dengan batas waktu").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(btnQuiz).setDismissText("Selanjutnya")
                .setContentText("Untuk membuat Quiz dari template yang sudah dibuat").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(btnHapus).setDismissText("Selanjutnya")
                .setContentText("Untuk menghapus kelas").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelas.this)
                .setTarget(tableLihatKelas).setDismissText("Baik")
                .setContentText("Memperlihatkan informasi penilaian dan informasi siswa").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}