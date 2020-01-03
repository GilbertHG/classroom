package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelUploadedTugas;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class DetailTugasUploaded extends AppCompatActivity implements DownloadFile.Listener {
    public static ModelUploadedTugas dataTugas;
    private LinearLayout root;
    private RemotePDFViewPager remotePDFViewPager;
    private TextView textTitle;
    private ImageView btnBack, btnEdit, btnDownload;
    private PDFPagerAdapter adapter;
    private RelativeLayout rlCustoomToolbar;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_uploaded_tugas);

        root = (LinearLayout) findViewById(R.id.remote_pdf_root);
        btnBack = (ImageView) findViewById(R.id.back);
        btnEdit = (ImageView) findViewById(R.id.img_edit);
        btnDownload = (ImageView) findViewById(R.id.img_download);
        textTitle = (TextView) findViewById(R.id.text_title_bar);
        rlCustoomToolbar = (RelativeLayout) findViewById(R.id.customToolbar);

        progressDialog = new ProgressDialog(DetailTugasUploaded.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Downloading");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.show();

        getDataUser(textTitle);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataTugas = null;
                startActivity(new Intent(DetailTugasUploaded.this, BuatTugas.class));
                finish();
            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(dataTugas.getUrlFile()));
                startActivity(intent);
            }
        });

        setPdf();
    }

    private void getDataUser(final TextView textNama) {
        Query query = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("username")
                .equalTo(dataTugas.getUsernameSiswa());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserData data = snapshot.getValue(UserData.class);
                        textNama.setText(data.getNama());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailTugasUploaded.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.close();
        }
    }

    protected void setPdf() {
        final Context ctx = this;
        final DownloadFile.Listener listener = this;
        remotePDFViewPager = new RemotePDFViewPager(ctx, dataTugas.getUrlFile(), listener);
    }

    public void updateLayout() {
        root.removeAllViewsInLayout();
        root.addView(rlCustoomToolbar,
                LinearLayout.LayoutParams.MATCH_PARENT, 150);
        root.addView(remotePDFViewPager,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        progressDialog.dismiss();
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        updateLayout();
    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Gagal Membuka File", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {
        double proses = (100.0 * progress)/total;
        progressDialog.setProgress((int) proses);
        String progressText = progress/1024+"KB/"+total/1024+"KB";
        progressDialog.setTitle(progressText);
    }

    @Override
    public void onBackPressed() {
        dataTugas = null;
        startActivity(new Intent(DetailTugasUploaded.this, BuatTugas.class));
        finish();
    }
}
