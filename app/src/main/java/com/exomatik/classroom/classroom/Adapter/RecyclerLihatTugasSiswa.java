package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelTugas;
import com.exomatik.classroom.classroom.Model.ModelUploadedTugas;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerLihatTugasSiswa
        extends RecyclerView.Adapter<RecyclerLihatTugasSiswa.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelTugas> dataList;
    private UserPreference userPreference;

    public RecyclerLihatTugasSiswa(ArrayList<ModelTugas> paramArrayList) {
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
        viewHolder.textNama.setText(posisi + ". " +dataList.get(position).getNamaTugas());
        viewHolder.textDateTime.setText("Batas Tanggal  : " + dataList.get(position).getTanggalTugas()
                + "\nWaktu Akhir Pengumpulan : " + dataList.get(position).getWaktuTugas());

        cekDataTugas(position, viewHolder.img);
    }

    private void cekDataTugas(final int posisi, final ImageView img) {
        Query query = FirebaseDatabase.getInstance().getReference("tugas_uploaded")
                .orderByChild("usernamePengajar")
                .equalTo(dataList.get(posisi).getUsernamePengajar());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ModelUploadedTugas data = snapshot.getValue(ModelUploadedTugas.class);

                        if (data.getKelas().equals(dataList.get(posisi).getNamaKelas())
                                && data.getDesc().equals(dataList.get(posisi).getDescKelas())
                                && data.getUsernameSiswa().equals(userPreference.getKEY_USERNAME())) {
                            if (data.getNamaTugas().equals(dataList.get(posisi).getNamaTugas())){
                                img.setImageResource(R.drawable.ic_true_green);
                            }
                        }
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
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_lihat_tugas_siswa, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        this.userPreference = new UserPreference(context);
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