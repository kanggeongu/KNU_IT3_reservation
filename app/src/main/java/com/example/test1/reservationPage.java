package com.example.test1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class reservationPage extends AppCompatActivity {

    TextView textView;
    EditText editTextStartTime, editTextEndTime, editTextUserName;
    Button buttonReservation;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User user;
    Room room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_page);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        textView = (TextView)findViewById(R.id.textView);
        editTextStartTime = (EditText)findViewById(R.id.editTextStartTime);
        editTextEndTime = (EditText)findViewById(R.id.editTextEndTime);
        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        buttonReservation = (Button)findViewById(R.id.buttonReservation);

        buttonReservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startTime = editTextStartTime.getText().toString();
                String endTime = editTextEndTime.getText().toString();
                String userName = editTextUserName.getText().toString();

                user = (User)getIntent().getSerializableExtra("user");
                room = (Room)getIntent().getSerializableExtra("room");

                RData rData;
                rData = new RData(startTime, endTime, user.userID,userName);
                room.pushData(room.roomID , rData);
                user.pushData(room.roomID , rData);
                databaseReference.child("Rooms").child(room.roomID).setValue(room);
                databaseReference.child("Users").child(user.userID).setValue(user);

                room = null;
                user = null;

                new AlertDialog.Builder(reservationPage.this)
                        .setTitle("")
                        .setMessage("예약 완료")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        });
    }
}
