package com.whaskalmanik.dtssensor.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.whaskalmanik.dtssensor.Database.DatabaseHelper;
import com.whaskalmanik.dtssensor.R;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    DatabaseHelper db;
    EditText curPassword,newPassword,newConfirm;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_actitivity);
        db = new DatabaseHelper(this);
        email= Objects.requireNonNull(getIntent().getExtras()).getString("email");
        curPassword=findViewById(R.id.chCurPass);
        newPassword=findViewById(R.id.chNewPass);
        newConfirm=findViewById(R.id.chNewConf);
    }

    public void changePassword(View view) {
        String currPassword=curPassword.getText().toString();
        String newPass=newPassword.getText().toString();
        String confPass=newConfirm.getText().toString();
        if(currPassword.isEmpty()||newPass.isEmpty()||confPass.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
        }
        if(db.emailPassword(email,currPassword))
        {
            if(newPass.equals(confPass))
            {
                db.changePassword(email,newPass);
                Toast.makeText(getApplicationContext(),"Password was changed",Toast.LENGTH_LONG).show();
                finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Email and password do not match",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Password is not correct",Toast.LENGTH_SHORT).show();
        }

    }
}
