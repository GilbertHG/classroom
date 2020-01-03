package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Adapter.RecyclerLihatKelas;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelAuthentikasi;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Iterator;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 1;
    private static int camId = 0;
    private ProgressDialog progressDialog = null, progressDialog2 = null;
    private ZXingScannerView scannerView;
    private View view;
    private UserPreference userPreference;

    private void showMessageOKCancel(String paramString, DialogInterface.OnClickListener paramOnClickListener) {
        new AlertDialog.Builder(this).setMessage(paramString).setPositiveButton("OK", paramOnClickListener).create().show();
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        view = findViewById(android.R.id.content);

        userPreference = new UserPreference(this);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
                scannerView.startCamera();
                scannerView.setResultHandler(ScanQR.this);
            } else {
                requestPermission();
            }
        } else {
            requestPermission();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.scannerView.stopCamera();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                        scannerView.setResultHandler(this);
                        scannerView.startCamera();
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        resumeScanner("Resume");
    }

    @Override
    public void handleResult(Result paramResult) {
        progressDialog2 = new ProgressDialog(ScanQR.this);
        progressDialog2.setMessage("Mohon Tunggu...");
        progressDialog2.setTitle("Proses");
        progressDialog2.setCancelable(false);
        progressDialog2.show();

        String[] result = paramResult.getText().toString().split("__");

        if (result.length == 3) {
            if (result[2].equalsIgnoreCase(userPreference.getKEY_USERNAME())) {
                Toast.makeText(this, "Anda adalah pengajar kelas ini", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ScanQR.this, MainActivity.class));
                finish();
            } else {
                cekQrCode(result[0], result[1], result[2]);
            }
        } else {
            resumeScanner("Code Invalid");
        }
        progressDialog2.dismiss();
    }

    private void cekQrCode(final String namaKelas, final String descKelas, final String userNamePengajar) {
        FirebaseDatabase.getInstance().getReference("kelas").child(userNamePengajar)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean notFound = false;
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelKelas localDataUser = (ModelKelas) ((DataSnapshot) localIterator.next()).getValue(ModelKelas.class);

                                if (localDataUser.getUserNamePengajar().equalsIgnoreCase(userNamePengajar) &&
                                        localDataUser.getNamaKelas().equalsIgnoreCase(namaKelas) &&
                                        localDataUser.getDescKelas().equalsIgnoreCase(descKelas)) {
                                    notFound = true;
                                    dialogSend(localDataUser);
                                }
                            }

                            if (notFound == false) {
                                resumeScanner("Mohon maaf, Kelas tidak ditemukan");
                            }
                        } else {
                            resumeScanner("Mohon maaf, Kelas tidak ditemukan");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        resumeScanner("Error, " + databaseError.getMessage().toString());
                    }
                });
    }

    private void dialogSend(final ModelKelas kelas) {
        final AlertDialog.Builder localBuilder = new AlertDialog.Builder(ScanQR.this);
        localBuilder.setTitle("Hasil Scan");
        localBuilder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                progressDialog = new ProgressDialog(ScanQR.this);
                progressDialog.setMessage("Mohon Tunggu...");
                progressDialog.setTitle("Proses");
                progressDialog.setCancelable(false);
                progressDialog.show();
                sendDataMasuk(kelas);
            }
        });
        localBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                resumeScanner("Batal Masuk Kelas");
            }
        });
        scannerView.setResultHandler(ScanQR.this);
        scannerView.startCamera();

        final String namaKelas = "Nama Kelas : " + kelas.getNamaKelas();
        final String desc = "Kelas : " + kelas.getDescKelas();

        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String pengajar = "Pengajar : ";
                if (dataSnapshot.exists()) {
                    Iterator localIterator = dataSnapshot.getChildren().iterator();
                    while (localIterator.hasNext()) {
                        UserData localDataUser = (UserData) ((DataSnapshot) localIterator.next()).getValue(UserData.class);
                        if (localDataUser.getUsername().toString().equalsIgnoreCase(kelas.getUserNamePengajar())) {
                            pengajar = "Pengajar : " + localDataUser.getNama();
                        }
                    }
                } else {
                    Toast.makeText(ScanQR.this, "Gagal Mengambil Data Pengajar", Toast.LENGTH_SHORT).show();
                }
                localBuilder.setMessage(namaKelas + "\n" + desc + "\n" + pengajar);
                localBuilder.create().show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ScanQR.this, "Gagal Mengambil Data Pengajar", Toast.LENGTH_SHORT).show();
            }
        });
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

    private void sendDataMasuk(ModelKelas kelas) {
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ModelAuthentikasi data = new ModelAuthentikasi(kelas, new UserData(userPreference.getKEY_NAME()
                , userPreference.getKEY_EMAIL(), userPreference.getKEY_USERNAME(), userPreference.getKEY_UID()
                , userPreference.getKEY_ALAMAT(), userPreference.getKEY_PHONE(), userPreference.getKEY_JK()
                , userPreference.getKEY_UMUR(), userPreference.getKEY_FOTO()));
        String alphabetDesc = alphabet(kelas.getDescKelas());

        localDatabaseReference.child("auth_masuk")
                .child(kelas.getUserNamePengajar())
                .child(kelas.getNamaKelas() + "_" + alphabetDesc)
                .child(userPreference.getKEY_USERNAME())
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(ScanQR.this, "Berhasil mengirim data, silahkan tunggu konfirmasi pengajar", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ScanQR.this, MainActivity.class));
                            finish();
                            return;
                        }
                        progressDialog.dismiss();
                        resumeScanner("Gagal upload data");
                    }
                });
    }

    private void resumeScanner(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
        } else {
            requestPermission();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ScanQR.this, MainActivity.class));
        finish();
    }
}