package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

public class myReservation extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User user;

    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    String stringNow = sdfNow.format(date);

    LinearLayout topLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_reservation);

        startLoading();
        init();
        func();
    }

    public void startLoading(){
        Intent intent = new Intent(getApplicationContext(),Loading.class);
        startActivity(intent);
    }

    public void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        user = (User)getIntent().getSerializableExtra("user");
    }

    public void func(){
        topLayout = (LinearLayout)findViewById(R.id.linear1);
        topLayout.removeAllViews();

        databaseReference.child("Users").child(user.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                TreeMap<String, RData> tm = new TreeMap<String, RData>(user.userRMap);
                Iterator<String> iter = tm.keySet().iterator();
                ArrayList<String> delKeyList = new ArrayList<>();

                boolean flag = false;
                while(iter.hasNext()){
                    String key = iter.next();
                    RData value = user.userRMap.get(key);

                    long chk1 = Long.parseLong(value.endTime);
                    long chk2 = (Long.parseLong(stringNow));

                    if(chk1<chk2){
                        delKeyList.add(key);
                        continue;
                    }

                    flag = true;
                    ListView(key, value);
                }

                for(String k : delKeyList){
                    user.userRMap.remove(k);
                }
                databaseReference.child("Users").child(user.userID).setValue(user);


                if(!flag){
                    EmptyView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void EmptyView(){
        TextView textViewEmpty = new TextView(myReservation.this);
        textViewEmpty.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewEmpty.setTextSize(30);
        textViewEmpty.setText("예약 정보가 없습니다.");
        topLayout.addView(textViewEmpty);
    }

    public void ListView(String roomID, RData rData){
        attachRoom(roomID.substring(12));
        attachTimeTable();
        attachUser(rData.userName, rData.userID);
        attachTime(rData.startTime, rData.endTime);
        attachDeleteButton(roomID, rData);
    }

    public void attachRoom(String roomID){
        TextView textViewRoom = new TextView(myReservation.this);
        textViewRoom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewRoom.setText("방 : " + roomID);
        topLayout.addView(textViewRoom);
    }

    public void attachTimeTable(){

    }

    public void attachUser(String userName, String userID){
        TextView textViewUser = new TextView(myReservation.this);
        textViewUser.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewUser.setText("이름 : " + userName + " / 아이디 : " + userID);
        topLayout.addView(textViewUser);
    }

    public void attachTime(String startTime, String endTime){
        TextView textViewTime = new TextView(myReservation.this);
        textViewTime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewTime.setText("예약시간 : " + startTime + " ~ " + endTime);
        topLayout.addView(textViewTime);
    }

    public void attachDeleteButton(final String key, final RData rData){
        Button buttonDel = new Button(myReservation.this);
        buttonDel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonDel.setText("삭제");
        buttonDel.setBackgroundResource(R.drawable.blank);
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservationDelete(key, rData);
            }
        });
        topLayout.addView(buttonDel);
    }


    public void reservationDelete(final String roomID, RData rData){
        databaseReference.child("Rooms").child(roomID.substring(12)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                //Toast.makeText(getApplicationContext(),"Hi : " + room.roomID,Toast.LENGTH_LONG).show();
                room.roomRMap.remove(roomID);
                databaseReference.child("Rooms").child(room.roomID).setValue(room);
                room = null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Users").child(rData.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //Toast.makeText(getApplicationContext(),"Bye : " + user.userID,Toast.LENGTH_LONG).show();
                user.userRMap.remove(roomID);
                databaseReference.child("Users").child(user.userID).setValue(user);
                user = null;
                func();
                ((reservationHome)reservationHome.HomeContext).onRefresh();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getApplicationContext(),"Delete Complete",Toast.LENGTH_LONG).show();
    }
}
