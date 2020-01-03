package com.exomatik.classroom.classroom.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.TambahKehadiran;
import com.exomatik.classroom.classroom.Model.ModelKehadiran;
import com.exomatik.classroom.classroom.Model.ModelPenilaian;
import com.exomatik.classroom.classroom.Model.UserData;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class RecyclerTambahPenilaian extends RecyclerView.Adapter<RecyclerTambahPenilaian.bidangViewHolder> {
    private Context context;
    private ArrayList<ModelPenilaian> dataList;
    private Activity activity;

    public RecyclerTambahPenilaian(ArrayList<ModelPenilaian> paramArrayList, Activity activity) {
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

        if (dataList.get(position).getNilai() == 404){
            viewHolder.etNilai.setText("");
        }
        else {
            viewHolder.etNilai.setText(Integer.toString(dataList.get(position).getNilai()));
        }

        viewHolder.etNilai.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String nilai = viewHolder.etNilai.getText().toString();

                if (!nilai.isEmpty()){
                    int nil = Integer.parseInt(nilai);
                    if (nil > 100){
                        viewHolder.etNilai.setError("Nilai tidak boleh lebih dari 100");
                        viewHolder.etNilai.setText("100");
                        dataList.set(position, new ModelPenilaian(dataList.get(position).getUsernamePengajar()
                                , dataList.get(position).getUsernameSiswa(), dataList.get(position).getKelas()
                                , dataList.get(position).getDesc(), dataList.get(position).getJenis()
                                , Integer.parseInt("100"), dataList.get(position).getPertemuan()));
                    }
                    else if (nil < 0){
                        viewHolder.etNilai.setError("Nilai tidak boleh kurang dari 0");
                        viewHolder.etNilai.setText("0");
                        dataList.set(position, new ModelPenilaian(dataList.get(position).getUsernamePengajar()
                                , dataList.get(position).getUsernameSiswa(), dataList.get(position).getKelas()
                                , dataList.get(position).getDesc(), dataList.get(position).getJenis()
                                , Integer.parseInt("0"), dataList.get(position).getPertemuan()));
                    }
                    else {
                        dataList.set(position, new ModelPenilaian(dataList.get(position).getUsernamePengajar()
                                , dataList.get(position).getUsernameSiswa(), dataList.get(position).getKelas()
                                , dataList.get(position).getDesc(), dataList.get(position).getJenis()
                                , Integer.parseInt(nilai), dataList.get(position).getPertemuan()));
                    }
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
        View localView = LayoutInflater.from(paramViewGroup.getContext()).inflate(R.layout.list_tambah_penilaian, paramViewGroup, false);
        this.context = paramViewGroup.getContext();
        return new bidangViewHolder(localView);
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder {
        private TextView textNomor, textNama;
        private EditText etNilai;

        public bidangViewHolder(final View paramView) {
            super(paramView);

            textNomor = (TextView) paramView.findViewById(R.id.text_nomor);
            textNama = (TextView) paramView.findViewById(R.id.text_nama);
            etNilai = (EditText) paramView.findViewById(R.id.et_nilai);
        }
    }
}