package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class reservationHome extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String S;
    User user;
    Intent intent;
    SwipeRefreshLayout swipeRefreshLayout;

    int ny =0, nm=0, nd=0;
    long selectedDate, temp=0;
    DatePickerDialog.OnDateSetListener datePickerListener;
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    String stringNow = sdfNow.format(date);
    TextView txvDate2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_home);

        temp = 20;
        temp *= 100; temp *= 10000; temp *= 10000;

        init();
        initializeListener();
        InitializeOther();
    }

    public void onClickHandlerReservationHome1(View view){
        switch (view.getId()){
            case R.id.btnDatepicker2:
                DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener, ny,nm-1,nd);
                dialog.show();
                break;
            case R.id.buttonMy:
                Intent intent = new Intent(this,myReservation.class);
                intent.putExtra("user",user);
                startActivity(intent);
                break;
        }
    }

    public void onClickHandlerReservationHome2(View view){
        String roomId = null;

        switch (view.getId()){
            case R.id.buttonRoom1:
                roomId = "room1";
                break;
            case R.id.buttonRoom2:
                roomId = "room2";
                break;
            case R.id.buttonRoom3:
                roomId = "room3";
                break;
            case R.id.buttonRoom4:
                roomId = "room4";
                break;
        }

        databaseReference.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                intent = new Intent(getApplicationContext(),reservationPage.class);
                intent.putExtra("room",room);

                databaseReference.child("Users").child(user.userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User realUser = dataSnapshot.getValue(User.class);
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

    @Override
    public void onRefresh() {
        changeImageView("room1");
        changeImageView("room2");
        changeImageView("room3");
        changeImageView("room4");
        swipeRefreshLayout.setRefreshing(false);
    }


    public void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        txvDate2 = (TextView)findViewById(R.id.txvDate2);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );

        user = (User)getIntent().getSerializableExtra("user");
    }

    public void initializeListener(){
        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ny = year; nm = month+1; nd = dayOfMonth;
                long y = year, m=month+1, d=dayOfMonth;
                txvDate2.setText(y+"년 " + m + "월 " + d + "일");
                selectedDate = y % 100 * 100000000+m*1000000+d*10000;

                changeImageView("room1");
                changeImageView("room2");
                changeImageView("room3");
                changeImageView("room4");
            }
        };
    }

    public void InitializeOther(){
        long longNow = Long.parseLong(stringNow);
        long y = longNow/100000000;
        long m = (longNow%100000000)/1000000;
        long d = (longNow%1000000)/10000;
        ny=(int)y; nm=(int)m; nd=(int)d;
        txvDate2.setText(y+"년 "+m+"월 "+d+"일");
        selectedDate = (longNow/10000)*10000 - temp;

        changeImageView("room1");
        changeImageView("room2");
        changeImageView("room3");
        changeImageView("room4");
    }

    public void changeImageView(final String roomID){
        for(int i=900;i<=2330;){
            String temp = "img"+Long.toString(i)+roomID;
            int k = getResources().getIdentifier(temp,"id","com.example.test1");
            ImageView img = (ImageView) findViewById(k);
            img.setImageResource(R.drawable.white);
            if(i%100==0) i+=30;
            else i+=70;
        }

        databaseReference.child("Rooms").child(roomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                Iterator<String> iter = room.roomRMap.keySet().iterator();
                while(iter.hasNext()){
                    String key = iter.next();
                    RData value = room.roomRMap.get(key);
                    String rstime = value.startTime, retime = value.endTime;

                    if(selectedDate <= Long.parseLong(rstime) && Long.parseLong(retime) < selectedDate + 10000){
                        long tempStart = Long.parseLong(rstime) % 10000;
                        long tempEnd = Long.parseLong(retime) % 10000;
                        for(long i=tempStart;i<tempEnd;){
                            String temp = "img"+Long.toString(i)+ roomID;
                            int k = getResources().getIdentifier(temp,"id","com.example.test1");
                            ImageView img = (ImageView) findViewById(k);
                            img.setImageResource(R.drawable.black);
                            if(i%100==0) i+=30;
                            else i+=70;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
