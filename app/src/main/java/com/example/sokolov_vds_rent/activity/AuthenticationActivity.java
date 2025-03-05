package com.example.sokolov_vds_rent.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sokolov_vds_rent.R;
import com.example.sokolov_vds_rent.databinding.ActivityAuthenticationBinding;
import com.example.sokolov_vds_rent.fragment.LoginFragment;
import com.example.sokolov_vds_rent.fragment.RegistrationFragment;

public class AuthenticationActivity extends AppCompatActivity {

    private ActivityAuthenticationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.container, LoginFragment.class, null).commit();
    }

    public void openAccountRegistration(View view){
        getSupportFragmentManager().beginTransaction().replace(R.id.container, RegistrationFragment.class, null).addToBackStack(null).commit();
    }
}