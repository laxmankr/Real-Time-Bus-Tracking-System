package com.example.laxman.bussuvidha;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textSignin);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class) );
        }

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }


    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)) {
            //Empty email
            Toast.makeText(this, "please Enter email", Toast.LENGTH_SHORT).show();
            //Stoping the function execution further
            return;
        }
        if(TextUtils.isEmpty(password)) {
            //Empty password
            Toast.makeText(this, "please Enter password", Toast.LENGTH_SHORT).show();
            //Stoping the function execution further
            return;
        }

        //Validation is ok
        //will first show the progress dialog
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Successful registraterd
                            //profile activity
                            Toast.makeText(getApplicationContext(), "Registeration Sucessful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),ProfileActivity.class) );
                            progressDialog.dismiss();
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Registeration Unsucessful", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {
            registerUser();
        }
        if (view == textViewSignin) {
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
    }
}
