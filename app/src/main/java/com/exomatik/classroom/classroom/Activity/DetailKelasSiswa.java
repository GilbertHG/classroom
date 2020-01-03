package com.exomatik.classroom.classroom.Activity;

import android.app.DialogFragment;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerLihatTugasSiswa;
import com.exomatik.classroom.classroom.Dialog.CustomDialogExcel;
import com.exomatik.classroom.classroom.Dialog.CustomDialogLihatTugas;
import com.exomatik.classroom.classroom.Dialog.CustomDialogMasukQuiz;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKehadiran;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.ModelPenilaian;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.Model.ModelTugas;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class DetailKelasSiswa extends AppCompatActivity implements ItemClickSupport.OnItemClickListener {
    public static ModelKelasSiswa dataKelasSiswa;
    private ImageView back, btnHelp;
    private TextView textDesc, textJumlah, textPengajar, textKelas, textPrivate;
    private TableLayout tableLihatKelas;
    private UserPreference userPreference;
    private View view;
    private ModelKelas dataKelas;
    private int nilai[] = new int[25];
    private boolean cekNilaiAkhir = false;
    private NestedScrollView sv_tugas;
    private RecyclerView rcTugas;
    private ArrayList<ModelTugas> listTugas = new ArrayList<ModelTugas>();
    private CircleImageView btnQuiz;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kelas_siswa);

        back = (ImageView) findViewById(R.id.back);
        btnHelp = (ImageView) findViewById(R.id.img_help);
        textDesc = (TextView) findViewById(R.id.text_desc_isi);
        textJumlah = (TextView) findViewById(R.id.text_jumlah_isi);
        textPengajar = (TextView) findViewById(R.id.text_pengajar_isi);
        textKelas = (TextView) findViewById(R.id.text_kelas);
        textPrivate = (TextView) findViewById(R.id.text_private);
        tableLihatKelas = (TableLayout) findViewById(R.id.tb_lihat_kelas);
        view = (View) findViewById(android.R.id.content);
        sv_tugas = (NestedScrollView) findViewById(R.id.nv_tugas);
        rcTugas = (RecyclerView) findViewById(R.id.rc_tugas);
        btnQuiz = (CircleImageView) findViewById(R.id.img_quiz);

        userPreference = new UserPreference(this);
        ItemClickSupport.addTo(rcTugas).setOnItemClickListener(this);

        textKelas.setText(dataKelasSiswa.getNamaKelas());
        textDesc.setText(dataKelasSiswa.getDescKelas());
        getDataPengajar(dataKelasSiswa.getUserNamePengajar());

        getDataKelas();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.menu = false;
                dataKelas = null;
                startActivity(new Intent(DetailKelasSiswa.this, MainActivity.class));
                finish();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialShowcaseView.resetSingleUse(DetailKelasSiswa.this, "Sequence Showcase");
                showTutorSequence(0);
            }
        });

        btnQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogMasukQuiz.dataKelas = dataKelas;
                DialogFragment newFragment = CustomDialogMasukQuiz
                        .newInstance();

                newFragment.setCancelable(false);
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    private void getDataTugas(ModelKelas kelas) {
        FirebaseDatabase.getInstance().getReference("tugas")
                .child(kelas.getUserNamePengajar())
                .child(kelas.getNamaKelas() + "_" + alphabet(kelas.getDescKelas()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                ModelTugas data = snapshot.getValue(ModelTugas.class);

                                String currentTanggal = DateFormat.getDateInstance().format(new Date());
                                String currentWaktu = DateFormat.getTimeInstance().format(new Date());

                                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                                SimpleDateFormat dateFormatterCurrent = new SimpleDateFormat("dd MMM yyyy", Locale.US);
                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                                SimpleDateFormat timeFormatCurrent = new SimpleDateFormat("HH.mm.ss");

                                try {
                                    Date dateTugas = dateFormatter.parse(data.getTanggalTugas());
                                    Date dateCurrent = dateFormatterCurrent.parse(currentTanggal);
                                    Date timeTugas = timeFormat.parse(data.getWaktuTugas());
                                    Date timeCurrent = timeFormatCurrent.parse(currentWaktu);

                                    if (dateTugas.compareTo(dateCurrent) > 0){
                                        listTugas.add(data);
                                    }
                                    else if (dateTugas.compareTo(dateCurrent) == 0){
                                        if (timeTugas.compareTo(timeCurrent) > 0){
                                            listTugas.add(data);
                                        }
                                    }
                                } catch (ParseException e) {
                                    Log.e("Error", e.getMessage().toString());
                                }
                            }

                            RecyclerLihatTugasSiswa adapterLihatKelas = new RecyclerLihatTugasSiswa(listTugas);
                            LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(DetailKelasSiswa.this, 1, false);
                            rcTugas.setLayoutManager(localLinearLayoutManager);
                            rcTugas.setNestedScrollingEnabled(false);
                            rcTugas.setAdapter(adapterLihatKelas);
                        } else {
                            sv_tugas.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        sv_tugas.setVisibility(View.GONE);
                        Toast.makeText(DetailKelasSiswa.this, "Error mengambil data tugas", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTextTabel(int width, String text, TableRow tableRow, int index, int color) {
        TextView textView = new TextView(this);
        if (index % 2 == 0) {
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        } else {
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
    private void setTextTabelGabungSamping(int width, String text, TableRow tableRow, int index, int color, int jumlahGabung) {
        TextView textView = new TextView(this);

        TableRow.LayoutParams params = new TableRow.LayoutParams(300
                , TableLayout.LayoutParams.WRAP_CONTENT);
        params.span = jumlahGabung;
        textView.setLayoutParams(params); // causes layout update

        if (index % 2 == 0) {
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        } else {
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

    private void getDataKelas() {
        String alphabetDesc = alphabet(dataKelasSiswa.getDescKelas());
        FirebaseDatabase.getInstance().getReference("kelas")
                .child(dataKelasSiswa.getUserNamePengajar())
                .child(dataKelasSiswa.getNamaKelas() + "_" + alphabetDesc)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ModelKelas dataKelas = dataSnapshot.getValue(ModelKelas.class);
                            if (dataKelas.getLihat()) {
                                previewTable(dataKelas);
//                                cekDataTugas(dataKelas);
                                getDataSiswa(dataKelas);
                            } else {
                                textJumlah.setText("0");
                                textPrivate.setVisibility(View.VISIBLE);
                            }
                            getDataTugas(dataKelas);
                        } else {
                            Toast.makeText(DetailKelasSiswa.this, "Mohon maaf, terjadi error Database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DetailKelasSiswa.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                    Toast.makeText(DetailKelasSiswa.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailKelasSiswa.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        dataKelasSiswa = null;
        dataKelas = null;
        MainActivity.menu = false;
        startActivity(new Intent(DetailKelasSiswa.this, MainActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void previewTable(ModelKelas dataKelas) {
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

    private void getDataSiswa(final ModelKelas dataKelas) {
        Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                .orderByChild("userNamePengajar")
                .equalTo(dataKelasSiswa.getUserNamePengajar());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<ModelKelasSiswa> listSiswa = new ArrayList<ModelKelasSiswa>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelKelasSiswa data = snapshot.getValue(ModelKelasSiswa.class);

                        if (dataKelasSiswa != null) {
                            if ((data.getNamaKelas().toString().equals(dataKelasSiswa.getNamaKelas()))
                                    && (data.getDescKelas().toString().equals(dataKelasSiswa.getDescKelas()))) {
                                listSiswa.add(data);
                            }
                        }
                    }

                    setTabelSiswa(listSiswa, dataKelas);
                    textJumlah.setText(Integer.toString(listSiswa.size()));
                } else {
                    textJumlah.setText("0");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                textJumlah.setText("0");
                Toast.makeText(DetailKelasSiswa.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTabelSiswa(ArrayList<ModelKelasSiswa> listSiswa, ModelKelas dataKelas) {
        int nomor = 1;
        for (int a = 0; a < listSiswa.size(); a++) {
            TableRow tableRow = new TableRow(this);
            tableRow.removeAllViews();
            tableRow.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));

            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            setTextTabel(75, Integer.toString(nomor), tableRow, 0, R.color.hitam);
            nomor++;

            int indexSamping = 1;
            for (int index = 0; index < dataKelas.getModelNilaiKelas().size(); index++) {
                if (dataKelas.getModelNilaiKelas().get(index).isNamaPelajar()) {
                    setTextTabel(500, listSiswa.get(a).getNama(), tableRow, indexSamping, R.color.hitam);
                    indexSamping++;
                } else if (dataKelas.getModelNilaiKelas().get(index).isKehadiran()) {
                    for (int pertemuan = 0; pertemuan < dataKelas.getModelNilaiKelas().get(index).getJumlah(); pertemuan++) {
                        getDataKehadiran(pertemuan, indexSamping, listSiswa, tableRow, listSiswa.get(a).getUsername(), dataKelas);
                        indexSamping++;
                    }
                } else {
                    for (int penilaian = 0; penilaian < dataKelas.getModelNilaiKelas().get(index).getJumlah(); penilaian++) {
                        getDataPenilaian(penilaian, indexSamping, listSiswa, tableRow, listSiswa.get(a).getUsername(), index
                                , dataKelas, dataKelas.getModelNilaiKelas().size(), index);
                        indexSamping++;
                    }
                }
            }

            int ind = 2;

            TextView textView = new TextView(this);
            if (ind % 2 == 0) {
                textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
            } else {
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
            if (ind % 2 == 0) {
                textAkhir.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
            } else {
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
            , final TableRow tableRow, final String username, final int index, final ModelKelas dataKelas
            , final int banyakJenisNilai, final int nilaiJenis) {
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

                        DetailKelasSiswa.this.dataKelas = dataKelas;
                        setTextPenilaian(tableRow, indexSamping, dataPenilaianSiswa, banyakJenisNilai, nilaiJenis);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(DetailKelasSiswa.this, "Gagal Mengambil Nilai", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setTextPenilaian(TableRow tableRow, int posisiTabel, ModelPenilaian listNilai
            , int banyakJenisNilai, int nilaiJenis) {
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

        if (nilaiJenis < banyakJenisNilai) {
            if (cekNilaiAkhir) {

            } else {
                getData();
            }
        }
    }

    private void getDataKehadiran(final int pertemuan, final int posisiTabel, final ArrayList<ModelKelasSiswa> listSiswa
            , final TableRow tableRow, final String usernameSiswa, final ModelKelas dataKelas) {
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
                        Toast.makeText(DetailKelasSiswa.this, "Gagal Mengambil Data Kehadiran", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void getData() {
        Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                .orderByChild("userNamePengajar")
                .equalTo(dataKelas.getUserNamePengajar());
        query.addListenerForSingleValueEvent(valueEventListener3);
    }

    ValueEventListener valueEventListener3 = new ValueEventListener() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final ArrayList<ModelKelasSiswa> listAkhirSiswa = new ArrayList<ModelKelasSiswa>();
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ModelKelasSiswa data = snapshot.getValue(ModelKelasSiswa.class);

                    if (dataKelas != null) {
                        if ((data.getNamaKelas().toString().equals(dataKelas.getNamaKelas()))
                                && (data.getDescKelas().toString().equals(dataKelas.getDescKelas()))) {
                            listAkhirSiswa.add(data);
                        }
                    }
                }

                getNilai(listAkhirSiswa);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(DetailKelasSiswa.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void getNilai(ArrayList<ModelKelasSiswa> listSiswa) {
        int indexSiswa = 0;
        do {
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
        } while (indexSiswa < listSiswa.size());
    }

    private void getNilaiKehadiran(final ArrayList<ModelKelasSiswa> listSiswa, final int siswa
            , final int jenisNilai, final int index, final int indexSamping) {
        FirebaseDatabase.getInstance().getReference("nilai")
                .child("kehadiran")
                .child(dataKelas.getUserNamePengajar())
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
                        Toast.makeText(DetailKelasSiswa.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getNilaiPenilaian(final ArrayList<ModelKelasSiswa> listSiswa, final int siswa, final int jenisNilai
            , final int index, final int indexSamping) {
        FirebaseDatabase.getInstance().getReference("nilai")
                .child(dataKelas.getModelNilaiKelas().get(jenisNilai).getNama())
                .child(dataKelas.getUserNamePengajar())
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
                        Toast.makeText(DetailKelasSiswa.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
        if (index % 2 == 0) {
            textView.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        } else {
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
        if (index % 2 == 0) {
            textHuruf.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
        } else {
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

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        CustomDialogLihatTugas.dataTugas = new ModelTugas(listTugas.get(position).getUsernamePengajar()
                , listTugas.get(position).getNamaKelas(), listTugas.get(position).getDescKelas()
                , listTugas.get(position).getNamaTugas(), listTugas.get(position).getDescTugas()
                , listTugas.get(position).getTanggalTugas(), listTugas.get(position).getWaktuTugas());
        DialogFragment newFragment = CustomDialogLihatTugas
                .newInstance();

        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void showTutorSequence(int paramInt) {
        ShowcaseConfig localShowcaseConfig = new ShowcaseConfig();
        localShowcaseConfig.setDelay(paramInt);
        MaterialShowcaseSequence localMaterialShowcaseSequence = new MaterialShowcaseSequence(DetailKelasSiswa.this, "Sequence Showcase");
        localMaterialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView paramAnonymousMaterialShowcaseView, int paramAnonymousInt) {
            }
        });
        localMaterialShowcaseSequence.setConfig(localShowcaseConfig);
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelasSiswa.this)
                .setTarget(tableLihatKelas).setDismissText("Selanjutnya")
                .setContentText("Informasi tabel penilaian kelas").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelasSiswa.this)
                .setTarget(sv_tugas).setDismissText("Selanjutnya")
                .setContentText("Informasi tugas di kelas ini").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(DetailKelasSiswa.this)
                .setTarget(sv_tugas).setDismissText("Baik")
                .setContentText("Untuk join ke Quiz, silahkan masukkan kode quiz pengajar kelas ini").withCircleShape().build());
        localMaterialShowcaseSequence.start();
    }
}
