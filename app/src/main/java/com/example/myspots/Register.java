package com.example.myspots;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    private EditText eEmail;
    private EditText ePassword;
    private TextView Login_BTN;
    private Button Register_BTN;
    private FirebaseAuth mAuth;

    private EditText eFirstname;
    private EditText eSurname;
    private EditText eConfirmPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        eEmail = findViewById(R.id.logEmail);
        ePassword = findViewById(R.id.logPassword);
        Login_BTN = findViewById(R.id.txtLogin);
        Register_BTN = findViewById(R.id.btnRegister);
        eFirstname = findViewById(R.id.logFirstname);
        eSurname = findViewById(R.id.logSurname);
        eConfirmPassword = findViewById(R.id.logConfirmPassword);

        mAuth = FirebaseAuth.getInstance();

        Register_BTN.setOnClickListener(view -> {
            registerUser();
        });

        Login_BTN.setOnClickListener(view -> {
            startActivity(new Intent(Register.this, MainActivity.class));
        });
    }
    private void registerUser() {
        String email = eEmail.getText().toString().trim();
        String password = ePassword.getText().toString().trim();
        String firstname = eFirstname.getText().toString().trim();
        String surname = eSurname.getText().toString().trim();
        String conPass = eConfirmPassword.getText().toString().trim();
        InputValidation iv = new InputValidation();

        if(TextUtils.isEmpty(email)){
            eEmail.setError("Please Enter Email!!");
        }
        // this validates whether it contains an @ sign
        else if (!email.contains("@"))
        {
            eEmail.setError("Please Enter a Valid Email!!");
        } else if (TextUtils.isEmpty(password))
        {
            ePassword.setError("Please enter Password!!");
        } else if (iv.ValidatePassword(password, Register.this) == false) // validates password and sends errors on toasts
        {
            iv.msg("Password is Invalid!!", Register.this);
        } else
        {

            if(password.equals(conPass))
            {
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this,"You have Been Registered!!!",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Register.this, MainActivity.class));
                        }else{
                            Toast.makeText(Register.this,"You have Not Been Registered!!!"+ task.getException().getMessage() ,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else
            {
                Toast.makeText(Register.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            }

        }

    }
}