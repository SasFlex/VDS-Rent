package com.example.sokolov_vds_rent.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;

import com.example.sokolov_vds_rent.R;
import com.example.sokolov_vds_rent.databinding.ActivityMainBinding;
import com.example.sokolov_vds_rent.fragment.AboutAppFragment;
import com.example.sokolov_vds_rent.fragment.AboutDevFragment;
import com.example.sokolov_vds_rent.fragment.HomeFragment;
import com.example.sokolov_vds_rent.fragment.InstructionsFragment;
import com.example.sokolov_vds_rent.fragment.OrdersFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.class, null).commit();

        MenuItem signoutItem = binding.navView.getMenu().findItem(R.id.sign_out);
        if (auth.getCurrentUser() == null) {
            signoutItem.setTitle("Войти");
        }else {
            signoutItem.setTitle("Выйти");
        }



        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.class, null).commit();
                } else if (item.getItemId() == R.id.orders) {
                    if (auth.getCurrentUser() != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, OrdersFragment.class, null).commit();
                    }else {
                        Toast.makeText(getApplicationContext(), "Необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } else if (item.getItemId() == R.id.instuctions) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, InstructionsFragment.class, null).commit();
                } else if (item.getItemId() == R.id.about_app) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AboutAppFragment.class, null).commit();
                } else if (item.getItemId() == R.id.about_developer) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AboutDevFragment.class, null).commit();
                } else if (item.getItemId() == R.id.sign_out) {
                    if (auth.getCurrentUser() != null){
                        auth.signOut();
                    }
                    Intent toAuth = new Intent(getApplicationContext(), AuthenticationActivity.class);
                    startActivity(toAuth);
                    finish();
                }
                binding.main.closeDrawer(GravityCompat.START, true);
                return true;

            }
        });

    }

    public void buyServer(View view){
        View root = (View) view.getParent();
        TextView category = root.findViewById(R.id.server_category);
        TextView id = root.findViewById(R.id.server_id);
        AppCompatButton buy = root.findViewById(R.id.buy_btn);
        buy.setText("Куплено");
        buy.setEnabled(false);
        buy.setBackgroundColor(getResources().getColor(R.color.buy_btn_green));
        database.getReference().child("users").child(auth.getCurrentUser().getUid()).child("orders")
                .child(category.getText().toString()).child(id.getText().toString()).setValue("successful");
    }
}