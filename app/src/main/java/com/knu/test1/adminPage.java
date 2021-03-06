package com.knu.test1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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
import java.util.List;
import java.util.Vector;

public class adminPage extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    Button btnDateRangePicker;
    TextView txvStartDate, txvEndDate, editTextUserName;
    TimePicker startTimepicker, endTimepicker;
    CheckBox checkBox103, checkBox104, checkBox106, checkBox111, checkBox413;
    CheckBox checkBoxMon, checkBoxTue, checkBoxWed, checkBoxThu, checkBoxFri, checkBoxSat, checkBoxSun;

    int ny =0, nm=0, nd=0;
    String startTime, endTime;
    List<String> displayedValues;
    NumberPicker numberPicker;
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMMddHHmm");
    long now = System.currentTimeMillis();
    Date date = new Date(now);
    String stringNow = sdfNow.format(date);
    private int TIME_PICKER_INTERVAL = 30;

    long selectedDateStart, selectedDateEnd;

    int[] rooms = {103,104,106,111,413};
    boolean[] isRoomChecked={false,false,false,false,false};
    int roomNumber;
    boolean[] isDayChecked={false,false,false,false,false,false,false,false};

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
                new android.app.AlertDialog.Builder(adminPage.this)
                        .setTitle("")
                        .setMessage("정말 예약하시겠습니까?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                submit();
                            }})
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(adminPage.this, "취소하였습니다.", Toast.LENGTH_SHORT).show();
                            }})
                        .show();
                break;
        }
    }

    public void OnCheckBoxClicked(View v){
        //boolean checked = ((CheckBox) v).isChecked();
        for(int i=0;i<5;i++) isRoomChecked[i]=false;
        checkBox103.setChecked(false);
        checkBox104.setChecked(false);
        checkBox106.setChecked(false);
        checkBox111.setChecked(false);
        checkBox413.setChecked(false);

        switch(v.getId()){
            case R.id.checkbox103:
                isRoomChecked[0] = true;
                checkBox103.setChecked(true);
                break;
            case R.id.checkbox104:
                isRoomChecked[1] = true;
                checkBox104.setChecked(true);
                break;
            case R.id.checkbox106:
                isRoomChecked[2] = true;
                checkBox106.setChecked(true);
                break;
            case R.id.checkbox111:
                isRoomChecked[3] = true;
                checkBox111.setChecked(true);
                break;
            case R.id.checkbox413:
                isRoomChecked[4] = true;
                checkBox413.setChecked(true);
                break;

        }
    }

    public void OnCheckBoxDay(View v){
        switch (v.getId()){
            case R.id.checkBoxMon:
                if (checkBoxMon.isChecked())
                    isDayChecked[2] = true;
                else
                    isDayChecked[2] = false;
                break;
            case R.id.checkBoxTue:
                if (checkBoxTue.isChecked())
                    isDayChecked[3] = true;
                else
                    isDayChecked[3] = false;
                break;
            case R.id.checkBoxWed:
                if (checkBoxWed.isChecked())
                    isDayChecked[4] = true;
                else
                    isDayChecked[4] = false;
                break;
            case R.id.checkBoxThu:
                if (checkBoxThu.isChecked())
                    isDayChecked[5] = true;
                else
                    isDayChecked[5] = false;
                break;
            case R.id.checkBoxFri:
                if (checkBoxFri.isChecked())
                    isDayChecked[6] = true;
                else
                    isDayChecked[6] = false;
                break;
            case R.id.checkBoxSat:
                if (checkBoxSat.isChecked())
                    isDayChecked[7] = true;
                else
                    isDayChecked[7] = false;
                break;
            case R.id.checkBoxSun:
                if (checkBoxSun.isChecked())
                    isDayChecked[1] = true;
                else
                    isDayChecked[1] = false;
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
        editTextUserName = (TextView)findViewById(R.id.editTextUserName);
        startTimepicker = (TimePicker)findViewById(R.id.startTimepicker);
        endTimepicker = (TimePicker)findViewById(R.id.endTimepicker);

        checkBox103 = (CheckBox)findViewById(R.id.checkbox103);
        checkBox104 = (CheckBox)findViewById(R.id.checkbox104);
        checkBox106 = (CheckBox)findViewById(R.id.checkbox106);
        checkBox111 = (CheckBox)findViewById(R.id.checkbox111);
        checkBox413 = (CheckBox)findViewById(R.id.checkbox413);

        checkBoxMon = (CheckBox)findViewById(R.id.checkBoxMon);
        checkBoxTue = (CheckBox)findViewById(R.id.checkBoxTue);
        checkBoxWed = (CheckBox)findViewById(R.id.checkBoxWed);
        checkBoxThu = (CheckBox)findViewById(R.id.checkBoxThu);
        checkBoxFri = (CheckBox)findViewById(R.id.checkBoxFri);
        checkBoxSat = (CheckBox)findViewById(R.id.checkBoxSat);
        checkBoxSun = (CheckBox)findViewById(R.id.checkBoxSun);

        DateInit();
        setinterval();
    }

    public void DateInit(){
        long longNow = Long.parseLong(stringNow);
        long y = longNow/100000000;
        long m = (longNow%100000000)/1000000;
        long d = (longNow%1000000)/10000;
        ny=(int)y; nm=(int)m; nd=(int)d;
        txvStartDate.setText("From : "+ny+"/"+nm+"/"+nd);
        txvEndDate.setText("To : "+ny+"/"+nm+"/"+nd);
        selectedDateStart = (longNow/10000)*10000;
        selectedDateEnd = (longNow/10000)*10000;
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

        final Vector<Long> dates = new Vector<>();
        long syear, smonth, sdate;
        long eyear, emonth, edate;

        syear = selectedDateStart/100000000;
        smonth = (selectedDateStart%100000000)/1000000;
        sdate = (selectedDateStart%1000000)/10000;

        eyear = selectedDateEnd/100000000;
        emonth = (selectedDateEnd%100000000)/1000000;
        edate = (selectedDateEnd%1000000)/10000;

        if(editTextUserName.getText().toString().equals("")){
            Toast.makeText(this.getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_LONG).show();
            return;
        }

        boolean isRoomSelected=false;
        for(int i=0;i<5;i++){
            if(isRoomChecked[i]){
                isRoomSelected=true;
                roomNumber = i;
                break;
            }
        }
        if(!isRoomSelected){
            Toast.makeText(this.getApplicationContext(),"방을 하나 선택해주세요..",Toast.LENGTH_LONG).show();
            return;
        }

        boolean isDaySelected=false;
        for(int i=1;i<=7;i++){
            if(isDayChecked[i]){
                isDaySelected=true;
                break;
            }
        }
        if(!isDaySelected){
            Toast.makeText(this.getApplicationContext(), "요일을 선택해주세요.",Toast.LENGTH_LONG).show();
            return;
        }

        final long shour = startTimepicker.getCurrentHour();
        final long smin = startTimepicker.getCurrentMinute();
        final long ehour = endTimepicker.getCurrentHour();
        final long emin = endTimepicker.getCurrentMinute();

        long tempTimeStart = selectedDateStart+shour*100+(smin % 2==0? 0: 30);
        long tempTimeEnd = selectedDateStart+ehour*100+(emin % 2==0? 0: 30);
        if(tempTimeStart % 10000 == 0) tempTimeStart += 2400;
        if(tempTimeEnd % 10000 == 0) tempTimeEnd += 2400;

        if(tempTimeStart + 30 <= Long.parseLong(stringNow)){
            Toast.makeText(this.getApplicationContext(),"현재 시간 이후로 예약을 해주십시오.",Toast.LENGTH_LONG).show();
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

        Calendar calendar = Calendar.getInstance();
        calendar.set((int)syear, (int)smonth-1, (int)sdate);

        while(true){
            if(getDateByLong(calendar.getTime())>selectedDateEnd) break;
            if (isDayChecked[calendar.get(Calendar.DAY_OF_WEEK)])
                dates.add(getDateByLong(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
        }

        databaseReference.child("Users").child("110").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot1) {
                final String roomID = String.valueOf(rooms[roomNumber]);

                databaseReference.child("Rooms").child(roomID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                        User user = dataSnapshot1.getValue(User.class);
                        Room room = dataSnapshot2.getValue(Room.class);

                        for(int i=0;i<dates.size();i++){
                            Long d = dates.get(i);
                            long stime = d+shour*100+(smin % 2==0? 0: 30);
                            long etime = d+ehour*100+(emin % 2==0?0:30);
                            if(stime % 10000 == 0) stime += 2400;
                            if(etime % 10000 == 0) etime += 2400;

                            startTime = String.valueOf(stime);
                            endTime = String.valueOf(etime);
                            RData rData = new RData(startTime, endTime, "110", editTextUserName.getText().toString());
                            room.pushData(roomID, rData);
                            user.pushData(roomID, rData);
                        }

                        databaseReference.child("Rooms").child(room.roomID).setValue(room);
                        databaseReference.child("Users").child(user.userID).setValue(user);
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

        new AlertDialog.Builder(adminPage.this)
                .setTitle("")
                .setMessage("예약 완료")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((myReservation)myReservation.MyContext).ReLoading();
                        afterSumbit();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        ((myReservation)myReservation.MyContext).ReLoading();
                        afterSumbit();
                    }
                })
                .show();

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

    public void afterSumbit(){
        for(int i=0;i<5;i++)
            isRoomChecked[i]=false;
        checkBox103.setChecked(false);
        checkBox104.setChecked(false);
        checkBox106.setChecked(false);
        checkBox111.setChecked(false);
        checkBox413.setChecked(false);

        for(int i=1;i<=7;i++)
            isDayChecked[i] = false;
        checkBoxMon.setChecked(false);
        checkBoxTue.setChecked(false);
        checkBoxWed.setChecked(false);
        checkBoxThu.setChecked(false);
        checkBoxFri.setChecked(false);
        checkBoxSat.setChecked(false);
        checkBoxSun.setChecked(false);

        editTextUserName.setText("");
        DateInit();
        setinterval();
    }
}
