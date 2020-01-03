package com.exomatik.classroom.classroom.Dialog;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.BuatTugas;
import com.exomatik.classroom.classroom.Activity.DetailKelas;
import com.exomatik.classroom.classroom.Activity.DetailKelasSiswa;
import com.exomatik.classroom.classroom.Activity.MainActivity;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.ModelTugas;
import com.exomatik.classroom.classroom.Model.ModelUploadedTugas;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by IrfanRZ on 03/11/2018.
 */

public class CustomDialogBuatTugas extends DialogFragment {
    public static ModelTugas tugas;
    public static TableLayout tableLayout;
    public static ModelKelas dataKelas;
    private Button btnHapus, buttonTambah;
    private EditText etNama, etDesc, etTanggal, etWaktu;
    private String path;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;
    private TimePickerDialog timePickerDialog;
    private TextView textTitle;
    private ImageView btnDismiss;
    private ArrayList<ModelUploadedTugas> listTugas = new ArrayList<ModelUploadedTugas>();

    public static CustomDialogBuatTugas newInstance() {
        return new CustomDialogBuatTugas();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_tugas, container, false);

        btnDismiss = (ImageView) dialogView.findViewById(R.id.img_dismiss);
        textTitle = (TextView) dialogView.findViewById(R.id.text_title);
        btnHapus = (Button) dialogView.findViewById(R.id.dialog_hapus);
        buttonTambah = (Button) dialogView.findViewById(R.id.dialog_tambah);
        etNama = (EditText) dialogView.findViewById(R.id.et_tambah);
        etDesc = (EditText) dialogView.findViewById(R.id.et_desc);
        etTanggal = (EditText) dialogView.findViewById(R.id.et_tanggal);
        etWaktu = (EditText) dialogView.findViewById(R.id.et_time);

        dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        if (tugas != null) {
            textTitle.setText("Edit Tugas");
            etNama.setText(tugas.getNamaTugas());
            etDesc.setText(tugas.getDescTugas());
            etWaktu.setText(tugas.getWaktuTugas());
            etTanggal.setText(tugas.getTanggalTugas());
        }

        buttonTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahTugas();
            }
        });

        etTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        etWaktu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog();
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertHapus(tugas.getNamaTugas());
            }
        });

        return dialogView;
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

    private void tambahTugas() {
        String namaFile = etNama.getText().toString();
        String tanggal = etTanggal.getText().toString();
        String waktu = etWaktu.getText().toString();
        String desc = etDesc.getText().toString();

        if (namaFile.isEmpty() || tanggal.isEmpty() || waktu.isEmpty()) {
            if (namaFile.isEmpty())
                etNama.setError("Tidak boleh kosong");
            if (tanggal.isEmpty())
                etTanggal.setError("Tidak boleh kosong");
            if (waktu.isEmpty())
                etWaktu.setError("Tidak boleh kosong");
        } else {
            if (tugas == null) {
                cekNamaTugas(namaFile, desc, tanggal, waktu);
            } else {
                getUploadedTugas(true);
                hapusDataTugas(tugas.getNamaTugas());
                simpanTugas(namaFile, desc, tanggal, waktu);
            }
        }
    }

    private void alertHapus(final String namaTugas) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());

        alert.setTitle("Hapus");
        alert.setMessage("Apakah anda yakin ingin menghapus tugas ini?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                hapusDataTugas(namaTugas);
                getUploadedTugas(false);
                dialog.dismiss();

                startActivity(new Intent(getActivity(), BuatTugas.class));
                getActivity().finish();
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

    private void getUploadedTugas(final boolean action) {
        Query query = FirebaseDatabase.getInstance().getReference("tugas_uploaded")
                .orderByChild("usernamePengajar")
                .equalTo(dataKelas.getUserNamePengajar());
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listTugas.removeAll(listTugas);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelUploadedTugas data = snapshot.getValue(ModelUploadedTugas.class);

                        if ((data.getNamaTugas().equals(tugas.getNamaTugas()))
                                && (data.getKelas().equals(dataKelas.getNamaKelas()))
                                && (data.getDesc().equals(dataKelas.getDescKelas()))
                                ) {
                            listTugas.add(data);
                        }
                    }

                    if (listTugas.size() != 0) {
                        if (action){
                            hapusUploadedTugas();
                            editUploadedTugas();
                        }
                        else {
                            hapusUploadedTugas();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editUploadedTugas() {
        Log.e("Size", Integer.toString(listTugas.size()));
        for (int a = 0; a < listTugas.size(); a++) {
            DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

            ModelUploadedTugas dataTugas = new ModelUploadedTugas(listTugas.get(a).getUsernamePengajar()
                    , listTugas.get(a).getUsernameSiswa(), listTugas.get(a).getKelas()
                    , listTugas.get(a).getDesc(), listTugas.get(a).getUrlFile()
                    , listTugas.get(a).getDateANDtime(), etNama.getText().toString());

            localDatabaseReference.child("tugas_uploaded")
                    .child(dataKelas.getUserNamePengajar()
                            + "_" + dataKelas.getNamaKelas()
                            + "_" + alphabet(dataKelas.getDescKelas())
                            + "_" + etNama.getText().toString()
                            + "_" + listTugas.get(a).getUsernameSiswa())
                    .setValue(dataTugas);
        }
    }

    private void hapusUploadedTugas() {
        for (int a = 0; a < listTugas.size(); a++) {
            DatabaseReference db_remove = FirebaseDatabase.getInstance().getReference().child("tugas_uploaded")
                    .child(dataKelas.getUserNamePengajar()
                            + "_" + dataKelas.getNamaKelas()
                            + "_" + alphabet(dataKelas.getDescKelas())
                            + "_" + listTugas.get(a).getNamaTugas()
                            + "_" + listTugas.get(a).getUsernameSiswa());
            db_remove.removeValue();
        }
    }

    private void hapusDataTugas(String namaTugas) {
        DatabaseReference db_remove = FirebaseDatabase.getInstance().getReference().child("tugas")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child(namaTugas);
        db_remove.removeValue();
    }

    private void cekNamaTugas(final String namaFile, final String desc, final String tanggal, final String waktu) {
        FirebaseDatabase.getInstance().getReference("tugas")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child(namaFile)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getActivity(), "Nama tugas sudah ada", Toast.LENGTH_SHORT).show();
                        } else {
                            simpanTugas(namaFile, desc, tanggal, waktu);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void simpanTugas(String namaFile, String desc, String tanggal, String waktu) {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ModelTugas dataTugas = new ModelTugas(dataKelas.getUserNamePengajar(), dataKelas.getNamaKelas()
                , dataKelas.getDescKelas(), namaFile, desc, tanggal, waktu);
        localDatabaseReference.child("tugas")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas() + "_" + alphabet(dataKelas.getDescKelas()))
                .child(namaFile)
                .setValue(dataTugas)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        BuatTugas.dataKelas = dataKelas;
                        tugas = null;
                        dataKelas = null;
                        startActivity(new Intent(getActivity(), BuatTugas.class));
                        getActivity().finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error, " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancel() {
        BuatTugas.dataKelas = dataKelas;
        tableLayout = null;
        dataKelas = null;
        tugas = null;
        dismiss();
    }

    private void showDateDialog() {
        Calendar localCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker paramAnonymousDatePicker, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {
                Calendar localCalendar = Calendar.getInstance();
                localCalendar.set(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
                etTanggal.setError(null);
                etTanggal.setText(dateFormatter.format(localCalendar.getTime()));
            }
        }, localCalendar.get(Calendar.YEAR)
                , localCalendar.get(Calendar.MONTH)
                , localCalendar.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void showTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                etWaktu.setError(null);
                etWaktu.setText(hourOfDay + ":" + minute);
            }
        },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity()));

        timePickerDialog.show();
    }
}