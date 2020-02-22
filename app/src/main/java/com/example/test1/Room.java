package com.example.test1;

import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;

public class Room implements Serializable {
    public String roomID;
    public HashMap<String, RData>roomRMap = new HashMap<String, RData>();

    Room(){

    }

    Room(String roomID){
        this.roomID = roomID;
    }

    public void pushData(String ID , RData rData){
        roomRMap.put(rData.startTime + ID, rData);
    }
}
