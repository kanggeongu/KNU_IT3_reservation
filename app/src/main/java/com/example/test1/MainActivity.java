package com.example.test1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity {

    EditText editTextID,editTextPW;
    Button LoginButton;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        editTextID=(EditText)findViewById(R.id.editTextID);
        editTextPW=(EditText)findViewById(R.id.editTextPW);
        LoginButton=(Button)findViewById(R.id.LoginButton);

        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.knu_it3);


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String S = editTextID.getText().toString();
                final String P = editTextPW.getText().toString();

                if(S.equals("")){
                    Toast.makeText(getApplicationContext(),"ID is empty",Toast.LENGTH_LONG).show();
                    return;
                }
                if(P.equals("")){
                    Toast.makeText(getApplicationContext(),"PW is empty",Toast.LENGTH_LONG).show();
                    return;
                }

                databaseReference.child("Users").child(S).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user == null){
                            Toast.makeText(getApplicationContext(), "ID is not found",Toast.LENGTH_LONG).show();
                            return;
                        }

                        if(P.equals(user.userPW)){
                            Intent intent = new Intent(getApplicationContext(),reservationHome.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                            finish();
                            //Toast.makeText(getApplicationContext(),"User : "+user.userID + user.userPW,Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Not correct",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
