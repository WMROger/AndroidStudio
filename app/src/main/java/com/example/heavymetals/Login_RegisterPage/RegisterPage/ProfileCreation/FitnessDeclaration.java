package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.R;

public class FitnessDeclaration extends AppCompatActivity {
    Button btnPFDnext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_fitness_declaration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnPFDnext.findViewById(R.id.btnPFDnext);

        btnPFDnext.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration.this, FitnessDeclaration2.class);
            startActivity(intent);
        });
    }
}