package com.example.test1;

import java.io.Serializable;

public class RData implements Serializable {
    public String startTime;
    public String endTime;
    public String userID;
    public String userName;

    RData(){

    }

    RData(String startTime, String endTime, String userID, String userName){
        this.startTime = startTime;
        this.endTime = endTime;
        this.userID = userID;
        this.userName = userName;
    }
}
