package com.miggy.mekaniko.historyRecyclerView;

/**
 * Created by miggy on 01/10/2018.
 */

public class HistoryObject {
    private String rideId;
    private String time;
    private String ID;

    public HistoryObject(String rideId, String time) {
        this.rideId = rideId;
        this.time = time;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}