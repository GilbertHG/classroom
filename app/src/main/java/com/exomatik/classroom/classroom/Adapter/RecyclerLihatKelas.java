package com.exomatik.classroom.classroom.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.BuatKelas;
import com.exomatik.classroom.classroom.Activity.SplashScreen;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelNilai;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerLihatKelas
        extends RecyclerView.Adapter<RecyclerLihatKelas.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelKelas> dataList;

    public RecyclerLihatKelas(ArrayList<ModelKelas> paramArrayList) {
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
        viewHolder.imgCheck.setVisibility(View.VISIBLE);
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