package com.knu.test1;

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
