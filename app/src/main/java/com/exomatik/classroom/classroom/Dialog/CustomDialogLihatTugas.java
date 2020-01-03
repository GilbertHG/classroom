package com.exomatik.classroom.classroom.Dialog;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.DetailKelasSiswa;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelTugas;
import com.exomatik.classroom.classroom.Model.ModelUploadedTugas;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;

/**
 * Created by IrfanRZ on 03/11/2018.
 */

public class CustomDialogLihatTugas extends DialogFragment {
    public static ModelTugas dataTugas;
    private ImageView btnDismiss;
    private Button btnPilih;
    private RelativeLayout btnKirim;
    private TextView textNama, textDesc, textFile;
    private Uri pdfUri;
    private ProgressDialog progressDialog;
    private FirebaseStorage storage;
    private FirebaseDatabase database;
    private UserPreference userPreference;
    private ModelUploadedTugas tugas;

    public static CustomDialogLihatTugas newInstance() {
        return new CustomDialogLihatTugas();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_upload_tugas, container, false);

        btnDismiss = (ImageView) dialogView.findViewById(R.id.btn_close);
        btnPilih = (Button) dialogView.findViewById(R.id.btn_upload);
        btnKirim = (RelativeLayout) dialogView.findViewById(R.id.rl_upload);
        textNama = (TextView) dialogView.findViewById(R.id.text_nama);
        textDesc = (TextView) dialogView.findViewById(R.id.text_desc);
        textFile = (TextView) dialogView.findViewById(R.id.text_nama_file);

        storage = getInstance();
        database = FirebaseDatabase.getInstance();
        userPreference = new UserPreference(getActivity());
        cekDataTugas();

        textNama.setText(dataTugas.getNamaTugas());
        textDesc.setText(dataTugas.getDescTugas());

        btnPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectPdf();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 9);
                }
            }
        });

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (pdfUri != null) {
                    uploadFile();
                } else {
                    if (textFile.getText().toString().contains(tugas.getUrlFile())) {
                        Toast.makeText(getActivity(), "File baru anda sama dengan file anda sebelumnya", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "Pilih File .PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataTugas = null;
                cancel();
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

    private void cancel() {
        dismiss();
    }

    private void selectPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            textFile.setText("File yang dipilih : " + data.getData().getLastPathSegment());
        } else {
            Toast.makeText(getActivity(), "Please, select a file", Toast.LENGTH_SHORT).show();
        }
    }

    private void cekDataTugas() {
        Query query = FirebaseDatabase.getInstance().getReference("tugas_uploaded")
                .orderByChild("usernamePengajar")
                .equalTo(dataTugas.getUsernamePengajar());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelUploadedTugas data = snapshot.getValue(ModelUploadedTugas.class);

                        if (data.getKelas().equals(dataTugas.getNamaKelas())
                                && data.getDesc().equals(dataTugas.getDescKelas())
                                && data.getUsernameSiswa().equals(userPreference.getKEY_USERNAME())
                                && data.getNamaTugas().equals(dataTugas.getNamaTugas())) {
                            tugas = data;
                            btnPilih.setText("Upload File Baru (.PDF)");
                            textFile.setText(data.getUrlFile());
                        }
                    }
                } else {
                    tugas = null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                tugas = null;
                Toast.makeText(getActivity(), "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void uploadFile() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (tugas == null) {
            simpanFile();
        } else {
            if (textFile.getText().toString().contains(tugas.getUrlFile())) {
                Toast.makeText(getActivity(), "File baru anda sama dengan file anda sebelumnya", Toast.LENGTH_SHORT).show();
            } else {
                hapusData();
                hapusFile();
                simpanFile();
            }
        }
    }

    private void hapusFile() {
        StorageReference filePdfDelete = getInstance().getReferenceFromUrl(tugas.getUrlFile());
        filePdfDelete.delete();
    }

    private void hapusData() {
        DatabaseReference db_node = FirebaseDatabase.getInstance().getReference()
                .child("tugas_uploaded")
                .child(dataTugas.getUsernamePengajar()
                        + "_" + dataTugas.getNamaKelas()
                        + "_" + alphabet(dataTugas.getDescKelas())
                        + "_" + dataTugas.getNamaTugas()
                        + "_" + userPreference.getKEY_USERNAME());
        db_node.removeValue();
    }

    private void simpanFile() {
        final String file = System.currentTimeMillis() + "";
        final StorageReference storageReference = storage.getReference();
        storageReference.child("Uploads").child(file).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        String url = taskSnapshot.getDownloadUrl().toString();
                        simpanData(url);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "File Not Succesfully Uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                progressDialog.setProgress((int) progress);
                String progressText = taskSnapshot.getBytesTransferred() / 1024 + "KB/" + taskSnapshot.getTotalByteCount() / 1024 + "KB";
                progressDialog.setTitle(progressText);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void simpanData(String url) {
        DatabaseReference reference = database.getReference();

        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        ModelUploadedTugas dataUpload = new ModelUploadedTugas(dataTugas.getUsernamePengajar()
                , userPreference.getKEY_USERNAME(), dataTugas.getNamaKelas(), dataTugas.getDescKelas()
                , url, currentDateTimeString, dataTugas.getNamaTugas());

        reference.child("tugas_uploaded")
                .child(dataTugas.getUsernamePengajar()
                        + "_" + dataTugas.getNamaKelas() + "_" + alphabet(dataTugas.getDescKelas())
                        + "_" + dataTugas.getNamaTugas()
                        + "_" + userPreference.getKEY_USERNAME())
                .setValue(dataUpload)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dismiss();
                            Intent intent = new Intent(getActivity(), DetailKelasSiswa.class);
                            startActivity(intent);
                            getActivity().finish();
                            Toast.makeText(getActivity(), "Berhasil Kirim Tugas", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "File Not Succesfully Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}