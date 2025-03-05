package com.example.sokolov_vds_rent.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sokolov_vds_rent.R;
import com.example.sokolov_vds_rent.activity.MainActivity;
import com.example.sokolov_vds_rent.databinding.FragmentRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegistrationFragment extends Fragment {

    private FragmentRegistrationBinding binding;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegistrationBinding.inflate(inflater, container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.actionSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailInput.getText().toString();
                String password = binding.passwordInput.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()){
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent toMain = new Intent(getContext(), MainActivity.class);
                                startActivity(toMain);
                                Toast.makeText(getContext(),"Добро пожаловать!", Toast.LENGTH_LONG).show();
                                requireActivity().finish();
                            }else {
                                Toast.makeText(getContext(),"Ошибка Регистрации!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(getContext(),"Заполните все поля!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}