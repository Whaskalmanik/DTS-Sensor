package com.whaskalmanik.dtssensor.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context)
    {
        super(context,"Login.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user(email text primary key, password text, name text, surname text)");
    }

    //deleting on new update
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
    }

    //creating new account
    public boolean insert(String email, String password,String name,String surname)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("email", email);
        contentValues.put("password",password);
        contentValues.put("name", name);
        contentValues.put("surname", surname);
        long ins= db.insert("user",null, contentValues);
        if(ins == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    //checking if email exists
    public Boolean checkMail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user where email=?", new String[]{email});
        if (cursor.getCount() > 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    //checking the email and password
    public boolean emailPassword(String email,String password)
    {
        SQLiteDatabase db= this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user where email=? and password=?",new String[]{email,password} );
        if(cursor.getCount()>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public Cursor select(String email)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from user where email=?", new String[]{email});
        return cursor;
    }

    public void changePassword(String email, String newPassword)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put("password",newPassword);
        db.update("user",cv,"email=?",new String[]{email});
    }
}
