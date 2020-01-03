package com.exomatik.classroom.classroom.Dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.InviteSiswaQuiz;
import com.exomatik.classroom.classroom.Activity.MulaiQuizPengajar;
import com.exomatik.classroom.classroom.Activity.MulaiQuizSiswa;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatKelas;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IrfanRZ on 03/11/2018.
 */

public class CustomDialogMasukQuiz extends DialogFragment {
    public static ModelKelas dataKelas;
    private Button buttonTambah;
    private EditText etNama;
    private ImageView btnDismiss;
    private UserPreference userPreference;
    private ProgressDialog progressDialog;
    private View view;

    public static CustomDialogMasukQuiz newInstance() {
        return new CustomDialogMasukQuiz();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_quiz, container, false);

        btnDismiss = (ImageView) dialogView.findViewById(R.id.img_dismiss);
        buttonTambah = (Button) dialogView.findViewById(R.id.dialog_tambah);
        etNama = (EditText) dialogView.findViewById(R.id.et_tambah);
        view = (View) dialogView.findViewById(android.R.id.content);

        userPreference = new UserPreference(getActivity());

        buttonTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namaFile = etNama.getText().toString();

                if (namaFile.isEmpty()){
                    etNama.setError("Tidak boleh kosong");
                }
                else {
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setMessage("Mohon Tunggu...");
                    progressDialog.setTitle("Proses");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    cekKode(namaFile);
                }
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKelas = null;
                dismiss();
            }
        });

        return dialogView;
    }

    private void cekKode(String kodeQuiz) {
        if (dataKelas != null){
            FirebaseDatabase.getInstance()
                    .getReference("quiz_invitation")
                    .child(dataKelas.getUserNamePengajar())
                    .child(dataKelas.getNamaKelas())
                    .child(kodeQuiz)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean masuk = false;
                            ModelQuizInvite invite = null;

                            if (dataSnapshot.exists()) {
                                Iterator localIterator = dataSnapshot.getChildren().iterator();

                                while (localIterator.hasNext()) {
                                    ModelQuizInvite data = (ModelQuizInvite) ((DataSnapshot) localIterator.next()).getValue(ModelQuizInvite.class);

                                    if (data.getUsername().equals(userPreference.getKEY_USERNAME())){
                                        if (data.isIkutQuiz()){
                                            invite = data;
                                            masuk = true;
                                        }
                                    }
                                }
                            }
                            else {
                                dismiss();
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Quiz tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                            if (masuk){
                                sendData(invite);
                            }
                            else {
                                progressDialog.dismiss();
                                dismiss();
                                Toast.makeText(getActivity(), "Quiz tidak ditemukan atau Anda tidak diperbolehkan masuk quiz ", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            dismiss();
                            Toast.makeText(getActivity(), "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            Toast.makeText(getActivity(), "Error getting data kelas", Toast.LENGTH_SHORT).show();
            dismiss();
            progressDialog.dismiss();
        }
    }

    private void sendData(final ModelQuizInvite data) {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();

        localDatabaseReference
                .child("quiz_accept")
                .child(dataKelas.getUserNamePengajar())
                .child(dataKelas.getNamaKelas())
                .child(data.getKodeQuiz())
                .child(userPreference.getKEY_USERNAME())
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Berhasil masuk quiz", Toast.LENGTH_SHORT).show();

                            progressDialog.dismiss();
                            dismiss();
                            MulaiQuizSiswa.dataKelas = dataKelas;
                            MulaiQuizSiswa.dataQuiz = data;
                            startActivity(new Intent(getActivity(), MulaiQuizSiswa.class));
                            getActivity().finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Snackbar.make(view, "Error tidak diketahui", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                dismiss();
                Toast.makeText(getActivity(), "Error, " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}