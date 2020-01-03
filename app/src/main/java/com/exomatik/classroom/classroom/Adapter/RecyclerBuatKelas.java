package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.BuatKelas;
import com.exomatik.classroom.classroom.Model.ModelNilai;
import com.exomatik.classroom.classroom.R;

import java.util.ArrayList;

public class RecyclerBuatKelas
        extends RecyclerView.Adapter<RecyclerBuatKelas.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelNilai> dataList;

    public RecyclerBuatKelas(ArrayList<ModelNilai> paramArrayList) {
        this.dataList = paramArrayList;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {
        if (BuatKelas.listBuatKelas.get(position).getNama() != null){
            viewHolder.etNama.setText(BuatKelas.listBuatKelas.get(position).getNama());
            viewHolder.etJumlah.setText(Integer.toString(BuatKelas.listBuatKelas.get(position).getJumlah()));
            viewHolder.etPersen.setText(Integer.toString(BuatKelas.listBuatKelas.get(position).getPersentase()));
        }

        viewHolder.imgDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.etNama.setText("");
                viewHolder.etJumlah.setText("");
                viewHolder.etPersen.setText("");

                BuatKelas.listBuatKelas.remove(position);
                BuatKelas.adapterBuatKelas.notifyDataSetChanged();
            }
        });
    }

    public bidangViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt) {
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_buat_kelas, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private EditText etNama, etJumlah, etPersen;
        private ImageView imgDismiss;

        public bidangViewHolder(View paramView) {
            super(paramView);
            etNama = ((EditText) paramView.findViewById(R.id.et_nama_nilai));
            etJumlah = ((EditText) paramView.findViewById(R.id.et_jumlah_nilai));
            etPersen = ((EditText) paramView.findViewById(R.id.et_persen_nilai));
            imgDismiss = ((ImageView) paramView.findViewById(R.id.img_dismiss));

        }
    }
}