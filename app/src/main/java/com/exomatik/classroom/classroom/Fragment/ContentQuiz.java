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
import com.exomatik.classroom.classroom.Activity.BuatQuiz;
import com.exomatik.classroom.classroom.Activity.DetailKelas;
import com.exomatik.classroom.classroom.Activity.DetailQuiz;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatKelas;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatQuiz;
import com.exomatik.classroom.classroom.Adapter.RecyclerLihatSoalQuiz;
import com.exomatik.classroom.classroom.Featured.ItemClickSupport;
import com.exomatik.classroom.classroom.Featured.UserPreference;
import com.exomatik.classroom.classroom.Model.ModelKelas;
import com.exomatik.classroom.classroom.Model.ModelTemplateQuiz;
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

public class ContentQuiz extends Fragment implements ItemClickSupport.OnItemClickListener {
    private ArrayList<ModelTemplateQuiz> listQuiz = new ArrayList<ModelTemplateQuiz>();
    private CircleImageView btnHelp;
    private View view;
    private TextView textNothing;
    private RelativeLayout btnBuatQuiz;
    private UserPreference userPreference;
    private RecyclerView rcQuiz;

    @Nullable
    public View onCreateView(@NonNull LayoutInflater paramLayoutInflater, @Nullable ViewGroup paramViewGroup, @Nullable Bundle paramBundle) {
        view = paramLayoutInflater.inflate(R.layout.content_quiz, paramViewGroup, false);

        btnHelp = (CircleImageView) view.findViewById(R.id.img_help);
        btnBuatQuiz = (RelativeLayout) view.findViewById(R.id.rl_buat_quiz);
        textNothing = (TextView) view.findViewById(R.id.text_nothing);
        rcQuiz = (RecyclerView) view.findViewById(R.id.rc_quiz);

        userPreference = new UserPreference(getActivity());
        ItemClickSupport.addTo(rcQuiz).setOnItemClickListener(this);

        getDataQuiz();

        btnBuatQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BuatQuiz.class));
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

    private void getDataQuiz() {
        FirebaseDatabase.getInstance()
                .getReference("template_quiz")
                .child(userPreference.getKEY_USERNAME())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterator localIterator = dataSnapshot.getChildren().iterator();
                            while (localIterator.hasNext()) {
                                ModelTemplateQuiz localDataUser = (ModelTemplateQuiz) ((DataSnapshot) localIterator.next()).getValue(ModelTemplateQuiz.class);

                                listQuiz.add(localDataUser);
                            }

                            RecyclerLihatQuiz adapterLihatKelas = new RecyclerLihatQuiz(listQuiz);
                            LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(getActivity(), 1, false);
                            rcQuiz.setLayoutManager(localLinearLayoutManager);
                            rcQuiz.setNestedScrollingEnabled(false);
                            rcQuiz.setAdapter(adapterLihatKelas);
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
        DetailQuiz.templateQuiz = listQuiz.get(position);
        startActivity(new Intent(getActivity(), DetailQuiz.class));
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
                .setTarget(btnBuatQuiz).setDismissText("Selanjutnya")
                .setContentText("Untuk membuat template yang berisi soal-soal Quiz").withRectangleShape().build());
        localMaterialShowcaseSequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                .setTarget(rcQuiz).setDismissText("Baik")
                .setContentText("Untuk melihat detail quiz yang sudah dibuat").withRectangleShape().build());
        localMaterialShowcaseSequence.start();
    }
}