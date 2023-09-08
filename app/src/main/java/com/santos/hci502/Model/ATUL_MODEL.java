package com.santos.hci502.Model;

public class ATUL_MODEL {
    String name, profilePicUrl, topUpValue, userUid;
    long timeStamp;

    public ATUL_MODEL() {
        //empty constructor
    }

    public ATUL_MODEL(String name, String profilePicUrl, String topUpValue, long timeStamp, String userUid) {
        this.name = name;
        this.profilePicUrl = profilePicUrl;
        this.topUpValue = topUpValue;
        this.timeStamp = timeStamp;
        this.userUid = userUid;
    }

    public String getName() {
        return name;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getTopUpValue() {
        return topUpValue;
    }

    public String getUserUid() {
        return userUid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
