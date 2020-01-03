package com.exomatik.classroom.classroom.Activity;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Featured.SaveImageHelper;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.R;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import dmax.dialog.SpotsDialog;

public class LihatFoto extends AppCompatActivity {
    public static String urlFoto, nama;
    private ImageView back, btnDownload;
    private PhotoView imgFoto;
    private TextView textTitle;
    private UserPreference userPreference;
    private static final int PERMISSION_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_foto);

        back = (ImageView) findViewById(R.id.back);
        btnDownload = (ImageView) findViewById(R.id.img_download);
        imgFoto = (PhotoView) findViewById(R.id.img_foto);
        textTitle = (TextView) findViewById(R.id.text_title);

        userPreference = new UserPreference(this);

        if (urlFoto == null){
            Uri localUri = Uri.parse(userPreference.getKEY_FOTO());
            Picasso.with(this).load(localUri).into(imgFoto);
            textTitle.setText(userPreference.getKEY_NAME());
        }
        else {
            Uri localUri = Uri.parse(urlFoto);
            Picasso.with(this).load(localUri).into(imgFoto);
            textTitle.setText(nama);
            btnDownload.setVisibility(View.GONE);
        }

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(LihatFoto.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(LihatFoto.this, "You should grant permission", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSION_REQUEST_CODE);
                    return;
                }
                else {
                    AlertDialog dialog = new SpotsDialog(LihatFoto.this);
                    dialog.show();
                    dialog.setMessage("Downloading...");
                    String fileName = userPreference.getKEY_NAME() + ".jpg";
                    Picasso.with(getBaseContext()).load(userPreference.getKEY_FOTO())
                            .into(new SaveImageHelper(getBaseContext(),
                                    dialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "Image Description"));
                    Toast.makeText(LihatFoto.this, "Foto sudah di simpan di folder Pictures ", Toast.LENGTH_LONG).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlFoto = null;
                nama = null;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        urlFoto = null;
        nama = null;
        finish();
    }
}
