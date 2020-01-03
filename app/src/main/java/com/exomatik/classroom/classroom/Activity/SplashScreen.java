package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jpardogo.android.googleprogressbar.library.FoldingCirclesDrawable;
import com.jpardogo.android.googleprogressbar.library.GoogleProgressBar;

import io.fabric.sdk.android.Fabric;
import java.util.Iterator;

public class SplashScreen extends AppCompatActivity {
    private GoogleProgressBar googleProgressBar;
    private TextView textMaintenance;
    private boolean back = false;
    private UserPreference userPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash_screen);

        googleProgressBar = (GoogleProgressBar) findViewById(R.id.google_progress);
        textMaintenance = (TextView) findViewById(R.id.text_maintenance);
        userPreference = new UserPreference(this);

        try{
            googleProgressBar.setIndeterminateDrawable(new FoldingCirclesDrawable.Builder(this).colors(getResources().getIntArray(R.array.progressLoader)).build());
        }
        catch (IndexOutOfBoundsException e)
        {
            e.printStackTrace();
            Log.d("Google Progress Bar", "onCreate() returned: " + e );
        }

        getDataMaintenance();
    }

    private void getDataMaintenance() {
        FirebaseDatabase.getInstance().getReference("maintenance").addListenerForSingleValueEvent(this.valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            Toast.makeText(SplashScreen.this, paramAnonymousDatabaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }

        public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
            if (paramAnonymousDataSnapshot.exists()) {
                Iterator localIterator = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator.hasNext()) {
                    String localDataString = (String) ((DataSnapshot) localIterator.next()).getValue(String.class);

                    if (localDataString.equals("aktif")){
                        appsActive();
                    }
                    else if (localDataString.equals("maintenance")){
                        textMaintenance.setVisibility(View.VISIBLE);
                        textMaintenance.setText("Mohon maaf, aplikasi sedang maintenance");
                        googleProgressBar.setVisibility(View.GONE);
                        back = true;
                    }
                    else if (localDataString.equals("service")){
                        textMaintenance.setVisibility(View.VISIBLE);
                        textMaintenance.setText("Mohon maaf, aplikasi akan maintenance segera");

                        appsActive();
                    }
                }
            }
        }
    };

    private void appsActive(){
        new Handler().postDelayed(new Runnable()
        {
            public void run()
            {
                if (FirebaseAuth.getInstance().getCurrentUser() != null){
                    saveData(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                }
                else {
                    Intent homeIntent = new Intent(SplashScreen.this, LoginUser.class);
                    startActivity(homeIntent);
                    finish();
                }
            }
        }, 2000L);
    }

    @Override
    public void onBackPressed() {
        if (back){
            finish();
        }
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
                            MainActivity.menu = true;
                            Intent homeIntent = new Intent(SplashScreen.this, MainActivity.class);
                            startActivity(homeIntent);
                            finish();
                        }
                    }
                }
                else {
                    Toast.makeText(SplashScreen.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SplashScreen.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });
    }
}