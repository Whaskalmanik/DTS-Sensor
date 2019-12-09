package com.whaskalmanik.dtssensor.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.whaskalmanik.dtssensor.Database.DatabaseHelper;
import com.whaskalmanik.dtssensor.R;

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
             Intent intent= new Intent(getApplicationContext(), NetworkActivity.class);
             intent.putExtra("email",em);
             startActivity(intent);
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Email and password do not match",Toast.LENGTH_LONG).show();
        }

    }

    public void register(View view) {
        Intent register = new Intent(getBaseContext(), RegisterActivity.class);
        startActivity(register);
    }

}
