package com.example.laxman.bussuvidha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String  TAG = "PROFILE_ACTIVITY";
    private TextView textViewEmail;
    private Button buttonLogout;

    private Button buttonSave;
    private EditText editTextName;
    private EditText editTextAddress;
    private EditText editTextPhone;
    private EditText editTextBusName;
    private EditText editTextBusRoute;

    DatabaseReference users;
    FirebaseDatabase db;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        textViewEmail = findViewById(R.id.textViewEmail);
        buttonLogout = findViewById(R.id.buttonLogout);
        buttonSave = findViewById(R.id.buttonSave);
        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextBusName = findViewById(R.id.editTextBusName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextBusRoute = findViewById(R.id.editTextBusRoute);

        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("DriverInformation");

        //users=db.getReference("Users");

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));

        }

        String email = firebaseAuth.getCurrentUser().getEmail().trim();
        textViewEmail.setText("Welcome!"+" "+ email);

        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);


    }

    private void saveInformation(){
        String name = editTextName.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String email = firebaseAuth.getCurrentUser().getEmail().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String busName = editTextBusName.getText().toString().trim();
        String busRoute = editTextBusRoute.getText().toString().trim();
        Log.d(TAG,"Save Information...");

        UserInformation userInformation = new UserInformation(email,name,address,phone,busName,busRoute);
        FirebaseUser firebaseUser =firebaseAuth.getCurrentUser();

        try{
            databaseReference.child(firebaseUser.getUid()).setValue(userInformation).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    finish();
                    Toast.makeText (getApplicationContext(),"Information saved...", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,"Information saved...");
                    startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText (getApplicationContext(),"Information saving Failed",Toast.LENGTH_SHORT).show();

                }
            });
        }catch (DatabaseException e){
            Toast.makeText(this,"DatabaseException: "+e,Toast.LENGTH_SHORT).show();
            Log.d(TAG,"DatabaseException: "+e);
        }

    }

    @Override
    public void onClick(View v) {
        if(v == buttonSave){
            saveInformation();
        }
        if(v == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this,LoginActivity.class));
        }
    }
}
