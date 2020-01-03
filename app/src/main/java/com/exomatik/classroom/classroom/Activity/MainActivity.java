package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Fragment.ContentProfile;
import com.exomatik.classroom.classroom.Fragment.ContentPengajar;
import com.exomatik.classroom.classroom.Fragment.ContentQuiz;
import com.exomatik.classroom.classroom.Fragment.ContentSiswa;
import com.exomatik.classroom.classroom.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static boolean menu = true;
    private DrawerLayout drawer;
    private CircleImageView imagePerson;
    private UserPreference userPreference;
    private TextView textWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar localToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(localToolbar);

        userPreference = new UserPreference(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView localNavigationView = (NavigationView) findViewById(R.id.nav_view);
        localNavigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, localToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (menu){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    , new ContentPengajar()).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                    , new ContentSiswa()).commit();
        }

        View localView = localNavigationView.getHeaderView(0);
        imagePerson = ((CircleImageView) localView.findViewById(R.id.image_person));
        textWelcome = (TextView) localView.findViewById(R.id.text_welcome);

        if (userPreference.getKEY_FOTO() != null){
            Uri localUri = Uri.parse(userPreference.getKEY_FOTO());
            Picasso.with(this).load(localUri).into(imagePerson);
        }
        else {
            imagePerson.setImageResource(R.drawable.ic_people_black);
        }
        if (userPreference.getKEY_NAME() != null){
            textWelcome.setText("Selamat Datang, " + userPreference.getKEY_NAME());
        }

        imagePerson.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {
                if (userPreference.getKEY_FOTO() != null){
                    startActivity(new Intent(MainActivity.this, LihatFoto.class));
                }
                else {
                    Toast.makeText(MainActivity.this, "Anda belum mengupload gambar profil", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_kelas_ajar:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ContentPengajar()).commit();
                break;
            case R.id.nav_kelas_mahasiswa:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ContentSiswa()).commit();
                break;
            case R.id.nav_quiz:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ContentQuiz()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container
                        , new ContentProfile()).commit();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
