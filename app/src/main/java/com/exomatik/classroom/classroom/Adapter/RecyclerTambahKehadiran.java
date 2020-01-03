package com.exomatik.classroom.classroom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.BuatKelas;
import com.exomatik.classroom.classroom.Activity.TambahKehadiran;
import com.exomatik.classroom.classroom.Model.ModelKehadiran;
import com.exomatik.classroom.classroom.Model.ModelNilai;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RecyclerTambahKehadiran extends RecyclerView.Adapter<RecyclerTambahKehadiran.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelKehadiran> dataList;
    private Activity activity;

    public RecyclerTambahKehadiran(ArrayList<ModelKehadiran> paramArrayList, Activity activity) {
        this.dataList = paramArrayList;
        this.activity = activity;
    }

    public int getItemCount() {
        if (this.dataList != null) {
            return this.dataList.size();
        }
        return 0;
    }

    public void onBindViewHolder(final bidangViewHolder viewHolder, final int position) {
        int nomor = position;
        nomor++;
        viewHolder.textNomor.setText(Integer.toString(nomor));
        setTextNama(viewHolder, position);
        if (dataList.get(position).getHadir() == 0){
            viewHolder.rgHadir.check(R.id.rb_tidak);
        }
        else if (dataList.get(position).getHadir() == 1){
            viewHolder.rgHadir.check(R.id.rb_hadir);
        }
        else if (dataList.get(position).getHadir() == 2){
            viewHolder.rgHadir.check(R.id.rb_sakit);
        }
        else if (dataList.get(position).getHadir() == 3){
            viewHolder.rgHadir.check(R.id.rb_izin);
        }

        viewHolder.rgHadir.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = activity.findViewById(checkedId);

                if (rb.getText().toString().equals("Tidak Hadir")){
                    dataList.set(position, new ModelKehadiran(dataList.get(position).getUsernamePengajar()
                            , dataList.get(position).getUsernameSiswa(), dataList.get(position).getKelas()
                            , dataList.get(position).getDesc(), 0, TambahKehadiran.spinnerPertemuan.getSelectedItemPosition()));
                }
                else if (rb.getText().toString().equals("Hadir")){
                    dataList.set(position, new ModelKehadiran(dataList.get(position).getUsernamePengajar()
                            , dataList.get(position).getUsernameSiswa(), dataList.get(position).getKelas()
                            , dataList.get(position).getDesc(), 1, TambahKehadiran.spinnerPertemuan.getSelectedItemPosition()));
                }
                else if (rb.getText().toString().equals("Sakit")){
                    dataList.set(position, new ModelKehadiran(dataList.get(position).getUsernamePengajar()
                            , dataList.get(position).getUsernameSiswa(), dataList.get(position).getKelas()
                            , dataList.get(position).getDesc(), 2, TambahKehadiran.spinnerPertemuan.getSelectedItemPosition()));
                }
                else if (rb.getText().toString().equals("Izin")){
                    dataList.set(position, new ModelKehadiran(dataList.get(position).getUsernamePengajar()
                            , dataList.get(position).getUsernameSiswa(), dataList.get(position).getKelas()
                            , dataList.get(position).getDesc(), 3, TambahKehadiran.spinnerPertemuan.getSelectedItemPosition()));
                }
            }
        });
    }

    private void setTextNama(final bidangViewHolder viewHolder, final int position){
        FirebaseDatabase.getInstance().getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterator localIterator = dataSnapshot.getChildren().iterator();
                    while (localIterator.hasNext()) {
                        UserData localDataUser = (UserData) ((DataSnapshot) localIterator.next()).getValue(UserData.class);
                        if (localDataUser.getUsername().toString().equalsIgnoreCase(dataList.get(position).getUsernameSiswa())){
                            viewHolder.textNama.setText(localDataUser.getNama());
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
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_tambah_kehadiran, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNomor, textNama;
        private RadioGroup rgHadir;

        public bidangViewHolder(final View paramView) {
            super(paramView);

            textNomor = (TextView) paramView.findViewById(R.id.text_nomor);
            textNama = (TextView) paramView.findViewById(R.id.text_nama);
            rgHadir = (RadioGroup) paramView.findViewById(R.id.rg_hadir);
        }
    }
}