package com.whaskalmanik.dtssensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText email;
    EditText password;
    EditText cPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(this);
        setContentView(R.layout.activity_register);
        password=findViewById(R.id.regPassword);
        email=findViewById(R.id.regEmail);
        cPassword=findViewById(R.id.confPassword);
    }

    public void register(View view) {
        String em=email.getText().toString();
        String p=password.getText().toString();
        String cp=cPassword.getText().toString();
        if(em.isEmpty()||p.isEmpty()||cp.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(p.equals(cp))
            {
                boolean mailCheck=db.checkMail(em);
                if(mailCheck)
                {
                    Boolean insert= db.insert(em,p);
                    if(insert)
                    {
                        Toast.makeText(getApplicationContext(),"User registered",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"User exists",Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Password do not match",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
