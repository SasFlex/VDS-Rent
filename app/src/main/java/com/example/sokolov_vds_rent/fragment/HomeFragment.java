package com.example.sokolov_vds_rent.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sokolov_vds_rent.R;
import com.example.sokolov_vds_rent.databinding.FragmentHomeBinding;
import com.example.sokolov_vds_rent.onGetImageUrl;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    FirebaseDatabase database;
    FirebaseAuth auth;

    private GestureDetector gestureDetectorPowerful, gestureDetectorBasic;

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
        binding = FragmentHomeBinding.inflate(inflater, container,false);
        return binding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gestureDetectorPowerful = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                snapToCenter(binding.powerfulServersHSV, binding.powerfulServersList);
                return true;
            }
        });

        gestureDetectorBasic = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                snapToCenter(binding.basicServersHSV, binding.basicServersList);
                return true;
            }
        });

        binding.basicServersHSV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetectorBasic.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP){
                    snapToCenter(binding.basicServersHSV, binding.basicServersList);
                    return true;
                }
                return false;
            }
        });

        binding.powerfulServersHSV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetectorPowerful.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP){
                    snapToCenter(binding.powerfulServersHSV, binding.powerfulServersList);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        createBasicServersList();
        createPowerfulServersList();
    }

    private void createPowerfulServersList() {

        database.getReference().child("servers").child("powerful").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot server : snapshot.getChildren()){
                    i++;
                    View item = getLayoutInflater().inflate(R.layout.server_card, binding.powerfulServersList, false);

                    TextView srv_name = item.findViewById(R.id.server_name);
                    TextView cpu_info = item.findViewById(R.id.cpu_info);
                    TextView ram_info = item.findViewById(R.id.ram_info);
                    TextView disk_info = item.findViewById(R.id.disk_info);
                    TextView os_name = item.findViewById(R.id.OS);
                    TextView price = item.findViewById(R.id.priceTV);
                    ImageView logo = item.findViewById(R.id.os_logo);
                    AppCompatButton buy = item.findViewById(R.id.buy_btn);
                    TextView id = item.findViewById(R.id.server_id);
                    TextView category = item.findViewById(R.id.server_category);

                    srv_name.setText(String.format("Мощный " + i));
                    cpu_info.setText(String.format("CPU: " + server.child("cpu_threads").getValue(String.class) + " x " + server.child("thread_freq").getValue(String.class) + " ГГц"));
                    ram_info.setText(String.format("RAM: " + server.child("ram_mb").getValue(String.class) + " Мб"));
                    disk_info.setText(String.format(server.child("disk_type").getValue(String.class) + ": " + server.child("disk_mem_gb").getValue(String.class) + " Гб"));
                    os_name.setText(String.format("ОС: " + server.child("os").getValue(String.class)));
                    price.setText(String.format(server.child("price").getValue(String.class) + "₽ / в Месяц"));
                    id.setText(server.getKey());
                    category.setText("powerful");

                    if (auth.getCurrentUser() == null){
                        buy.setEnabled(false);
                        buy.setText("Войдите в аккаунт");
                    }else {
                        checkOrders(category.getText().toString(), id.getText().toString(), new onGetImageUrl() {
                            @Override
                            public void onSuccess(boolean isSuccess, String url) {
                                if (isSuccess){
                                    buy.setText("Куплено");
                                    buy.setEnabled(false);
                                    buy.setBackgroundColor(getResources().getColor(R.color.buy_btn_green));
                                }
                            }
                        });
                    }

                    gettingLogo(server.child("os").getValue(String.class), new onGetImageUrl() {
                        @Override
                        public void onSuccess(boolean isSuccess, String url) {
                            if (isSuccess){
                                Glide.with(getContext()).load(url).into(logo);
                                binding.powerfulServersList.addView(item);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void createBasicServersList() {

        database.getReference().child("servers").child("basic").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot server : snapshot.getChildren()){
                    i++;
                    View item = getLayoutInflater().inflate(R.layout.server_card, binding.basicServersList, false);

                    TextView srv_name = item.findViewById(R.id.server_name);
                    TextView cpu_info = item.findViewById(R.id.cpu_info);
                    TextView ram_info = item.findViewById(R.id.ram_info);
                    TextView disk_info = item.findViewById(R.id.disk_info);
                    TextView os_name = item.findViewById(R.id.OS);
                    TextView price = item.findViewById(R.id.priceTV);
                    ImageView logo = item.findViewById(R.id.os_logo);
                    AppCompatButton buy = item.findViewById(R.id.buy_btn);
                    TextView id = item.findViewById(R.id.server_id);
                    TextView category = item.findViewById(R.id.server_category);

                    srv_name.setText(String.format("Стартовый " + i));
                    cpu_info.setText(String.format("CPU: " + server.child("cpu_threads").getValue(String.class) + " x " + server.child("thread_freq").getValue(String.class) + " ГГц"));
                    ram_info.setText(String.format("RAM: " + server.child("ram_mb").getValue(String.class) + " Мб"));
                    disk_info.setText(String.format(server.child("disk_type").getValue(String.class) + ": " + server.child("disk_mem_gb").getValue(String.class) + " Гб"));
                    os_name.setText(String.format("ОС: " + server.child("os").getValue(String.class)));
                    price.setText(String.format(server.child("price").getValue(String.class) + "₽ / в Месяц"));
                    id.setText(server.getKey());
                    category.setText("basic");


                    if (auth.getCurrentUser() == null){
                        buy.setEnabled(false);
                        buy.setText("Войдите в аккаунт");
                    }else {
                        checkOrders(category.getText().toString(), id.getText().toString(), new onGetImageUrl() {
                            @Override
                            public void onSuccess(boolean isSuccess, String url) {
                                if (isSuccess){
                                    buy.setText("Куплено");
                                    buy.setEnabled(false);
                                    buy.setBackgroundColor(getResources().getColor(R.color.buy_btn_green));
                                }
                            }
                        });
                    }

                    gettingLogo(server.child("os").getValue(String.class), new onGetImageUrl() {
                        @Override
                        public void onSuccess(boolean isSuccess, String url) {
                            if (isSuccess){
                                Glide.with(getContext()).load(url).into(logo);
                                binding.basicServersList.addView(item);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void checkOrders(String category, String id, onGetImageUrl onGetImageUrl){
        database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("orders").child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot order : snapshot.getChildren()){
                    if (order.getKey().equals(id)){
                        onGetImageUrl.onSuccess(true, "ok");
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void snapToCenter(HorizontalScrollView scrollView, LinearLayout layout) {
        int scrollX = scrollView.getScrollX();
        int centerX = scrollView.getWidth() / 2;

        View closestChild = null;
        int closestDistance = Integer.MAX_VALUE;

        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            int childCenter = child.getLeft() + child.getWidth() / 2;
            int distance = Math.abs(childCenter - scrollX - centerX);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestChild = child;
            }
        }

        if (closestChild != null) {
            int targetScrollX = closestChild.getLeft() - centerX + closestChild.getWidth() / 2;
            scrollView.smoothScrollTo(targetScrollX, 0);
        }
    }

}