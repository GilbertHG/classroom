package com.exomatik.classroom.classroom.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.classroom.classroom.Activity.BuatKelas;
import com.exomatik.classroom.classroom.Activity.DetailKelas;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatKelas;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ContentPengajar extends Fragment implements ItemClickSupport.OnItemClickListener {
    private ArrayList<ModelKelas> listKelas = new ArrayList<ModelKelas>();
    private CircleImageView btnHelp;
    private View view;
    private TextView textNothing;
    private RelativeLayout btnBuatKelas;
    private UserPreference userPreference;
    private RecyclerView rcKelas;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        view = paramLayoutInflater.inflate(R.layout.content_pengajar, paramViewGroup, false);

        btnHelp = (CircleImageView) view.findViewById(R.id.img_help);
        btnBuatKelas = (RelativeLayout) view.findViewById(R.id.rl_buat_kelas);
        textNothing = (TextView) view.findViewById(R.id.text_nothing);
        rcKelas = (RecyclerView) view.findViewById(R.id.rc_kelas);

        userPreference = new UserPreference(getActivity());
        ItemClickSupport.addTo(rcKelas).setOnItemClickListener(this);

        getDataKelas();

        btnBuatKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BuatKelas.class));
                getActivity().finish();
            }
        });

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialShowcaseView.resetSingleUse(getActivity(), "Sequence Showcase");
                showTutorSequence(0);
            }
        });

        return view;
    }

    private void getDataKelas() {
        FirebaseDatabase.getInstance().getReference("kelas").child(userPreference.getKEY_USERNAME())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelKelas localDataUser = (ModelKelas) ((DataSnapshot) localIterator.next()).getValue(ModelKelas.class);
                                listKelas.add(new ModelKelas(localDataUser.getNamaKelas(), localDataUser.getDescKelas()
                                            , localDataUser.getUserNamePengajar(), localDataUser.getModelNilaiKelas()
                                            , localDataUser.getLihat()));
                            }

                            RecyclerLihatKelas adapterLihatKelas = new RecyclerLihatKelas(listKelas);
                            LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
                            rcKelas.setLayoutManager(localLinearLayoutManager);
                            rcKelas.setNestedScrollingEnabled(false);
                            rcKelas.setAdapter(adapterLihatKelas);
                        }
                        else {
                            textNothing.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "Gagal Mengambil Data Terbaru", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        DetailKelas.dataKelas = new ModelKelas(listKelas.get(position).getNamaKelas(),
                listKelas.get(position).getDescKelas(), listKelas.get(position).getUserNamePengajar(),
                listKelas.get(position).getModelNilaiKelas(), listKelas.get(position).getLihat());
        startActivity(new Intent(getActivity(), DetailKelas.class));
        getActivity().finish();
    }

    private void showTutorSequence(int paramInt) {
        ShowcaseConfig localShowcaseConfig = new ShowcaseConfig();
        localShowcaseConfig.setDelay(paramInt);
        MaterialShowcaseSequence localMaterialShowcaseSequence = new MaterialShowcaseSequence(getActivity(), "Sequence Showcase");
        localMaterialShowcaseSequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            public void onShow(MaterialShowcaseView paramAnonymousMaterialShowcaseView, int paramAnonymousInt) {
            }
        });
        localMaterialShowcaseSequence.setConfig(localShowcaseConfig);
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                .setTarget(btnBuatKelas).setDismissText("Selanjutnya")
                .setContentText("Untuk membuat kelas").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                .setTarget(rcKelas).setDismissText("Baik")
                .setContentText("Untuk mengelola kelas yang sudah dibuat").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}