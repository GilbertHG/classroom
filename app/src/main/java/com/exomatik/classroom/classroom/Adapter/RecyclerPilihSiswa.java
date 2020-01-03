package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.ModelQuizInvite;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerPilihSiswa extends RecyclerView.Adapter<RecyclerPilihSiswa.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelQuizInvite> dataList;

    public RecyclerPilihSiswa(ArrayList<ModelQuizInvite> paramArrayList) {
        this.dataList = paramArrayList;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {

        getNama(dataList.get(position).getUsername(), viewHolder.textNama);

        if (dataList.get(position).isIkutQuiz()){
            viewHolder.imgTrue.setVisibility(View.VISIBLE);
        }
        else {
            viewHolder.imgTrue.setVisibility(View.GONE);
        }

        viewHolder.rlPilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.imgTrue.getVisibility() == View.VISIBLE){
                    viewHolder.imgTrue.setVisibility(View.GONE);
                    dataList.set(position, new ModelQuizInvite(dataList.get(position).getUsername()
                            , dataList.get(position).getNamaKelas(), dataList.get(position).getDescKelas()
                            , dataList.get(position).getUserNamePengajar(), dataList.get(position).getNamaQuiz()
                            , dataList.get(position).getKodeQuiz(), false, dataList.get(position).getTemplateQuiz()
                            ));
                }
                else {
                    viewHolder.imgTrue.setVisibility(View.VISIBLE);
                    dataList.set(position, new ModelQuizInvite(dataList.get(position).getUsername()
                            , dataList.get(position).getNamaKelas(), dataList.get(position).getDescKelas()
                            , dataList.get(position).getUserNamePengajar(), dataList.get(position).getNamaQuiz()
                            , dataList.get(position).getKodeQuiz(), true, dataList.get(position).getTemplateQuiz()
                    ));
                }
            }
        });
    }

    private void getNama(String username, final TextView textNama){
        Query query = FirebaseDatabase.getInstance()
                .getReference("users")
                .orderByChild("username")
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserData data = snapshot.getValue(UserData.class);
                        textNama.setText(data.getNama());
                    }
                }
                else {
                    Toast.makeText(context, "Error, user not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(context, "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_pilih_siswa, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNama;
        private ImageView imgTrue;
        private RelativeLayout rlPilih;

        public bidangViewHolder(View paramView) {
            super(paramView);
            textNama = ((TextView) paramView.findViewById(R.id.text_nama));
            imgTrue = (ImageView) paramView.findViewById(R.id.img_true);
            rlPilih = (RelativeLayout) paramView.findViewById(R.id.ln_pilih);
        }
    }
}