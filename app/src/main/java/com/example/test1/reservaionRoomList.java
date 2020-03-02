package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;
import java.util.TreeMap;

public class reservaionRoomList extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView toolbarTitle1, txvDate;
    Room room;
    Button btnDatepicker;
    DatePickerDialog.OnDateSetListener datePickerListener;

    int ny =0, nm=0, nd=0;
    long selectedDate;

    LinearLayout topLayout;
    LinearLayout childLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservaion_room_list);

        startLoading();
        init();
        initializeListener();
        InitializeOther();
    }

    public void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        toolbarTitle1 = (TextView)findViewById(R.id.toolbarTitle1);
        room = (Room)getIntent().getSerializableExtra("room");

        toolbarTitle1.setText( room.roomID );
        txvDate = (TextView)findViewById(R.id.txvDate);

        btnDatepicker = (Button)findViewById(R.id.btnDatepicker);
        btnDatepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(reservaionRoomList.this, datePickerListener, ny,nm-1,nd);
                dialog.show();
            }
        });
    }

    public void initializeListener(){
        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ny = year; nm = month+1; nd = dayOfMonth;
                long y = year, m=month+1, d=dayOfMonth;
                txvDate.setText(y+"년 " + m + "월 " + d + "일");
                selectedDate = y * 100000000+m*1000000+d*10000;
                changeView();
            }
        };
    }

    public void InitializeOther(){
        long longNow = getIntent().getExtras().getLong("selectedDate");
        long y = longNow/100000000;
        long m = (longNow%100000000)/1000000;
        long d = (longNow%1000000)/10000;
        ny=(int)y; nm=(int)m; nd=(int)d;
        txvDate.setText(y+"년 "+m+"월 "+d+"일");
        selectedDate = (longNow/10000)*10000;

        changeView();
    }

    public void startLoading(){
        Intent intent = new Intent(getApplicationContext(),Loading.class);
        startActivity(intent);
    }

    public void changeView(){
        topLayout = (LinearLayout)findViewById(R.id.linear1);
        topLayout.removeAllViews();

        databaseReference.child("Rooms").child(room.roomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Room realRoom = dataSnapshot.getValue(Room.class);

                TreeMap<String, RData> tm = new TreeMap<String, RData>(realRoom.roomRMap);
                Iterator<String> iter = tm.keySet().iterator();
                long now = ny * 10000 + nm * 100 + nd;

                boolean flag = false;
                while(iter.hasNext()){
                    String key = iter.next();
                    RData value = realRoom.roomRMap.get(key);

                    if(now <= Long.valueOf(value.startTime)/10000 && Long.valueOf(value.endTime)/10000 < now + 1) {
                        flag = true;
                        ListView(key, value);
                    }
                }

                if(!flag) {
                    EmptyView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void EmptyView(){
        TextView textViewEmpty = new TextView(reservaionRoomList.this);
        textViewEmpty.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textViewEmpty.setTextSize(20);
        textViewEmpty.setGravity(Gravity.CENTER);
        textViewEmpty.setText("예약 정보가 없습니다.");
        topLayout.addView(textViewEmpty);
    }

    public void ListView(String key, RData rData){
        childLayout = new LinearLayout(reservaionRoomList.this);
        childLayout.setOrientation(LinearLayout.VERTICAL);
        attachTimeTable(rData);
        attachUser(rData.userName, rData.userID);
        attachTime(rData.startTime, rData.endTime);
        childLayout.setPadding(10,20,10,20);
        topLayout.addView(childLayout);
    }

    public void attachTimeTable(RData rData){

        LinearLayout linearLayoutTextRPage = new LinearLayout(reservaionRoomList.this);
        linearLayoutTextRPage.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout linearLayoutImageRPage = new LinearLayout(reservaionRoomList.this);
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
        TextView textViewUser = new TextView(reservaionRoomList.this);
        textViewUser.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        textViewUser.setText("이름 : " + userName);
        textViewUser.setTextSize(12);
        textViewUser.setGravity(Gravity.CENTER);
        textViewUser.setPadding(10,20,10,20);

        TextView tv = new TextView(reservaionRoomList.this);
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tv.setText("아이디 : " + userID);
        tv.setTextSize(12);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(10,20,10,20);

        LinearLayout ll = new LinearLayout(reservaionRoomList.this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER);
        ll.addView(textViewUser);
        ll.addView(tv);
        ll.setBackgroundResource(R.drawable.border_tv);
        childLayout.addView(ll);
    }

    public void attachTime(String startTime, String endTime){
        TextView textViewTime = new TextView(reservaionRoomList.this);
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
}
