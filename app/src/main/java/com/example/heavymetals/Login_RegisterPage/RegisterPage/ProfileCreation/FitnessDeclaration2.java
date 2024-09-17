package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.R;

public class FitnessDeclaration2 extends AppCompatActivity {
    TextView Profile_Declaration_2;
    Button btnPFDnext2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fitness_declaration_2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Profile_Declaration_2 = findViewById(R.id.Profile_Declaration_2);
        btnPFDnext2 = findViewById(R.id.btnPFDnext2);

        Profile_Declaration_2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration2.this, FitnessDeclaration.class);
            startActivity(intent);
        });
        btnPFDnext2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration2.this, FitnessDeclaration3.class);
            startActivity(intent);
        });
    }
}