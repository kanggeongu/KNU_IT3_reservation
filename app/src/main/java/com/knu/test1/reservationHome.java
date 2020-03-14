package com.knu.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    public static Context HomeContext;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String S;
    User user;
    Intent intent;
    SwipeRefreshLayout swipeRefreshLayout;

    int ny =0, nm=0, nd=0;
    long selectedDate;
    DatePickerDialog.OnDateSetListener datePickerListener;
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    String stringNow = sdfNow.format(date);
    TextView txvDate2;

    LayoutInflater layoutInflater;
    LinearLayout linearLayoutRHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_home);

        startLoading();
        HomeContext = this;
        initView();
        initializeListener();
        InitializeOther();
    }

    public void startLoading(){
        Intent intent = new Intent(getApplicationContext(),Loading.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
                roomId = "103";
                break;
            case R.id.buttonRoom2:
                roomId = "104";
                break;
            case R.id.buttonRoom3:
                roomId = "106";
                break;
            case R.id.buttonRoom4:
                roomId = "111";
                break;
            case R.id.buttonRoom5:
                roomId = "413";
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
                        intent.putExtra("selectedDate", selectedDate);
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

    public void onClickHandlerReservationHome3(View view){
        String roomId = null;

        switch (view.getId()){
            case R.id.buttonRoom103:
                roomId = "103";
                break;
            case R.id.buttonRoom104:
                roomId = "104";
                break;
            case R.id.buttonRoom106:
                roomId = "106";
                break;
            case R.id.buttonRoom111:
                roomId = "111";
                break;
            case R.id.buttonRoom413:
                roomId = "413";
                break;
        }

        databaseReference.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room room = dataSnapshot.getValue(Room.class);
                intent = new Intent(getApplicationContext(),reservaionRoomList.class);
                intent.putExtra("room",room);
                intent.putExtra("selectedDate", selectedDate);
                startActivity(intent);
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
        changeImageView("room5");
        swipeRefreshLayout.setRefreshing(false);
    }

    public void setLayout(final String roomID){
        String ltv = "linearLayoutRHome"+roomID;

        int lID = getResources().getIdentifier(ltv,"id","com.knu.test1");

        linearLayoutRHome = (LinearLayout) findViewById(lID);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ,1f);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                ,1f);

        int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,1,getResources().getDisplayMetrics());
        linearLayoutRHome.setPadding(w,0,w,0);

        for(int i=9;i<=23;i++){
            LinearLayout wholeLayout = new LinearLayout(this);
            LinearLayout imageLayout = new LinearLayout(this);
            wholeLayout.setOrientation(LinearLayout.VERTICAL);
            wholeLayout.setLayoutParams(layoutParams2);
            imageLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView tv = new TextView(this);
            tv.setText(String.format("%02d", i));
            tv.setLayoutParams(layoutParams2);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.border);
            wholeLayout.addView(tv);

            for(int j=0;j<=30;j+=30){
                ImageView iv = new ImageView(this);
                iv.setLayoutParams(layoutParams);
                int k = i*100+j;
                String temp = "img"+Integer.toString(k)+roomID;
                int a = getResources().getIdentifier(temp,"id","com.knu.test1");
                iv.setId(a);
                iv.setImageResource(R.drawable.blank);
                iv.setBackgroundResource(R.drawable.border_white);
                imageLayout.addView(iv);
            }
            wholeLayout.addView(imageLayout);
            linearLayoutRHome.addView(wholeLayout);
        }
    }

    public void initView(){
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

        setLayout("room1");
        setLayout("room2");
        setLayout("room3");
        setLayout("room4");
        setLayout("room5");

        layoutInflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        user = (User)getIntent().getSerializableExtra("user");
    }

    public void initializeListener(){
        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ny = year; nm = month+1; nd = dayOfMonth;
                long y = year, m=month+1, d=dayOfMonth;
                txvDate2.setText(y+"년 " + m + "월 " + d + "일");
                selectedDate = y * 100000000 + m * 1000000 + d * 10000;

                onRefresh();
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
        selectedDate = (longNow/10000)*10000;

        onRefresh();
    }

    public void changeImageView(final String roomID){
        for(int i=900;i<=2330;){
            String temp = "img"+Long.toString(i)+roomID;
            int k = getResources().getIdentifier(temp,"id","com.knu.test1");
            ImageView img = (ImageView) findViewById(k);
            img.setBackgroundResource(R.drawable.border_white);
            if(i%100==0) i+=30;
            else i+=70;
        }

        String tempString = null;
        switch (roomID){
            case "room1":
                tempString = "103";
                break;
            case "room2":
                tempString = "104";
                break;
            case "room3":
                tempString = "106";
                break;
            case "room4":
                tempString = "111";
                break;
            case "room5":
                tempString = "413";
                break;
        }

        databaseReference.child("Rooms").child(tempString).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            int k = getResources().getIdentifier(temp,"id","com.knu.test1");
                            ImageView img = (ImageView) findViewById(k);
                            img.setBackgroundResource(R.drawable.border_black);
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

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(reservationHome.this)
                .setTitle("")
                .setMessage("정말 종료하시겠습니까?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(reservationHome.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                    }})
                .show();
    }
}
