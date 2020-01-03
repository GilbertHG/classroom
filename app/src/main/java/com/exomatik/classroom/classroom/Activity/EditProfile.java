package com.exomatik.classroom.classroom.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Featured.FileUtil;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.UserData;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.google.firebase.storage.FirebaseStorage.getInstance;

public class EditProfile extends AppCompatActivity {
    private ImageView btnBack;
    private EditText etNama, etPhone, etAlamat, etUmur;
    private RadioGroup rgJk;
    private CircleImageView imgFoto;
    private UserPreference userPreference;
    private RelativeLayout rlSimpan;
    private static int PICK_IMAGE = 100;
    private ProgressDialog progressDialog = null;
    private Uri imageUriCompress = null;
    private StorageReference mStorageRef;
    private View view;
    private File compressedImage, actualImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnBack = (ImageView) findViewById(R.id.back);
        imgFoto = (CircleImageView) findViewById(R.id.img_foto);
        rgJk = (RadioGroup) findViewById(R.id.rg_jk);
        etNama = (EditText) findViewById(R.id.text_nama_isi);
        etPhone = (EditText) findViewById(R.id.text_phone_isi);
        etAlamat = (EditText) findViewById(R.id.text_alamat_isi);
        etUmur = (EditText) findViewById(R.id.text_umur_isi);
        rlSimpan = (RelativeLayout) findViewById(R.id.rl_simpan);
        view = (View) findViewById(android.R.id.content);

        userPreference = new UserPreference(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        setData();

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto();
            }
        });

        rlSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, MainActivity.class));
                finish();
            }
        });
    }

    private void hapusFoto() {
        StorageReference fotoDelete = getInstance().getReferenceFromUrl(userPreference.getKEY_FOTO());
        fotoDelete.delete();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(EditProfile.this, MainActivity.class));
        finish();
    }

    private void setData() {
        if (userPreference.getKEY_NAME() != null) {
            etNama.setText(userPreference.getKEY_NAME());
        } else {
            etNama.setText("-");
        }
        if (userPreference.getKEY_ALAMAT() != null) {
            etAlamat.setText(userPreference.getKEY_ALAMAT());
        } else {
            etAlamat.setText("-");
        }
        if (userPreference.getKEY_PHONE() != null) {
            etPhone.setText(userPreference.getKEY_PHONE());
        } else {
            etPhone.setText("-");
        }
        if (userPreference.getKEY_UMUR() >= 0) {
            etUmur.setText(Integer.toString(userPreference.getKEY_UMUR()));
        } else {
            etUmur.setText("-");
        }
        if (userPreference.getKEY_JK() != null) {
            if (userPreference.getKEY_JK().toString().equals("Laki-laki")) {
                rgJk.check(R.id.rb_lk);
            } else {
                rgJk.check(R.id.rb_pr);
            }
        }
        if (userPreference.getKEY_FOTO() != null) {
            Uri localUri = Uri.parse(userPreference.getKEY_FOTO());
            Picasso.with(this).load(localUri).into(imgFoto);
        }
        else {
            imgFoto.setImageResource(R.drawable.ic_add_blue);
        }
    }

    public void uploadData() {
        progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setTitle("Proses");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.show();
        //memerika apakah user menggunakan foto baru
        if (imageUriCompress != null) {
            if (userPreference.getKEY_FOTO() != null){
                hapusFoto();
            }
            simpanFoto();
        }
        //user menggunakan foto lama
        else {
            simpanData(null);
        }
    }

    private void simpanFoto() {
        mStorageRef.child("storage/" + this.imageUriCompress.getLastPathSegment()).putFile(this.imageUriCompress).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            public void onSuccess(UploadTask.TaskSnapshot paramAnonymousTaskSnapshot) {
                simpanData(paramAnonymousTaskSnapshot);
            }
        }).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception paramAnonymousException) {
                progressDialog.dismiss();
                Toast.makeText(EditProfile.this, "error " + paramAnonymousException.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

    private void simpanData(UploadTask.TaskSnapshot uriUploaded) {
        String uri = null;
        if (uriUploaded == null) {
            uri = userPreference.getKEY_FOTO();
        } else {
            uri = uriUploaded.getDownloadUrl().toString();
        }
        final String nama = etNama.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        int umur = Integer.parseInt(etUmur.getText().toString());
        String jk = null;
        if (rgJk.getCheckedRadioButtonId() > 0){
            RadioButton rbButton = findViewById(rgJk.getCheckedRadioButtonId());
            jk = rbButton.getText().toString();
        }

        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
        UserData data = new UserData(nama, userPreference.getKEY_EMAIL(), userPreference.getKEY_USERNAME(),
                userPreference.getKEY_UID(), alamat, phone, jk, umur, uri);

        localDatabaseReference.child("users").child(userPreference.getKEY_USERNAME()).setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                if (paramAnonymous2Task.isSuccessful()) {
                    saveNama(userPreference.getKEY_USERNAME(), nama);
                    saveData(userPreference.getKEY_EMAIL());
                    return;
                }
                progressDialog.dismiss();
                Snackbar.make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG).show();
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

    private void saveNama(String key_username, final String nama) {
        Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                .orderByChild("username")
                .equalTo(key_username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelKelasSiswa dataKelas = snapshot.getValue(ModelKelasSiswa.class);

                        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
                        localDatabaseReference.child("kelas_siswa")
                                .child(dataKelas.getUserNamePengajar() + "_"
                                        + dataKelas.getNamaKelas() + "_"
                                        + alphabet(dataKelas.getDescKelas() + "_")
                                        + dataKelas.getUsername())
                                .child("nama")
                                .setValue(nama);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Gagal menyimpan nama di setiap kelas", Toast.LENGTH_SHORT).show();
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

                            progressDialog.dismiss();
                            startActivity(new Intent(EditProfile.this, SplashScreen.class));
                            finish();
                        }
                    }
                }
                else {
                    Toast.makeText(EditProfile.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void foto() {
        progressDialog = new ProgressDialog(EditProfile.this);
        progressDialog.setMessage("Mohon Tunggu...");
        progressDialog.setTitle("Proses");
        progressDialog.setCancelable(false);
        progressDialog.show();
        startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.INTERNAL_CONTENT_URI), PICK_IMAGE);
        progressDialog.dismiss();
    }

    public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        if ((paramInt2 == -1) && (paramInt1 == PICK_IMAGE)) {
            try {
                actualImage = FileUtil.from(this, paramIntent.getData());
                compressedImage = new Compressor(EditProfile.this).compressToFile(actualImage);
                imageUriCompress = Uri.fromFile(compressedImage);
                Picasso.with(EditProfile.this).load(imageUriCompress).into(imgFoto);
            } catch (IOException e) {
                Toast.makeText(EditProfile.this, "Error " +e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
