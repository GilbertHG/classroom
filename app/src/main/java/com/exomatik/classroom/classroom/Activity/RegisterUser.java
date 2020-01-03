package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

/**
 * Created by IrfanRZ on 23/05/2019.
 */

public class RegisterUser extends AppCompatActivity {
    private EditText etNama, etUsename, etEmail, etPass;
    private TextView textLogin;
    private RelativeLayout rlDaftar;
    private CheckBox cbPass;
    private boolean exit = false;
    private ProgressDialog progressDialog = null;
    private View v;
    private UserPreference userPreference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cbPass = (CheckBox) findViewById(R.id.cb_pass);
        textLogin = (TextView) findViewById(R.id.text_login2);
        etNama = (EditText) findViewById(R.id.et_nama);
        etUsename = (EditText) findViewById(R.id.et_username);
        etPass = (EditText) findViewById(R.id.et_password);
        etEmail = (EditText) findViewById(R.id.et_email);
        rlDaftar = (RelativeLayout) findViewById(R.id.rl_btn_daftar);
        v = (View) findViewById(android.R.id.content);

        userPreference = new UserPreference(this);

        rlDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama = etNama.getText().toString();
                String username = etUsename.getText().toString();
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                if (nama.isEmpty() || pass.isEmpty() || username.isEmpty() || email.isEmpty()) {
                    Toast.makeText(RegisterUser.this, "Maaf, anda harus melengkapi data dengan benar", Toast.LENGTH_SHORT).show();
                    if (nama.isEmpty()) {
                        etNama.setError("Data tidak boleh kosong");
                    }
                    if (pass.isEmpty()) {
                        etPass.setError("Data tidak boleh kosong");
                    }
                    if (username.isEmpty()) {
                        etUsename.setError("Data tidak boleh kosong");
                    }
                    if (email.isEmpty()) {
                        etEmail.setError("Data tidak boleh kosong");
                    }
                } else {
                    progressDialog = new ProgressDialog(RegisterUser.this);
                    progressDialog.setMessage("Mohon Tunggu...");
                    progressDialog.setTitle("Proses");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    checkUsername(nama, username, email, pass);
                }
            }
        });

        textLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterUser.this, LoginUser.class));
                finish();
            }
        });


        cbPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean) {
                if (paramAnonymousBoolean) {
                    etPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    return;
                }
                etPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void checkUsername(final String nama, final String username, final String email, final String pass) {
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean cekNama = true;
                    Iterator localIterator = dataSnapshot.getChildren().iterator();
                    while (localIterator.hasNext()) {
                        UserData localDataUser = (UserData) ((DataSnapshot) localIterator.next()).getValue(UserData.class);
                        if (localDataUser.getUsername().toString().equalsIgnoreCase(username)) {
                            progressDialog.dismiss();
                            cekNama = false;
                            etUsename.setError("Mohon maaf username sudah terpakai");
                            Toast.makeText(RegisterUser.this, "Mohon maaf username sudah terpakai", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (cekNama) {
                        prosesDaftar(nama, username, email, pass);
                    }
                } else {
                    prosesDaftar(nama, username, email, pass);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
            return;
        } else {
            Toast toast = Toast.makeText(RegisterUser.this, "Tekan Cepat 2 Kali untuk Keluar", Toast.LENGTH_SHORT);
            toast.show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }

    private void prosesDaftar(final String nama, final String username, final String email, String pass) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    addUsertoFirebase(task.getResult().getUser(), RegisterUser.this, nama, email, username);
                } else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(v, "Mohon maaf, gagal menyimpan data", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                if(e.getMessage().toString().contains("email address is already in use")){
                    etEmail.setError("Email sudah terpakai, coba email yang lain");
                }
                Snackbar snackbar = Snackbar
                        .make(v, "Mohon maaf, " + e.getMessage().toString(), Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        });
    }

    private void addUsertoFirebase(FirebaseUser firebaseUser, Context context, final String nama,
                                   final String email, final String username) {
        final UserData userData = new UserData(nama, email, username, firebaseUser.getUid(), null, null, null, 0, null);
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        database.child("users")
                .child(username)
                .setValue(userData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            saveData(email);
                        } else {
                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar
                                    .make(v, "Mohon maaf, " + task.getException().getMessage().toString(), Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterUser.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData(final String email){
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator localIterator = dataSnapshot.getChildren().iterator();
                    while (localIterator.hasNext()) {
                        UserData localDataUser = (UserData) ((DataSnapshot) localIterator.next()).getValue(UserData.class);
                        if (localDataUser.getEmail().toString().equalsIgnoreCase(email)){
                            userPreference.setKEY_NAME(localDataUser.getNama());
                            userPreference.setKEY_USERNAME(localDataUser.getUsername());
                            userPreference.setKEY_EMAIL(localDataUser.getEmail());
                            userPreference.setKEY_UID(localDataUser.getUid());
                            userPreference.setKEY_ALAMAT(localDataUser.getAlamat());
                            userPreference.setKEY_PHONE(localDataUser.getHp());
                            userPreference.setKEY_JK(localDataUser.getJenis_kelamin());
                            userPreference.setKEY_UMUR(localDataUser.getAge());
                            userPreference.setKEY_FOTO(localDataUser.getFoto());

                            Toast.makeText(RegisterUser.this, "Berhasil Registrasi", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterUser.this, SplashScreen.class));
                            finish();
                        }
                    }
                }
                else {
                    Toast.makeText(RegisterUser.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterUser.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
