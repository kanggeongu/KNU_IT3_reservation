package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class reservationHome extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String S;
    User user;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_home);

        //Log.e("Home() : ","Home");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        user = (User)getIntent().getSerializableExtra("user");

        Button.OnClickListener onClickListener = new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                String RoomID = null;
                switch (v.getId()){
                    case R.id.buttonRoom1:
                        RoomID = "room1";
                        break;
                    case R.id.buttonRoom2:
                        RoomID = "room2";
                        break;
                    case R.id.buttonRoom3:
                        RoomID = "room3";
                        break;
                    case R.id.buttonRoom4:
                        RoomID = "room4";
                        break;
                }


                databaseReference.child("Rooms").child(RoomID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Room room = dataSnapshot.getValue(Room.class);
                        intent = new Intent(getApplicationContext(),reservationPage.class);
                        //intent.putExtra("user",user);
                        intent.putExtra("room",room);

                        databaseReference.child("Users").child(user.userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                User realUser =dataSnapshot.getValue(User.class);
                                intent.putExtra("user",realUser);
                                startActivity(intent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        Button buttonRoom1 = (Button)findViewById(R.id.buttonRoom1);
        buttonRoom1.setOnClickListener(onClickListener);
        Button buttonRoom2 = (Button)findViewById(R.id.buttonRoom2);
        buttonRoom2.setOnClickListener(onClickListener);
        Button buttonRoom3 = (Button)findViewById(R.id.buttonRoom3);
        buttonRoom3.setOnClickListener(onClickListener);
        Button buttonRoom4 = (Button)findViewById(R.id.buttonRoom4);
        buttonRoom4.setOnClickListener(onClickListener);

        Button buttonMy = (Button)findViewById(R.id.buttonMy);
        buttonMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),myReservation.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

    }
}
