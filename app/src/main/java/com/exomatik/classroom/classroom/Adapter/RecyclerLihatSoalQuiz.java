package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.Model.ModelSoal;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RecyclerLihatSoalQuiz extends RecyclerView.Adapter<RecyclerLihatSoalQuiz.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelSoal> dataList;

    public RecyclerLihatSoalQuiz(ArrayList<ModelSoal> paramArrayList) {
        this.dataList = paramArrayList;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {
        int nomor = position + 1;
        viewHolder.textNomor.setText("Nomor " + Integer.toString(nomor) + ". ");
        viewHolder.textSoal.setText(dataList.get(position).getSoal());
        viewHolder.textWaktu.setText("Waktu Pengerjaan Soal : " + Integer.toString(dataList.get(position).getWaktu()) + " Detik");
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_lihat_soal_quiz, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textSoal, textWaktu, textNomor;

        public bidangViewHolder(View paramView) {
            super(paramView);
            textSoal = ((TextView) paramView.findViewById(R.id.text_soal));
            textWaktu = ((TextView) paramView.findViewById(R.id.text_waktu));
            textNomor = ((TextView) paramView.findViewById(R.id.text_nomor));
        }
    }
}