package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    LinearLayout childLayout;

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
        textViewEmpty.setTextSize(20);
        textViewEmpty.setGravity(Gravity.CENTER);
        textViewEmpty.setText("예약 정보가 없습니다.");
        topLayout.addView(textViewEmpty);
    }

    public void ListView(String key, RData rData){
        childLayout = new LinearLayout(myReservation.this);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        attachRoom(key.substring(12));
        attachTimeTable(rData);
        attachUser(rData.userName, rData.userID);
        attachTime(rData.startTime, rData.endTime);
        attachDeleteButton(key, rData);
        childLayout.setPadding(10,20,10,20);
        topLayout.addView(childLayout);
    }

    public void attachRoom(String roomID){
        TextView textViewRoom = new TextView(myReservation.this);
        textViewRoom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewRoom.setTextSize(20);
        textViewRoom.setGravity(Gravity.CENTER);
        textViewRoom.setText(roomID);
        textViewRoom.setTextColor(Color.BLACK);
        textViewRoom.setPadding(20,30,20,30);

        LinearLayout ll = new LinearLayout(myReservation.this);
        ll.setGravity(Gravity.CENTER);
        ll.addView(textViewRoom);
        ll.setBackgroundResource(R.drawable.border_tv);
        childLayout.addView(ll);
    }

    public void attachTimeTable(RData rData){

        LinearLayout linearLayoutTextRPage = new LinearLayout(myReservation.this);
        linearLayoutTextRPage.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayoutImageRPage = new LinearLayout(myReservation.this);
        linearLayoutImageRPage.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ,1f);

        for(int i=9;i<=23;i++){
            TextView tv = new TextView(this);
            tv.setText(String.format("%02d", i));
            tv.setLayoutParams(layoutParams);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.border);
            linearLayoutTextRPage.addView(tv);
        }

        long sTime = Long.parseLong(rData.startTime);
        long eTime = Long.parseLong(rData.endTime);
        long Date = sTime / 10000 * 10000;

        for(int i=9;i<=23;i++){
            for(int j=0;j<=30;j+=30){
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(layoutParams);
                int k = i*100+j;
                iv.setImageResource(R.drawable.blank);

                if(sTime <= Date + k && Date + k < eTime)
                    iv.setBackgroundResource(R.drawable.border_black);
                else
                    iv.setBackgroundResource(R.drawable.border_white);
                linearLayoutImageRPage.addView(iv);
            }
        }

        childLayout.addView(linearLayoutTextRPage);
        childLayout.addView(linearLayoutImageRPage);
    }

    public void attachUser(String userName, String userID){
        TextView textViewUser = new TextView(myReservation.this);
        textViewUser.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        textViewUser.setText("이름 : " + userName);
        textViewUser.setTextSize(12);
        textViewUser.setGravity(Gravity.CENTER);
        textViewUser.setPadding(10,20,10,20);

        TextView tv = new TextView(myReservation.this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tv.setText("아이디 : " + userID);
        tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(10,20,10,20);

        LinearLayout ll = new LinearLayout(myReservation.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER);
        ll.addView(textViewUser);
        ll.addView(tv);
        ll.setBackgroundResource(R.drawable.border_tv);
        childLayout.addView(ll);
    }

    public void attachTime(String startTime, String endTime){
        TextView textViewTime = new TextView(myReservation.this);
        textViewTime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        String y = startTime.substring(0,4);
        String m = startTime.substring(4,6);
        String d = startTime.substring(6,8);
        String sh = startTime.substring(8,10);
        String sm = startTime.substring(10,12);
        String eh = endTime.substring(8,10);
        String em = endTime.substring(10,12);

        textViewTime.setText(y+"."+m+"."+d+"\n"+sh+":"+sm+"~"+eh+":"+em);
        textViewTime.setTextSize(15);
        textViewTime.setGravity(Gravity.CENTER);
        textViewTime.setPadding(10,20,10,20);

        LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.addView(textViewTime);
        ll.setBackgroundResource(R.drawable.border_tv);

        childLayout.addView(ll);
    }

    public void attachDeleteButton(final String key, final RData rData){
        Button buttonDel = new Button(myReservation.this);
        buttonDel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        buttonDel.setText("삭제");
        buttonDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reservationDelete(key, rData);
            }
        });
        LinearLayout ll = new LinearLayout(this);
        ll.setGravity(Gravity.CENTER);
        ll.addView(buttonDel);
        ll.setBackgroundResource(R.drawable.border_tv);
        childLayout.addView(ll);
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
