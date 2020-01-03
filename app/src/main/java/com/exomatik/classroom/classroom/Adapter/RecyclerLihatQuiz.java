package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exomatik.classroom.classroom.Model.ModelSoal;
import com.exomatik.classroom.classroom.Model.ModelTemplateQuiz;
import com.exomatik.classroom.classroom.R;

import java.util.ArrayList;

public class RecyclerLihatQuiz extends RecyclerView.Adapter<RecyclerLihatQuiz.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelTemplateQuiz> dataList;

    public RecyclerLihatQuiz(ArrayList<ModelTemplateQuiz> paramArrayList) {
        this.dataList = paramArrayList;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {
        viewHolder.textQuiz.setText("Nama Quiz : " + dataList.get(position).getNamaQuiz());
        viewHolder.textJumlah.setText("Jumlah Soal : " + Integer.toString(dataList.get(position).getListSoal().size()));
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_lihat_quiz, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textQuiz, textJumlah;

        public bidangViewHolder(View paramView) {
            super(paramView);
            textQuiz = ((TextView) paramView.findViewById(R.id.text_quiz));
            textJumlah = ((TextView) paramView.findViewById(R.id.text_jumlah));
        }
    }
}