package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.Login_RegisterPage.LoginPage.LoginActivity;
import com.example.heavymetals.R;

public class FitnessDeclaration3 extends AppCompatActivity {
    private TextView Fitness_Declaration_2;
    private Button btnPFDnext3;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fitness_declaration_3);
        Fitness_Declaration_2 = findViewById(R.id.Fitness_Declaration_2);
        btnPFDnext3 = findViewById(R.id.btnPFDnext3);

        Fitness_Declaration_2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration3.this, FitnessDeclaration2.class);
            startActivity(intent);
        });
        btnPFDnext3.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration3.this, ProfileFinish.class);
            startActivity(intent);
        });
    }
}
