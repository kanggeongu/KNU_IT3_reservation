package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

    public static Context mContext;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User user;
    ArrayList<myGroup> DataList;
    ExpandableListView myList;
    myGroup temp;

    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    String stringNow = sdfNow.format(date);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_reservation);

        mContext = this;
        init();
        func();
    }

    public void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        user = (User)getIntent().getSerializableExtra("user");
        myList = (ExpandableListView)findViewById(R.id.mylist);
    }

    public void func(){

        databaseReference.child("Users").child(user.userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                TreeMap<String, RData> tm = new TreeMap<String, RData>(user.userRMap);
                Iterator<String> iter = tm.keySet().iterator();
                ArrayList<String> delKeyList = new ArrayList<>();
                DataList = new ArrayList<myGroup>();

                while(iter.hasNext()){
                    //TextView textView = new TextView(myReservation.this);
                    String key = iter.next();
                    RData value = user.userRMap.get(key);

                    long chk1 = Long.parseLong(value.endTime);
                    long chk2 = (Long.parseLong(stringNow));

                    if(chk1<chk2){
                        delKeyList.add(key);
                        continue;
                    }

                    temp = new myGroup(value.startTime + "-" + value.endTime + "-" + key);
                    temp.child.add(value.userName  + "-" + value.userID);
                    DataList.add(temp);
                }

                for(String k : delKeyList){
                    user.userRMap.remove(k);
                }
                databaseReference.child("Users").child(user.userID).setValue(user);

                ExpandAdapter adapter = new ExpandAdapter(getApplicationContext(),R.layout.group_row,R.layout.child_row,DataList);
                myList.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void reservationDelete(String ID){
        final String[] SS = ID.split("-");
        Log.e("myReservation",ID);
        // SS[0] = startTIme, SS[1] = endTime, SS[2] = hashID, SS[3] = userID

        databaseReference.child("Rooms").child(SS[2].substring(12)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                //Toast.makeText(getApplicationContext(),"Hi : " + room.roomID,Toast.LENGTH_LONG).show();
                room.roomRMap.remove(SS[2]);
                databaseReference.child("Rooms").child(room.roomID).setValue(room);
                room = null;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        databaseReference.child("Users").child(SS[3]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                //Toast.makeText(getApplicationContext(),"Bye : " + user.userID,Toast.LENGTH_LONG).show();
                user.userRMap.remove(SS[2]);
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
