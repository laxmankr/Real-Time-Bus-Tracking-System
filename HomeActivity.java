package com.example.laxman.bussuvidha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnUser,btnDriver;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnUser = (Button) findViewById(R.id.btnUser);
        btnDriver = (Button) findViewById(R.id.btnDriver);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class) );
        }


        btnUser.setOnClickListener(this);
        btnDriver.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if(v==btnUser){
            finish();
            startActivity(new Intent(this,BusLocationActivity.class));
        }
        if(v==btnDriver){
            finish();
            startActivity(new Intent(this,DriverLocationActivity.class));
        }
        return;
    }
}
