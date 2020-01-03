package com.exomatik.classroom.classroom.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailProfile extends AppCompatActivity {
    public static String usernameSiswa;
    private ImageView back;
    private TextView textNama, textUsername, textEmail, textPhone, textAlamat, textUmur, textJk;
    private CircleImageView imgFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_profile);

        back = (ImageView) findViewById(R.id.back);
        imgFoto = (CircleImageView) findViewById(R.id.img_foto);
        textNama = (TextView) findViewById(R.id.text_nama_isi);
        textUsername = (TextView) findViewById(R.id.text_username_isi);
        textEmail = (TextView) findViewById(R.id.text_email_isi);
        textPhone = (TextView) findViewById(R.id.text_phone_isi);
        textAlamat = (TextView) findViewById(R.id.text_alamat_isi);
        textUmur = (TextView) findViewById(R.id.text_umur_isi);
        textJk = (TextView) findViewById(R.id.text_jk_isi);

        getDataUser();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameSiswa = null;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        usernameSiswa = null;
        finish();
    }

    private void getDataUser() {
        Query query = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("username")
                .equalTo(usernameSiswa);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserData data = snapshot.getValue(UserData.class);

                    setData(data);
                }
            } else {
                Toast.makeText(DetailProfile.this, "Mohon maaf, terjadi error database", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(DetailProfile.this, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    private void setData(final UserData data){
        if (data.getNama() != null){
            textNama.setText(data.getNama());
        }
        else {
            textNama.setText("-");
        }
        if (data.getEmail() != null){
            textEmail.setText(data.getEmail());
        }
        else {
            textEmail.setText("-");
        }
        if (data.getUsername() != null){
            textUsername.setText(data.getUsername());
        }
        else {
            textUsername.setText("-");
        }
        if (data.getAlamat() != null){
            textAlamat.setText(data.getAlamat());
        }
        else {
            textAlamat.setText("-");
        }
        if (data.getHp() != null){
            textPhone.setText(data.getHp());
        }
        else {
            textPhone.setText("-");
        }
        if (data.getAge() >= 0){
            textUmur.setText(Integer.toString(data.getAge()));
        }
        else {
            textUmur.setText("-");
        }
        if (data.getJenis_kelamin() != null){
            textJk.setText(data.getJenis_kelamin());
        }
        else {
            textJk.setText("-");
        }
        if (data.getFoto() != null){
            Uri localUri = Uri.parse(data.getFoto());
            Picasso.with(DetailProfile.this).load(localUri).into(imgFoto);
        }else {
            imgFoto.setImageResource(R.drawable.ic_people_blue);
        }

        imgFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.getFoto() != null){
                    LihatFoto.urlFoto = data.getFoto();
                    LihatFoto.nama = data.getNama();
                    startActivity(new Intent(DetailProfile.this, LihatFoto.class));
                }
                else {
                    Toast.makeText(DetailProfile.this, "User belum mengupload foto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}