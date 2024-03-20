package org.ticket.booking.dto;

public class SeatAllocation {
    private String userName;
    private int seatNumber;

    public SeatAllocation(String userName, int seatNumber) {
        this.userName = userName;
        this.seatNumber = seatNumber;
    }

    public SeatAllocation() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }
}
