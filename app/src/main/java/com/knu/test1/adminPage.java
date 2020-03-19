package com.knu.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class adminPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Button btnDateRangePicker;
    TextView txvStartDate, txvEndDate;
    TimePicker startTimepicker, endTimepicker;

    int ny =0, nm=0, nd=0;
    String startTime, endTime;
    List<String> displayedValues;
    NumberPicker numberPicker;
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    String stringNow = sdfNow.format(date);
    ArrayList<String> delKeyList;
    private int TIME_PICKER_INTERVAL = 30;

    long selectedDateStart, selectedDateEnd;

    int[] rooms = {103,104,106,111,413};
    boolean[] isRoomChecked={false,false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_page);
        init();
    }

    public void OnClickHandler(View v){
        switch(v.getId()){
            case R.id.btnDateRangePicker:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
                        adminPage.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.btnReservation:
                submit();
                break;
        }
    }

    public void OnCheckBoxClicked(View v){
        boolean checked = ((CheckBox) v).isChecked();
        switch(v.getId()){
            case R.id.checkbox103:
                if(checked) isRoomChecked[0] = true;
                else isRoomChecked[0] = false;
                break;
            case R.id.checkbox104:
                if(checked) isRoomChecked[1] = true;
                else isRoomChecked[1] = false;
                break;
            case R.id.checkbox105:
                if(checked) isRoomChecked[2] = true;
                else isRoomChecked[2] = false;
                break;
            case R.id.checkbox106:
                if(checked) isRoomChecked[3] = true;
                else isRoomChecked[3] = false;
                break;
            case R.id.checkbox413:
                if(checked) isRoomChecked[4] = true;
                else isRoomChecked[4] = false;
                break;

        }
    }

    public void init(){
        for(int i=0;i<5;i++) isRoomChecked[i] = false;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        btnDateRangePicker = (Button) findViewById(R.id.btnDateRangePicker);
        txvEndDate = (TextView) findViewById(R.id.txvEndDate);
        txvStartDate = (TextView) findViewById(R.id.txvStartDate);
        startTimepicker = (TimePicker)findViewById(R.id.startTimepicker);
        endTimepicker = (TimePicker)findViewById(R.id.endTimepicker);

        long longNow = Long.parseLong(stringNow);
        long y = longNow/100000000;
        long m = (longNow%100000000)/1000000;
        long d = (longNow%1000000)/10000;
        ny=(int)y; nm=(int)m; nd=(int)d;
        txvStartDate.setText("From : "+ny+"/"+nm+"/"+nd);
        txvEndDate.setText("To : "+ny+"/"+nm+"/"+nd);
        selectedDateStart = (longNow/10000)*10000;
        selectedDateEnd = (longNow/10000)*10000;

        setinterval();
    }

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");
        if(dpd != null) dpd.setOnDateSetListener(this);
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

    public long getDateByLong(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Long.parseLong(sdf.format(date))*10000;
    }

    public void submit(){
        if(selectedDateStart > selectedDateEnd){
            Toast.makeText(this.getApplicationContext(),"날짜 세팅 오류",Toast.LENGTH_LONG).show();
            return;
        }

        Vector<Long> dates = new Vector<>();

        long syear, smonth, sdate;
        long eyear, emonth, edate;

        syear = selectedDateStart/100000000;
        smonth = (selectedDateStart%100000000)/1000000;
        sdate = (selectedDateStart%1000000)/10000;


        eyear = selectedDateEnd/100000000;
        emonth = (selectedDateEnd%100000000)/1000000;
        edate = (selectedDateEnd%1000000)/10000;


        Calendar calendar = Calendar.getInstance();
        calendar.set((int)syear, (int)smonth-1, (int)sdate);

        while(true){
            dates.add(getDateByLong(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
            if(getDateByLong(calendar.getTime())>selectedDateEnd) break;
        }

        boolean isRoomSelected=false;
        for(int i=0;i<5;i++){
            if(isRoomChecked[i]){
                isRoomSelected=true;
                break;
            }
        }
        if(!isRoomSelected){
            Toast.makeText(this.getApplicationContext(),"선택된 방 없음.",Toast.LENGTH_LONG).show();
            return;
        }

        long stime, etime;
        long shour, smin, ehour, emin;

        shour = startTimepicker.getCurrentHour();
        smin = startTimepicker.getCurrentMinute();
        ehour = endTimepicker.getCurrentHour();
        emin = endTimepicker.getCurrentMinute();

        long tempTimeStart = selectedDateStart+shour*100+(smin % 2==0? 0: 30);
        long tempTimeEnd = selectedDateStart+ehour*100+(emin % 2==0? 0: 30);

        if(tempTimeStart + 30 <= Long.parseLong(stringNow)){
            Toast.makeText(this.getApplicationContext(),"현재 시간보다 이후로 예약을 해주십시오.",Toast.LENGTH_LONG).show();
            return;
        }
        if(tempTimeStart>=tempTimeEnd){
            Toast.makeText(this.getApplicationContext(),"시작시간이 더 일찍 와야합니다.",Toast.LENGTH_LONG).show();
            return;
        }
        if(tempTimeStart % 10000 < 900){
            Toast.makeText(this.getApplicationContext(),"예약은 9시부터 24시까지 가능합니다.",Toast.LENGTH_LONG).show();
            return;
        }

        for(int i=0;i<dates.size();i++){
            Long d = dates.get(i);
            stime = d+shour*100+(smin % 2==0? 0: 30);
            etime = d+ehour*100+(emin % 2==0?0:30);

            startTime = String.valueOf(stime);
            endTime = String.valueOf(etime);
            for(int j=0;j<5;j++){
                if(isRoomChecked[j]){
                    databaseReference.child("Rooms").child(String.valueOf(rooms[j])).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth,int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
        long lys = year, lms = monthOfYear+1, lds = dayOfMonth;
        long lye = yearEnd, lme = monthOfYearEnd+1, lde = dayOfMonthEnd;

        selectedDateStart = lys*100000000+lms*1000000+lds*10000;
        selectedDateEnd = lye*100000000+lme*1000000+lde*10000;

        txvStartDate.setText("From : "+lys+"/"+lms+"/"+lds);
        txvEndDate.setText("To : "+lye+"/"+lme+"/"+lde);
    }
}
