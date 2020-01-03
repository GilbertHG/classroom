package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exomatik.classroom.classroom.Model.ModelSoal;
import com.exomatik.classroom.classroom.R;

import java.util.ArrayList;

public class RecyclerLihatSoalDetail extends RecyclerView.Adapter<RecyclerLihatSoalDetail.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelSoal> dataList;

    public RecyclerLihatSoalDetail(ArrayList<ModelSoal> paramArrayList) {
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

        if (dataList.get(position).getJawaban() == 1){
            viewHolder.textJawaban.setText("Jawaban : " + dataList.get(position).getJawabA());
        }
        else if (dataList.get(position).getJawaban() == 2){
            viewHolder.textJawaban.setText("Jawaban : " + dataList.get(position).getJawabB());
        }
        else if (dataList.get(position).getJawaban() == 3){
            viewHolder.textJawaban.setText("Jawaban : " + dataList.get(position).getJawabC());
        }
        else if (dataList.get(position).getJawaban() == 4){
            viewHolder.textJawaban.setText("Jawaban : " + dataList.get(position).getJawabD());
        }
        viewHolder.textWaktu.setText("Waktu Pengerjaan Soal : " + Integer.toString(dataList.get(position).getWaktu()) + " Detik");
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_lihat_soal_detail, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textSoal, textWaktu, textNomor, textJawaban;

        public bidangViewHolder(View paramView) {
            super(paramView);
            textSoal = ((TextView) paramView.findViewById(R.id.text_soal));
            textWaktu = ((TextView) paramView.findViewById(R.id.text_waktu));
            textNomor = ((TextView) paramView.findViewById(R.id.text_nomor));
            textJawaban = (TextView) paramView.findViewById(R.id.text_jawaban);
        }
    }
}