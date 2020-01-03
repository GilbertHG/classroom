package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class LoginUser extends AppCompatActivity {
    private TextView btnDaftar;
    private boolean exit = false;
    private EditText etEmail, etPass;
    private RelativeLayout btnLogin;
    private CheckBox cbPass;
    private ProgressDialog progressDialog = null;
    private View v;
    private UserPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnDaftar = (TextView) findViewById(R.id.text_daftar2);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPass = (EditText) findViewById(R.id.et_password);
        btnLogin = (RelativeLayout) findViewById(R.id.rl_login);
        cbPass = (CheckBox) findViewById(R.id.cb_pass);
        v = (View) findViewById(android.R.id.content);

        userPreference = new UserPreference(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String pass = etPass.getText().toString();

                if (email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(LoginUser.this, "Mohon maaf, anda harus melengkapi data", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog = new ProgressDialog(LoginUser.this);
                    progressDialog.setMessage("Mohon Tunggu...");
                    progressDialog.setTitle("Proses");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    prosesLogin(email, pass);
                }
            }
        });

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginUser.this, RegisterUser.class));
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

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
            return;
        } else {
            Toast toast = Toast.makeText(LoginUser.this, "Tekan Cepat 2 Kali untuk Keluar", Toast.LENGTH_SHORT);
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

    private void prosesLogin(final String email, String pass) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    saveData(email);
                }else {
                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(v, "Mohon maaf, " + task.getException().getMessage().toString() , Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                if (e.getMessage().toString().contains("There is no user record")){
                    etEmail.setError("Email belum terdaftar");
                }
                Snackbar snackbar = Snackbar
                        .make(v, "Mohon maaf, " + e.getMessage().toString() , Snackbar.LENGTH_LONG);
                snackbar.show();
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

                            Toast.makeText(LoginUser.this, "Berhasil Login", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginUser.this, SplashScreen.class));
                            finish();
                        }
                    }
                }
                else {
                    Toast.makeText(LoginUser.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoginUser.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
