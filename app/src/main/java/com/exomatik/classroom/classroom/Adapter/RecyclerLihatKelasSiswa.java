package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RecyclerLihatKelasSiswa extends RecyclerView.Adapter<RecyclerLihatKelasSiswa.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelKelasSiswa> dataList;

    public RecyclerLihatKelasSiswa(ArrayList<ModelKelasSiswa> paramArrayList) {
        this.dataList = paramArrayList;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {
        String alphabet = dataList.get(position).getNamaKelas().substring(0, 2);

        viewHolder.textNama.setText(dataList.get(position).getNamaKelas());
        viewHolder.textDesc.setText("Kelas " + dataList.get(position).getDescKelas());
        viewHolder.img.setText(alphabet);
        viewHolder.imgCheck.setVisibility(View.GONE);

        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator localIterator = dataSnapshot.getChildren().iterator();
                    while (localIterator.hasNext()) {
                        UserData localDataUser = (UserData) ((DataSnapshot) localIterator.next()).getValue(UserData.class);
                        if (localDataUser.getUsername().toString().equalsIgnoreCase(dataList.get(position).getUserNamePengajar())){
                            viewHolder.textPengajar.setText("Pengajar " + localDataUser.getNama());
                        }
                    }
                }
                else {
                    Toast.makeText(context, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_lihat_kelas, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNama, textDesc, textPengajar;
        private TextView img;
        private ImageView imgCheck;

        public bidangViewHolder(View paramView) {
            super(paramView);
            textNama = ((TextView) paramView.findViewById(R.id.text_nama));
            textDesc = ((TextView) paramView.findViewById(R.id.text_desc));
            textPengajar = ((TextView) paramView.findViewById(R.id.text_pengajar));
            img = ((TextView) paramView.findViewById(R.id.img_name));
            imgCheck = ((ImageView) paramView.findViewById(R.id.img_check));
        }
    }
}