package com.knu.test1;

import java.io.Serializable;
import java.util.HashMap;

public class User implements Serializable {
    public String userID;
    public String userPW;
    public HashMap<String, RData>userRMap = new HashMap<String, RData>();

    User(){

    }

    User(String userID, String userPW){
        this.userID = userID;
        this.userPW = userPW;
    }

    public void pushData(String ID , RData rData){
        userRMap.put(rData.startTime + ID, rData);
    }
}
