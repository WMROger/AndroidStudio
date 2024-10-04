package com.example.heavymetals.Login_RegisterPage.RegisterPage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.heavymetals.Login_RegisterPage.AuthenticationActivity;
import com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation.FitnessDeclaration;
import com.example.heavymetals.Login_RegisterPage.RegisterPage.ProfileCreation.ProfileCreation;
import com.example.heavymetals.R;

public class TermsConditionsActivity extends AppCompatActivity {
    //initialize variables
    CheckBox tncConfirmation;
    Button tncIagree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_terms_conditions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //assign variables
        tncConfirmation = findViewById(R.id.tncConfirmation);
        tncIagree = findViewById(R.id.tncIagree);

        //check if checkbox is checked
        tncConfirmation.setOnCheckedChangeListener((buttonView, isChecked) -> {

            tncIagree.setEnabled(isChecked);
        });

        //button to go next
        tncIagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the first name and last name from the intent
                String firstName = getIntent().getStringExtra("first_name");
                String lastName = getIntent().getStringExtra("last_name");
                String userId = getIntent().getStringExtra("user_id");

                Intent fitnessIntent = new Intent(TermsConditionsActivity.this, ProfileCreation.class);
                fitnessIntent.putExtra("user_id", userId);  // Pass user_id to FitnessDeclaration
                fitnessIntent.putExtra("first_name", firstName);
                fitnessIntent.putExtra("last_name", lastName);
                startActivity(fitnessIntent);
            }
        });
    }
}