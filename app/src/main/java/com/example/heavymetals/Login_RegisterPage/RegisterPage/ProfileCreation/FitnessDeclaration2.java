package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.R;

public class FitnessDeclaration2 extends AppCompatActivity {
    private TextView Profile_Declaration_2;
    private Button btnPFDnext2;
    private RadioGroup genderRadioGroup;
    private RadioButton FemaleRadioButton, MaleRadioButton;

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
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        MaleRadioButton = findViewById(R.id.MaleRadioButton);
        FemaleRadioButton = findViewById(R.id.FemaleRadioButton);

        Profile_Declaration_2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration2.this, FitnessDeclaration.class);
            startActivity(intent);
        });

        btnPFDnext2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration2.this, FitnessDeclaration3.class);
            startActivity(intent);
        });

        // Listener to highlight the selected radio button
        genderRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.MaleRadioButton) {
                // Male selected
                MaleRadioButton.setTextColor(getResources().getColor(R.color.custom_orange));
                FemaleRadioButton.setTextColor(getResources().getColor(R.color.unselected_color));
            } else if (checkedId == R.id.FemaleRadioButton) {
                // Female selected
                FemaleRadioButton.setTextColor(getResources().getColor(R.color.custom_orange));
                MaleRadioButton.setTextColor(getResources().getColor(R.color.unselected_color));
            }
        });
    }
}
