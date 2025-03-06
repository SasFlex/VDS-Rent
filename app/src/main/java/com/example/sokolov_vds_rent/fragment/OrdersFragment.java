package com.example.sokolov_vds_rent.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Space;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sokolov_vds_rent.R;
import com.example.sokolov_vds_rent.databinding.FragmentOrdersBinding;
import com.example.sokolov_vds_rent.onGetImageUrl;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrdersFragment extends Fragment {

    private FragmentOrdersBinding binding;
    private FirebaseDatabase database;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("orders").child("basic").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot order : snapshot.getChildren()){
                    createCard("basic", order.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("orders").child("powerful").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot order : snapshot.getChildren()){
                    createCard("powerful", order.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void createCard(String s_category, String s_id){
        database.getReference().child("servers").child(s_category).child(s_id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                View item = getLayoutInflater().inflate(R.layout.cart_server_card, binding.ordersList, false);

                ConstraintLayout main = item.findViewById(R.id.main);
                TextView srv_name = item.findViewById(R.id.server_name);
                TextView cpu_info = item.findViewById(R.id.cpu_info);
                TextView ram_info = item.findViewById(R.id.ram_info);
                TextView disk_info = item.findViewById(R.id.disk_info);
                TextView os_name = item.findViewById(R.id.OS);
                ImageView logo = item.findViewById(R.id.os_logo);
                TextView id = item.findViewById(R.id.server_id);
                TextView category = item.findViewById(R.id.server_category);

                if (s_category == "basic"){
                    srv_name.setText("Стартовый");
                }else {
                    srv_name.setText("Мощный");
                }
                cpu_info.setText(String.format("CPU: " + dataSnapshot.child("cpu_threads").getValue(String.class) + " x " + dataSnapshot.child("thread_freq").getValue(String.class) + " ГГц"));
                ram_info.setText(String.format("RAM: " + dataSnapshot.child("ram_mb").getValue(String.class) + " Мб"));
                disk_info.setText(String.format(dataSnapshot.child("disk_type").getValue(String.class) + ": " + dataSnapshot.child("disk_mem_gb").getValue(String.class) + " Гб"));
                os_name.setText(String.format("ОС: " + dataSnapshot.child("os").getValue(String.class)));

                gettingLogo(dataSnapshot.child("os").getValue(String.class), new onGetImageUrl() {
                    @Override
                    public void onSuccess(boolean isSuccess, String url) {
                        if (isSuccess){
                            Glide.with(getContext()).load(url).into(logo);
                            binding.ordersList.addView(item);
                        }
                    }
                });

            }
        });
    }

    public void gettingLogo(String OS, onGetImageUrl onGetImageUrl){
        database.getReference().child("logos").child(OS).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String url = "";
                url = dataSnapshot.getValue(String.class);
                Log.d("MSG", OS);
                if (url != null){
                    Log.d("MSG", url);
                    onGetImageUrl.onSuccess(true, url);
                }

            }
        });
    }

}