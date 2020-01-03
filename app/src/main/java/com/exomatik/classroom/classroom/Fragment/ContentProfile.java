package com.exomatik.classroom.classroom.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.EditProfile;
import com.exomatik.classroom.classroom.Activity.LihatFoto;
import com.exomatik.classroom.classroom.Activity.SplashScreen;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContentProfile extends Fragment{
    private RelativeLayout btnLogout;
    private ImageView btnEdit;
    private CircleImageView imgFoto;
    private View view;
    private TextView textNama, textUsername, textEmail, textPhone, textAlamat, textUmur, textJk;
    private UserPreference userPreference;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        view = paramLayoutInflater.inflate(R.layout.content_profile, paramViewGroup, false);

        btnLogout = (RelativeLayout) view.findViewById(R.id.rl_logout);
        btnEdit = (ImageView) view.findViewById(R.id.img_edit);
        imgFoto = (CircleImageView) view.findViewById(R.id.img_foto);
        textNama = (TextView) view.findViewById(R.id.text_nama_isi);
        textUsername = (TextView) view.findViewById(R.id.text_username_isi);
        textEmail = (TextView) view.findViewById(R.id.text_email_isi);
        textPhone = (TextView) view.findViewById(R.id.text_phone_isi);
        textAlamat = (TextView) view.findViewById(R.id.text_alamat_isi);
        textUmur = (TextView) view.findViewById(R.id.text_umur_isi);
        textJk = (TextView) view.findViewById(R.id.text_jk_isi);

        userPreference = new UserPreference(getActivity());
        setData();

        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EditProfile.class));
                getActivity().finish();
            }
        });

        imgFoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userPreference.getKEY_FOTO() != null){
                    startActivity(new Intent(getActivity(), LihatFoto.class));
                }
                else {
                    Toast.makeText(getActivity(), "Anda belum mengupload gambar profil", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusData();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(), SplashScreen.class));
                getActivity().finish();
                Toast.makeText(getActivity(), "Berhasil Keluar", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void hapusData(){
        userPreference.setKEY_FOTO(null);
        userPreference.setKEY_UMUR(0);
        userPreference.setKEY_PHONE(null);
        userPreference.setKEY_ALAMAT(null);
        userPreference.setKEY_UID(null);
        userPreference.setKEY_USERNAME(null);
        userPreference.setKEY_EMAIL(null);
        userPreference.setKEY_NAME(null);
        userPreference.setKEY_JK(null);
    }

    private void setData(){
        if (userPreference.getKEY_NAME() != null){
            textNama.setText(userPreference.getKEY_NAME());
        }
        else {
            textNama.setText("-");
        }
        if (userPreference.getKEY_EMAIL() != null){
            textEmail.setText(userPreference.getKEY_EMAIL());
        }
        else {
            textEmail.setText("-");
        }
        if (userPreference.getKEY_USERNAME() != null){
            textUsername.setText(userPreference.getKEY_USERNAME());
        }
        else {
            textUsername.setText("-");
        }
        if (userPreference.getKEY_ALAMAT() != null){
            textAlamat.setText(userPreference.getKEY_ALAMAT());
        }
        else {
            textAlamat.setText("-");
        }
        if (userPreference.getKEY_PHONE() != null){
            textPhone.setText(userPreference.getKEY_PHONE());
        }
        else {
            textPhone.setText("-");
        }
        if (userPreference.getKEY_UMUR() >= 0){
            textUmur.setText(Integer.toString(userPreference.getKEY_UMUR()));
        }
        else {
            textUmur.setText("-");
        }
        if (userPreference.getKEY_JK() != null){
            textJk.setText(userPreference.getKEY_JK());
        }
        else {
            textJk.setText("-");
        }
        if (userPreference.getKEY_FOTO() != null){
            Uri localUri = Uri.parse(userPreference.getKEY_FOTO());
            Picasso.with(getActivity()).load(localUri).into(imgFoto);
        }else {
            imgFoto.setImageResource(R.drawable.ic_people_blue);
        }
    }
}