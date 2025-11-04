package com.example.qldb;

// Lớp model để giữ dữ liệu cho RecyclerView
public class Reservation {
    private int reservationId;
    private String userName;
    private String dateTime;
    private int numPeople;
    private String status;

    // Constructor
    public Reservation(int reservationId, String userName, String dateTime, int numPeople, String status) {
        this.reservationId = reservationId;
        this.userName = userName;
        this.dateTime = dateTime;
        this.numPeople = numPeople;
        this.status = status;
    }

    // Getters
    public int getReservationId() {
        return reservationId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getNumPeople() {
        return numPeople;
    }

    public String getStatus() {
        return status;
    }
}