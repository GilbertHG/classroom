package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exomatik.classroom.classroom.Model.ModelNilaiQuiz;
import com.exomatik.classroom.classroom.Model.ModelQuiz;
import com.exomatik.classroom.classroom.Model.ModelTemplateQuiz;
import com.exomatik.classroom.classroom.R;

import java.util.ArrayList;

public class RecyclerHasilQuiz extends RecyclerView.Adapter<RecyclerHasilQuiz.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelNilaiQuiz> dataList;

    public RecyclerHasilQuiz(ArrayList<ModelNilaiQuiz> paramArrayList) {
        this.dataList = paramArrayList;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {
        viewHolder.textQuiz.setText("Nama : " + dataList.get(position).getUsername());
        viewHolder.textJumlah.setText("Nilai : " + Integer.toString(dataList.get(position).getNilai()));
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_hasil_quiz, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textQuiz, textJumlah;

        public bidangViewHolder(View paramView) {
            super(paramView);
            textQuiz = ((TextView) paramView.findViewById(R.id.text_nama));
            textJumlah = ((TextView) paramView.findViewById(R.id.text_nilai));
        }
    }
}