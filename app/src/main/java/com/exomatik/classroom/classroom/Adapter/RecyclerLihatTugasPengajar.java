package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Model.ModelUploadedTugas;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerLihatTugasPengajar
        extends RecyclerView.Adapter<RecyclerLihatTugasPengajar.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelUploadedTugas> dataList;

    public RecyclerLihatTugasPengajar(ArrayList<ModelUploadedTugas> paramArrayList) {
        this.dataList = paramArrayList;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {
        int posisi = position + 1;

        viewHolder.textDateTime.setText(dataList.get(position).getDateANDtime());
        viewHolder.img.setImageResource(R.drawable.ic_eye_green);
        getDataUser(position, viewHolder.textNama, posisi);
    }

    private void getDataUser(final int position, final TextView textNama, final int nomor) {
        Query query = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("username")
                .equalTo(dataList.get(position).getUsernameSiswa());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserData data = snapshot.getValue(UserData.class);
                        textNama.setText(Integer.toString(nomor) + ". " + data.getNama());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_lihat_tugas_pengajar, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNama, textDateTime;
        private ImageView img;

        public bidangViewHolder(View paramView) {
            super(paramView);
            textNama = ((TextView) paramView.findViewById(R.id.text_nama));
            textDateTime = ((TextView) paramView.findViewById(R.id.text_date_time));
            img = (ImageView) paramView.findViewById(R.id.img_check);
        }
    }
}