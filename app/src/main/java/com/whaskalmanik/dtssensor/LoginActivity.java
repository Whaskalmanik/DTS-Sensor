package com.whaskalmanik.dtssensor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText email;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);
        setContentView(R.layout.activity_login);
        password=findViewById(R.id.Password);
        email=findViewById(R.id.Email);
    }


    public void Login(View view) {
        String em=email.getText().toString();
        String p=password.getText().toString();
        boolean loginCheck=db.emailPassword(em,p);
        if(loginCheck)
        {
            Toast.makeText(getApplicationContext(),"OK",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Email and password do not match",Toast.LENGTH_LONG).show();
        }

    }

    public void register(View view) {
        Intent register = new Intent(getBaseContext(),RegisterActivity.class);
        startActivity(register);
    }

}
