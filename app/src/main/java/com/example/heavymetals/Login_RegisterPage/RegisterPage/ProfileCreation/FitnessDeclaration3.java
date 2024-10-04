package com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.heavymetals.R;

public class FitnessDeclaration3 extends AppCompatActivity {
    private TextView Fitness_Declaration_2;
    private Button btnPFDnext3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fitness_declaration_3);

        // Initialize Views
        Fitness_Declaration_2 = findViewById(R.id.Fitness_Declaration_2);
        btnPFDnext3 = findViewById(R.id.btnPFDnext3);
        Spinner spinnerStrengthExperience = findViewById(R.id.spinner_strength_experience);

        // Set Listeners
        Fitness_Declaration_2.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration3.this, FitnessDeclaration2.class);
            startActivity(intent);
        });

        btnPFDnext3.setOnClickListener(v -> {
            Intent intent = new Intent(FitnessDeclaration3.this, ProfileFinish.class);
            startActivity(intent);
        });

        // Create an ArrayAdapter using the custom spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.strength_experience_array, R.layout.custom_spinner_item);

        // Specify the custom layout to use for the dropdown list
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);

        // Apply the adapter to the spinner
        spinnerStrengthExperience.setAdapter(adapter);
    }
}
