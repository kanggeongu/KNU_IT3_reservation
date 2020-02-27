package com.example.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class reservationPage extends AppCompatActivity {

    TextView textView;
    EditText editTextStartTime, editTextEndTime, editTextUserName;
    Button buttonReservation;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    User user;
    Room room;

    String startTime, endTime;
    List<String> displayedValues;
    NumberPicker numberPicker;
    private int TIME_PICKER_INTERVAL = 30;

    int ny =0, nm=0, nd=0;
    long selectedDate, temp=0;
    TextView txvDate;
    TimePicker startTimepicker, endTimepicker;
    DatePickerDialog.OnDateSetListener datePickerListener;
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    String stringNow = sdfNow.format(date);
    ArrayList<String> delKeyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reservation_page);

        temp = 20;
        temp *= 100; temp *= 10000; temp *= 10000;

        init();
        initializeListener();
        InitializeOther();
    }

    public void init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        user = (User)getIntent().getSerializableExtra("user");
        room = (Room)getIntent().getSerializableExtra("room");

        startTimepicker = (TimePicker)findViewById(R.id.startTimepicker);
        endTimepicker = (TimePicker)findViewById(R.id.endTimepicker);
        txvDate = (TextView)findViewById(R.id.txvDate);
    }

    public void initializeListener(){
        datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ny = year; nm = month+1; nd = dayOfMonth;
                long y = year, m=month+1, d=dayOfMonth;
                txvDate.setText(y+"년 " + m + "월 " + d + "일");
                selectedDate = y % 100 * 100000000+m*1000000+d*10000;
                changeImageView();
            }
        };
    }

    public void InitializeOther(){
        setinterval();

        long longNow = Long.parseLong(stringNow);
        long y = longNow/100000000;
        long m = (longNow%100000000)/1000000;
        long d = (longNow%1000000)/10000;
        ny=(int)y; nm=(int)m; nd=(int)d;
        txvDate.setText(y+"년 "+m+"월 "+d+"일");
        selectedDate = (longNow/10000)*10000 - temp;

        changeImageView();
    }

    public void setTimePickerInterval(TimePicker timePicker){
        try{
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field field = classForid.getField("minute");
            numberPicker = (NumberPicker)timePicker.findViewById(field.getInt(null));
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(3);
            numberPicker.setDisplayedValues(displayedValues.toArray(new String[0]));
            numberPicker.setWrapSelectorWheel(true);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setinterval(){
        displayedValues = new ArrayList<>();
        for(int k=0;k<3;k++){
            for(int i=0;i<60;i+=TIME_PICKER_INTERVAL){
                displayedValues.add(String.format("%02d", i));
            }
        }
        startTimepicker.setCurrentMinute(0);
        startTimepicker.setIs24HourView(true);
        setTimePickerInterval(startTimepicker);
        endTimepicker.setCurrentMinute(0);
        endTimepicker.setIs24HourView(true);
        setTimePickerInterval(endTimepicker);
    }

    public void OnClickHandler(View view){
        switch (view.getId()){
            case R.id.btnDatepicker:
                DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener, ny,nm-1,nd);
                dialog.show();
                break;
            case R.id.buttonReservation:
                submit();
                break;
        }
    }

    public void submit(){

        long stime, etime;
        long shour, smin, ehour, emin;

        shour = startTimepicker.getCurrentHour();
        smin = startTimepicker.getCurrentMinute();
        ehour = endTimepicker.getCurrentHour();
        emin = endTimepicker.getCurrentMinute();


        stime = selectedDate+shour*100+(smin % 2==0? 0: 30);
        etime = selectedDate+ehour*100+(emin % 2==0?0:30);
        if(etime == 0) etime = 2400;


        if(stime + temp - Long.parseLong(stringNow)<-30){
            Toast.makeText(this.getApplicationContext(),"start time should be faster than now",Toast.LENGTH_LONG).show();
            return;
        }
        if(stime>=etime){
            Toast.makeText(this.getApplicationContext(),"start time should be faster than end time",Toast.LENGTH_LONG).show();
            return;
        }

        startTime = String.valueOf(stime);
        endTime = String.valueOf(etime);


        databaseReference.child("Rooms").child(room.roomID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean flag = true;
                Room room = dataSnapshot.getValue(Room.class);
                Iterator<String> iter = room.roomRMap.keySet().iterator();
                delKeyList = new ArrayList<>();
                while(iter.hasNext()){
                    String key = iter.next();
                    RData value = room.roomRMap.get(key);
                    String rstime = value.startTime, retime = value.endTime;

                    long chk1 = Long.parseLong(rstime)/10000;
                    long chk2 = (Long.parseLong(stringNow)-temp)/10000;

                    if(chk1<chk2){
                        delKeyList.add(key);
                    }

                    if(selectedDate <= Long.parseLong(startTime) && Long.parseLong(endTime) <= selectedDate + 10000){
                        if( !(Long.parseLong(retime) <= Long.parseLong(startTime) ||
                                Long.parseLong(rstime) >= Long.parseLong(endTime))) {
                            flag = false;
                        }
                    }
                }


                if(flag)
                    submit2();
                else
                    Toast.makeText(getApplicationContext(),"예약시간이 겹칩니다.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void submit2(){

        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        String userName = editTextUserName.getText().toString();

        RData rData;
        rData = new RData(startTime, endTime, user.userID,userName);
        room.pushData(room.roomID , rData);
        for(String k : delKeyList){
            room.roomRMap.remove(k);
        }

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

    public void changeImageView(){
        for(int i=900;i<=2330;){
            String temp = "img"+Long.toString(i);
            int k = getResources().getIdentifier(temp,"id","com.example.test1");
            ImageView img = (ImageView) findViewById(k);
            img.setImageResource(R.drawable.white);
            if(i%100==0) i+=30;
            else i+=70;
        }

        databaseReference.child("Rooms").child(room.roomID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            String temp = "img"+Long.toString(i);
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
