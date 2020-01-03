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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.BuatQuiz;
import com.exomatik.classroom.classroom.Activity.BuatTugas;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelSoal;
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

public class CustomDialogBuatSoalQuiz extends DialogFragment {
    private ImageView btnDismiss;
    private Button btnTambahSoal;
    private EditText etA, etB, etC, etD, etSoal, etWaktu;
    private RadioGroup rgJawaban;
    private View view;

    public static CustomDialogBuatSoalQuiz newInstance() {
        return new CustomDialogBuatSoalQuiz();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.dialog_buat_soal, container, false);

        btnDismiss = (ImageView) dialogView.findViewById(R.id.img_dismiss);
        btnTambahSoal = (Button) dialogView.findViewById(R.id.dialog_tambah);
        etSoal = (EditText) dialogView.findViewById(R.id.et_soal);
        etA = (EditText) dialogView.findViewById(R.id.et_soal);
        etB = (EditText) dialogView.findViewById(R.id.et_b);
        etC = (EditText) dialogView.findViewById(R.id.et_c);
        etD = (EditText) dialogView.findViewById(R.id.et_d);
        etWaktu = (EditText) dialogView.findViewById(R.id.et_waktu);
        rgJawaban = (RadioGroup) dialogView.findViewById(R.id.rg_soal);
        this.view = dialogView;

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        btnTambahSoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String soal = etSoal.getText().toString();
                String a = etA.getText().toString();
                String b = etB.getText().toString();
                String c = etC.getText().toString();
                String d = etD.getText().toString();
                String waktu = etWaktu.getText().toString();
                int rgJawab = rgJawaban.getCheckedRadioButtonId();

                if (soal.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty() || rgJawab == -1 || waktu.isEmpty()){
                    if (soal.isEmpty()){
                        etSoal.setError("Soal tidak boleh kosong");
                    }
                    if (a.isEmpty()){
                        etA.setError("Jawaban tidak boleh kosong");
                    }
                    if (b.isEmpty()){
                        etB.setError("Jawaban tidak boleh kosong");
                    }
                    if (c.isEmpty()){
                        etC.setError("Jawaban tidak boleh kosong");
                    }
                    if (d.isEmpty()){
                        etD.setError("Jawaban tidak boleh kosong");
                    }
                    if (rgJawab == -1){
                        Toast.makeText(getActivity(), "Anda harus mencentang jawaban yang benar", Toast.LENGTH_SHORT).show();
                    }
                    if (waktu.isEmpty()){
                        etWaktu.setError("Waktu tidak boleh kosong");
                    }
                }
                else {
                    tambahSoal(soal, a, b, c, d, rgJawab, waktu);
                }
            }
        });

        return dialogView;
    }

    private void tambahSoal(String soal, String a, String b, String c, String d, int rgJawab, String waktu) {
        int jawaban = 0;
        if (rgJawab == R.id.rb_jawaban_a){
            jawaban = 1;
        }
        else if (rgJawab == R.id.rb_jawaban_b){
            jawaban = 2;
        }
        else if (rgJawab == R.id.rb_jawaban_c){
            jawaban = 3;
        }
        else if (rgJawab == R.id.rb_jawaban_d){
            jawaban = 4;
        }

        ModelSoal data = new ModelSoal(soal, a, b, c, d, jawaban,Integer.parseInt(waktu));
        BuatQuiz.listSoal.add(data);
        getActivity().startActivity(new Intent(getActivity(), BuatQuiz.class));
        getActivity().finish();
    }

    private void cancel() {
        dismiss();
    }
}