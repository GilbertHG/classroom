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

import com.exomatik.classroom.classroom.Activity.DetailKelasSiswa;
import com.exomatik.classroom.classroom.Activity.ScanQR;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatKelasSiswa;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelasSiswa;
import com.exomatik.classroom.classroom.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class ContentSiswa extends Fragment implements ItemClickSupport.OnItemClickListener {
    private ArrayList<ModelKelasSiswa> listKelas = new ArrayList<ModelKelasSiswa>();
    private CircleImageView btnHelp;
    private View view;
    private TextView textNothing;
    private RelativeLayout btnJoinKelas;
    private UserPreference userPreference;
    private RecyclerView rcKelas;
    private DatabaseReference dbReference;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        view = paramLayoutInflater.inflate(R.layout.content_siswa, paramViewGroup, false);

        btnHelp = (CircleImageView) view.findViewById(R.id.img_help);
        btnJoinKelas = (RelativeLayout) view.findViewById(R.id.rl_join_kelas);
        textNothing = (TextView) view.findViewById(R.id.text_nothing);
        rcKelas = (RecyclerView) view.findViewById(R.id.rc_kelas);

        userPreference = new UserPreference(getActivity());
        ItemClickSupport.addTo(rcKelas).setOnItemClickListener(this);

        getDataKelas();

        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialShowcaseView.resetSingleUse(getActivity(), "Sequence Showcase");
                showTutorSequence(0);
            }
        });

        btnJoinKelas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ScanQR.class));
                getActivity().finish();
            }
        });

        return view;
    }

    private void getDataKelas() {
        Query query = FirebaseDatabase.getInstance().getReference("kelas_siswa")
                .orderByChild("username")
                .equalTo(userPreference.getKEY_USERNAME());
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            listKelas.clear();
            if (dataSnapshot.exists()){
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    ModelKelasSiswa dataKelas = snapshot.getValue(ModelKelasSiswa.class);
                    listKelas.add(dataKelas);
                }
                RecyclerLihatKelasSiswa adapterLihatKelas = new RecyclerLihatKelasSiswa(listKelas);
                LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
                rcKelas.setLayoutManager(localLinearLayoutManager);
                rcKelas.setNestedScrollingEnabled(false);
                rcKelas.setAdapter(adapterLihatKelas);
            }
            else{
                textNothing.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            textNothing.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Error, " + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        DetailKelasSiswa.dataKelasSiswa = new ModelKelasSiswa(listKelas.get(position).getNama(),
                listKelas.get(position).getEmail(), listKelas.get(position).getUsername(),
                listKelas.get(position).getUid(), listKelas.get(position).getNamaKelas(),
                listKelas.get(position).getDescKelas(), listKelas.get(position).getUserNamePengajar(),
                listKelas.get(position).getLihat()
                );
        startActivity(new Intent(getActivity(), DetailKelasSiswa.class));
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
                .setTarget(btnJoinKelas).setDismissText("Selanjutnya")
                .setContentText("Untuk join ke kelas, dengan cara Scan Qr Code kelas yang ingin dimasuki").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                .setTarget(rcKelas).setDismissText("Baik")
                .setContentText("Untuk masuk ke kelas").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}