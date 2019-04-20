package com.cashrichtask.model;

public class EquityData {

    private String date;
    private String sensex;
    private String equity;
    private String point;

    public String getDate() {
        return date;
    }

    public EquityData setDate(String date) {
        this.date = date;
        return this;
    }

    public String getSensex() {
        return sensex;
    }

    public EquityData setSensex(String sensex) {
        this.sensex = sensex;
        return this;
    }

    public String getEquity() {
        return equity;
    }

    public EquityData setEquity(String equity) {
        this.equity = equity;
        return this;
    }

    public String getPoint() {
        return point;
    }

    public EquityData setPoint(String point) {
        this.point = point;
        return this;
    }
}
