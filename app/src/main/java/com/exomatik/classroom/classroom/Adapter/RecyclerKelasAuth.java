package com.exomatik.classroom.classroom.Adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.GenerateQr;
import com.exomatik.classroom.classroom.Model.ModelAuthentikasi;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 17/09/2018.
 */

public class RecyclerKelasAuth extends RecyclerView.Adapter<RecyclerKelasAuth.bidangViewHolder> {
    private ArrayList<ModelAuthentikasi> dataList;
    private Context context;
    private ProgressDialog progressDialog = null;

    public RecyclerKelasAuth(ArrayList<ModelAuthentikasi> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_auth, parent, false);
        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(bidangViewHolder holder, final int position) {
        holder.txtNama.setText(dataList.get(position).getDataUser().getNama());
        holder.txtDesc.setText(dataList.get(position).getDataUser().getEmail());

        holder.btnTrue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setMessage("Mohon Tunggu...");
                progressDialog.setTitle("Proses");
                progressDialog.setCancelable(false);
                progressDialog.show();
                sendData(position);
            }
        });

        holder.btnFalse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                declined(position);
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

    private void sendData(final int position){
        DatabaseReference localDatabaseReference = FirebaseDatabase.getInstance().getReference();
        ModelKelasSiswa data = new ModelKelasSiswa(dataList.get(position).getDataUser().getNama()
                , dataList.get(position).getDataUser().getEmail()
                , dataList.get(position).getDataUser().getUsername()
                , dataList.get(position).getDataUser().getUid()
                , dataList.get(position).getDataKelas().getNamaKelas()
                , dataList.get(position).getDataKelas().getDescKelas()
                , dataList.get(position).getDataKelas().getUserNamePengajar()
                , dataList.get(position).getDataKelas().getLihat());
        final String alphabetDesc = alphabet(data.getDescKelas());

        localDatabaseReference.child("kelas_siswa")
                .child(data.getUserNamePengajar() + "_" + data.getNamaKelas() + "_" + alphabetDesc + "_" + data.getUsername())
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> paramAnonymous2Task) {
                        if (paramAnonymous2Task.isSuccessful()) {
                            progressDialog.dismiss();
                            hapusData(position, alphabetDesc);

                            Toast.makeText(context, "Berhasil memasukkan siswa, silahkan swipe ke atas untuk refresh", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        progressDialog.dismiss();
                        Toast.makeText(context, "Gagal Upload Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void declined(final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle("Tolak");
        alert.setMessage("Apakah anda yakin ingin menolak?");
        alert.setPositiveButton("Iya", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String alphabet = alphabet(dataList.get(position).getDataKelas().getDescKelas());

                hapusData(position, alphabet);

                GenerateQr.listAuthUser.remove(position);
                GenerateQr.adapter.notifyItemChanged(position);

                dialog.dismiss();
            }
        });
        alert.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void hapusData(int position, String alphabet){
        DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("auth_masuk")
                .child(dataList.get(position).getDataKelas().getUserNamePengajar())
                .child(dataList.get(position).getDataKelas().getNamaKelas() + "_" + alphabet)
                .child(dataList.get(position).getDataUser().getUsername());
        db_node.removeValue();
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNama, txtDesc;
        private ImageView btnTrue, btnFalse;

        public bidangViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.text_title);
            txtDesc = (TextView) itemView.findViewById(R.id.text_desc);
            btnTrue = (ImageView) itemView.findViewById(R.id.img_true);
            btnFalse = (ImageView) itemView.findViewById(R.id.img_false);
        }
    }
}
