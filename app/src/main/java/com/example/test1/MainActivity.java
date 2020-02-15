package com.example.test1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText editTextID,editTextPW;
    Button UploadDatabaseButton;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextID=(EditText)findViewById(R.id.editTextID);
        editTextPW=(EditText)findViewById(R.id.editTextPW);
        UploadDatabaseButton=(Button)findViewById(R.id.UploadDatabaseButton);

        UploadDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*databaseReference.child("message").push().setValue("2");
                databaseReference.child("message").child("gbgg").setValue("2");*/

                String ID = editTextID.getText().toString();
                if(ID.equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter ID",Toast.LENGTH_LONG).show();
                    return;
                }

                /*String PW = editTextPW.getText().toString();
                if(PW.equals("")){
                    Toast.makeText(getApplicationContext(),"Please Enter PW",Toast.LENGTH_LONG).show();
                    return;
                }*/

                databaseReference.child("User").child(ID).child("ID").setValue(ID);
                databaseReference.child("User").child(ID).child("PW").setValue("3ghrhks"+ID);
                Toast.makeText(getApplicationContext(),"Upload Completed",Toast.LENGTH_LONG).show();
            }
        });
    }
}
