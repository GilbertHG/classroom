package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerBuatKelas;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelNilai;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class BuatKelas extends AppCompatActivity {
    public static ArrayList<ModelNilai> listBuatKelas = new ArrayList<ModelNilai>();
    public static RecyclerBuatKelas adapterBuatKelas = new RecyclerBuatKelas(listBuatKelas);
    public static CircleImageView btnAddNilai;
    private ImageView back;
    private RelativeLayout btnBuatKelas, btnPreview;
    private RecyclerView rcNilai;
    private EditText etNamaKelas, etDescKelas, etNama, etJumlah, etPersen;
    private TableLayout tableBuatKelas;
    private int totalPersentase = 0;
    private boolean cekNamaNilai = false;
    private ProgressDialog progressDialog = null;
    private UserPreference userPreference;
    private View view;
    private ImageView btnHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_kelas);

        back = (ImageView) findViewById(R.id.back);
        btnAddNilai = (CircleImageView) findViewById(R.id.img_add);
        btnBuatKelas = (RelativeLayout) findViewById(R.id.rl_buat);
        btnPreview = (RelativeLayout) findViewById(R.id.rl_preview);
        rcNilai = (RecyclerView) findViewById(R.id.rc_nilai);
        etNamaKelas = (EditText) findViewById(R.id.et_nama_kelas);
        etDescKelas = (EditText) findViewById(R.id.et_desc_kelas);
        etNama = ((EditText) findViewById(R.id.et_nama_nilai));
        etJumlah = ((EditText) findViewById(R.id.et_jumlah_nilai));
        etPersen = ((EditText) findViewById(R.id.et_persen_nilai));
        tableBuatKelas = (TableLayout) findViewById(R.id.tb_buat_kelas);
        view = (View) findViewById(android.R.id.content);
        btnHelp = (ImageView) findViewById(R.id.img_help);

        userPreference = new UserPreference(this);

        setNilaiPaket();
        setRecyclerView();

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialShowcaseView.resetSingleUse(BuatKelas.this, "Sequence Showcase");
                showTutorSequence(0);
            }
        });

        btnBuatKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buatKelas();
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                previewTable();
            }
        });

        btnAddNilai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNilai();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listBuatKelas.removeAll(listBuatKelas);
                startActivity(new Intent(BuatKelas.this, MainActivity.class));
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void previewTable() {
        tableBuatKelas.removeAllViews();
        for (int a = 0; a < 2; a++) {
            TableRow tableRow = new TableRow(this);
            tableRow.removeAllViews();
            tableRow.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));

            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            if (a == 0) {
                int totalNilai = listBuatKelas.size();
                // Add a TextView in the first column.
                TextView textNo = new TextView(this);
                textNo.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textNo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                textNo.setWidth(75);
                textNo.setGravity(1);
                textNo.setPadding(5, 5, 5, 5);

                textNo.setTextColor(getResources().getColor(R.color.hitam));
                textNo.setTextSize(14);

                textNo.setText("No .");
                tableRow.addView(textNo, 0);

                int number = 1;
                for (int index = 0; index < listBuatKelas.size(); index++) {
                    TextView textNama = new TextView(this);

                    TableRow.LayoutParams params = new TableRow.LayoutParams(300
                            , TableLayout.LayoutParams.WRAP_CONTENT);
                    params.span = listBuatKelas.get(index).getJumlah();
                    textNama.setLayoutParams(params); // causes layout update

                    textNama.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        textNama.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    }
                    textNama.setWidth(300);
                    textNama.setGravity(1);
                    textNama.setPadding(5, 5, 5, 5);
                    textNama.setTextColor(getResources().getColor(R.color.hitam));
                    textNama.setTextSize(14);

                    if (listBuatKelas.get(index).isNamaPelajar()) {
                        textNama.setText(listBuatKelas.get(index).getNama());
                    } else {
                        textNama.setText(listBuatKelas.get(index).getNama()
                                + "(" + Integer.toString(listBuatKelas.get(index).getPersentase()) + "%)");
                    }

                    tableRow.addView(textNama, number);
                    number++;
                }

                TextView textNilaiAkhir = new TextView(this);

                textNilaiAkhir.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textNilaiAkhir.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                textNilaiAkhir.setWidth(300);
                textNilaiAkhir.setGravity(1);
                textNilaiAkhir.setPadding(5, 5, 5, 5);
                textNilaiAkhir.setTextColor(getResources().getColor(R.color.hitam));
                textNilaiAkhir.setTextSize(14);

                textNilaiAkhir.setText("Nilai Akhir Angka");

                totalNilai++;
                tableRow.addView(textNilaiAkhir, totalNilai);

                TextView textHurufAkhir = new TextView(this);

                textHurufAkhir.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textHurufAkhir.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                textHurufAkhir.setWidth(300);
                textHurufAkhir.setGravity(1);
                textHurufAkhir.setPadding(5, 5, 5, 5);
                textHurufAkhir.setTextColor(getResources().getColor(R.color.hitam));
                textHurufAkhir.setTextSize(14);

                textHurufAkhir.setText("Nilai Akhir Huruf");

                totalNilai++;
                tableRow.addView(textHurufAkhir, totalNilai);

                tableBuatKelas.addView(tableRow);
            } else if (a == 1) {
                TextView textNo = new TextView(this);
                textNo.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                textNo.setWidth(50);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textNo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                textNo.setGravity(1);
                textNo.setPadding(5, 5, 5, 5);
                textNo.setTextColor(getResources().getColor(R.color.hitam));
                textNo.setTextSize(14);

                textNo.setText("");
                tableRow.addView(textNo, 0);
                int number = 1;
                int indexData = 0;
                boolean stop = true;

                while (indexData < listBuatKelas.size()) {
                    int looping = 0;
                    int textTampil = 1;
                    do {
                        if (listBuatKelas.get(indexData).getJumlah() == 1) {
                            TextView textJumlah = new TextView(this);
                            textJumlah.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                            textJumlah.setWidth(50);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                textJumlah.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            textJumlah.setGravity(1);
                            textJumlah.setPadding(5, 5, 5, 5);
                            textJumlah.setTextColor(getResources().getColor(R.color.hitam));
                            textJumlah.setTextSize(14);

                            textJumlah.setText("");
                            tableRow.addView(textJumlah, number);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= listBuatKelas.get(indexData).getJumlah()) {
                                stop = false;
                            }
                        } else if (listBuatKelas.get(indexData).getJumlah() == 2) {
                            TextView textJumlah = new TextView(this);
                            textJumlah.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                            textJumlah.setWidth(150);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                textJumlah.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            textJumlah.setGravity(1);
                            textJumlah.setPadding(5, 5, 5, 5);
                            textJumlah.setTextColor(getResources().getColor(R.color.hitam));
                            textJumlah.setTextSize(14);

                            textJumlah.setText(Integer.toString(textTampil));
                            tableRow.addView(textJumlah, number);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= listBuatKelas.get(indexData).getJumlah()) {
                                stop = false;
                            }
                        } else if (listBuatKelas.get(indexData).getJumlah() == 3) {
                            TextView textJumlah = new TextView(this);
                            textJumlah.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                            textJumlah.setWidth(100);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                textJumlah.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            textJumlah.setGravity(1);
                            textJumlah.setPadding(5, 5, 5, 5);
                            textJumlah.setTextColor(getResources().getColor(R.color.hitam));
                            textJumlah.setTextSize(14);

                            textJumlah.setText(Integer.toString(textTampil));
                            tableRow.addView(textJumlah, number);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= listBuatKelas.get(indexData).getJumlah()) {
                                stop = false;
                            }
                        } else {
                            TextView textJumlah = new TextView(this);
                            textJumlah.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                            textJumlah.setWidth(50);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                textJumlah.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                            }
                            textJumlah.setGravity(1);
                            textJumlah.setPadding(5, 5, 5, 5);
                            textJumlah.setTextColor(getResources().getColor(R.color.hitam));
                            textJumlah.setTextSize(14);

                            textJumlah.setText(Integer.toString(textTampil));
                            tableRow.addView(textJumlah, number);
                            number++;
                            looping++;
                            textTampil++;
                            if (looping >= listBuatKelas.get(indexData).getJumlah()) {
                                stop = false;
                            }
                        }
                    } while (stop);
                    stop = true;
                    indexData++;
                }
                int totalNilai = number;
                TextView textNilaiAkhir = new TextView(this);
                textNilaiAkhir.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textNilaiAkhir.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                textNilaiAkhir.setWidth(300);
                textNilaiAkhir.setGravity(1);
                textNilaiAkhir.setPadding(5, 5, 5, 5);
                textNilaiAkhir.setTextColor(getResources().getColor(R.color.hitam));
                textNilaiAkhir.setTextSize(14);
                textNilaiAkhir.setText("");

                tableRow.addView(textNilaiAkhir, totalNilai);
                totalNilai++;

                TextView textHurufAkhir = new TextView(this);
                textHurufAkhir.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam_putih));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    textHurufAkhir.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                textHurufAkhir.setWidth(300);
                textHurufAkhir.setGravity(1);
                textHurufAkhir.setPadding(5, 5, 5, 5);
                textHurufAkhir.setTextColor(getResources().getColor(R.color.hitam));
                textHurufAkhir.setTextSize(14);
                textHurufAkhir.setText("");

                tableRow.addView(textHurufAkhir, totalNilai);
                totalNilai++;

                tableBuatKelas.addView(tableRow);
            }
        }
    }

    private void buatKelas() {
        String namaKelas = etNamaKelas.getText().toString();
        String descKelas = etDescKelas.getText().toString();
        totalPersentase = 0;
        for (int a = 0; a < listBuatKelas.size(); a++) {
            totalPersentase = totalPersentase + listBuatKelas.get(a).getPersentase();
        }

        if (totalPersentase != 100 || namaKelas.isEmpty() || (descKelas.isEmpty())) {
            if (totalPersentase != 100) {
                Toast.makeText(this, "Persentase nilai tidak sampai 100 %", Toast.LENGTH_SHORT).show();
                etPersen.setError("Persentase nilai tidak sampai 100 %");
            }
            if (namaKelas.isEmpty()) {
                etNamaKelas.setError("Nama kelas tidak boleh kosong");
            }
            if (descKelas.isEmpty()) {
                etDescKelas.setError("Deskripsi tidak boleh kosong");
            }
        } else {
            cekNamaKelas(namaKelas, descKelas, listBuatKelas);
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

    public void uploadData(String namaKelas, String descKelas, ArrayList<ModelNilai> dataKelas) {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ModelKelas data = new ModelKelas(namaKelas, descKelas, userPreference.getKEY_USERNAME(), dataKelas, true);
        String alphabetDesc = alphabet(descKelas);

        localDatabaseReference.child("kelas").child(userPreference.getKEY_USERNAME())
                .child(namaKelas + "_" + alphabetDesc).setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            listBuatKelas.removeAll(listBuatKelas);
                            Toast.makeText(BuatKelas.this, "Berhasil membuat kelas", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(BuatKelas.this, MainActivity.class));
                            finish();
                            return;
                        }
                        progressDialog.dismiss();
                        Snackbar.make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void cekNamaKelas(final String namaKelas, final String descKelas, final ArrayList<ModelNilai> dataKelas) {
        progressDialog = new ProgressDialog(BuatKelas.this);
        progressDialog.setMessage("Mohon Tunggu...");
        progressDialog.setTitle("Proses");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseDatabase.getInstance().getReference("kelas").child(userPreference.getKEY_USERNAME())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean cekNama = true;
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelKelas localDataUser = (ModelKelas) ((DataSnapshot) localIterator.next()).getValue(ModelKelas.class);

                                if (localDataUser.getNamaKelas().equalsIgnoreCase(namaKelas)) {
                                    cekNama = false;
                                }
                            }
                        }

                        if (cekNama) {
                            uploadData(namaKelas, descKelas, dataKelas);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(BuatKelas.this, "Nama kelas sudah terpakai", Toast.LENGTH_SHORT).show();
                            etNamaKelas.setError("Nama kelas sudah terpakai");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(BuatKelas.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addNilai() {
        String nama = etNama.getText().toString();
        String jumlah = etJumlah.getText().toString();
        String persentase = etPersen.getText().toString();
        totalPersentase = 0;
        cekNamaNilai = false;

        for (int a = 0; a < listBuatKelas.size(); a++) {
            totalPersentase = totalPersentase + listBuatKelas.get(a).getPersentase();
            if (nama.equalsIgnoreCase(listBuatKelas.get(a).getNama())) {
                cekNamaNilai = true;
            }
        }
        if (!persentase.isEmpty()) {
            totalPersentase = totalPersentase + Integer.parseInt(etPersen.getText().toString());
        }

        if (nama.isEmpty() || jumlah.isEmpty() || persentase.isEmpty() || totalPersentase > 100 || cekNamaNilai) {
            if (nama.isEmpty()) {
                etNama.setError("Tidak boleh kosong");
            }
            if (jumlah.isEmpty()) {
                etJumlah.setError("Tidak boleh kosong");
            }
            if (persentase.isEmpty()) {
                etPersen.setError("Tidak boleh kosong");
            }
            if (totalPersentase > 100) {
                etPersen.setText("");
                etPersen.setError("Persentase nilai sudah 100 %");
            }
            if (cekNamaNilai) {
                etNama.setText("");
                etNama.setError("Nama nilai sudah terpakai");
            }
        } else {
            if (nama.contains("Nama") || nama.contains("nama")) {
                cekNilaiSebagaiNamaAtauKehadiran(true);
            } else if (nama.contains("Hadir") || nama.contains("hadir")) {
                cekNilaiSebagaiNamaAtauKehadiran(false);
            } else {
                listBuatKelas.add(new ModelNilai(etNama.getText().toString()
                        , Integer.parseInt(etJumlah.getText().toString())
                        , Integer.parseInt(etPersen.getText().toString()), false, false));
                adapterBuatKelas.notifyDataSetChanged();
                etNama.setText("");
                etJumlah.setText("");
                etPersen.setText("");
            }
        }
    }

    private void cekNilaiSebagaiNamaAtauKehadiran(boolean pilih) {
        AlertDialog.Builder alert = new AlertDialog.Builder(BuatKelas.this);

        if (pilih) {
            alert.setTitle("Nama");
            alert.setMessage("Apakah anda ingin menetapkan field ini sebagai nama?");
            alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    listBuatKelas.add(new ModelNilai(etNama.getText().toString()
                            , Integer.parseInt(etJumlah.getText().toString())
                            , Integer.parseInt(etPersen.getText().toString()), false, true));
                    adapterBuatKelas.notifyDataSetChanged();
                    etNama.setText("");
                    etJumlah.setText("");
                    etPersen.setText("");
                }
            });
        } else {
            alert.setTitle("Kehadiran");
            alert.setMessage("Apakah anda ingin menetapkan field ini sebagai kehadiran?");
            alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    listBuatKelas.add(new ModelNilai(etNama.getText().toString()
                            , Integer.parseInt(etJumlah.getText().toString())
                            , Integer.parseInt(etPersen.getText().toString()), true, false));
                    adapterBuatKelas.notifyDataSetChanged();
                    etNama.setText("");
                    etJumlah.setText("");
                    etPersen.setText("");
                }
            });
        }
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listBuatKelas.add(new ModelNilai(etNama.getText().toString()
                        , Integer.parseInt(etJumlah.getText().toString())
                        , Integer.parseInt(etPersen.getText().toString()), false, false));
                adapterBuatKelas.notifyDataSetChanged();
                etNama.setText("");
                etJumlah.setText("");
                etPersen.setText("");
            }
        });

        alert.show();
    }

    private void setNilaiPaket() {
        listBuatKelas.add(new ModelNilai("Nama", 1, 0, false, true));
        listBuatKelas.add(new ModelNilai("Kehadiran", 16, 20, true, false));
        listBuatKelas.add(new ModelNilai("Tugas", 2, 10, false, false));
        listBuatKelas.add(new ModelNilai("Mid", 1, 30, false, false));
        listBuatKelas.add(new ModelNilai("Final", 1, 40, false, false));
    }

    private void setRecyclerView() {
        LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(BuatKelas.this, 1, false);
        rcNilai.setLayoutManager(localLinearLayoutManager);
        rcNilai.setNestedScrollingEnabled(false);
        rcNilai.setAdapter(adapterBuatKelas);
    }

    @Override
    public void onBackPressed() {
        listBuatKelas.removeAll(listBuatKelas);
        startActivity(new Intent(BuatKelas.this, MainActivity.class));
        finish();
    }

    private void showTutorSequence(int paramInt) {
        ShowcaseConfig localShowcaseConfig = new ShowcaseConfig();
        localShowcaseConfig.setDelay(paramInt);
        MaterialShowcaseSequence localMaterialShowcaseSequence = new MaterialShowcaseSequence(BuatKelas.this, "Sequence Showcase");
        localMaterialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView paramAnonymousMaterialShowcaseView, int paramAnonymousInt) {
            }
        });
        localMaterialShowcaseSequence.setConfig(localShowcaseConfig);
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(BuatKelas.this)
                .setTarget(btnPreview).setDismissText("Selanjutnya")
                .setContentText("Untuk melihat tampilan header dari kelas").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(BuatKelas.this)
                .setTarget(etNama).setDismissText("Selanjutnya")
                .setContentText("Informasi tambahan untuk menambah nilai, anda harus melengkapi form ini").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(BuatKelas.this)
                .setTarget(btnAddNilai).setDismissText("Selanjutnya")
                .setContentText("Untuk menambah nilai").withCircleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(BuatKelas.this)
                .setTarget(btnBuatKelas).setDismissText("Selanjutnya")
                .setContentText("Untuk membuat kelas").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(BuatKelas.this)
                .setTarget(rcNilai).setDismissText("Selanjutnya")
                .setContentText("Ini adalah custom nilai yang anda buat, informasi tambahan bahwa total persentase nilai harus 100 %").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}
