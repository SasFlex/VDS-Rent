package com.example.sokolov_vds_rent.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import com.example.sokolov_vds_rent.R;
import com.example.sokolov_vds_rent.databinding.ActivityMainBinding;
import com.example.sokolov_vds_rent.fragment.AboutAppFragment;
import com.example.sokolov_vds_rent.fragment.AboutDevFragment;
import com.example.sokolov_vds_rent.fragment.HomeFragment;
import com.example.sokolov_vds_rent.fragment.InstructionsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, HomeFragment.class, null).commit();
                } else if (item.getItemId() == R.id.instuctions) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, InstructionsFragment.class, null).commit();
                } else if (item.getItemId() == R.id.about_app) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AboutAppFragment.class, null).commit();
                } else if (item.getItemId() == R.id.about_developer) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, AboutDevFragment.class, null).commit();
                }
                binding.main.closeDrawer(GravityCompat.START);
                return true;

            }
        });

    }
}