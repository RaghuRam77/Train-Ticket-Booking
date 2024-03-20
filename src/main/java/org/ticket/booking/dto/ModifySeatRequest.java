package org.ticket.booking.dto;

public class ModifySeatRequest {
    private String email;
    private int newSeatNumber;



    // Constructors, getters, and setters


    public ModifySeatRequest(String email, int newSeatNumber) {
        this.email = email;
        this.newSeatNumber = newSeatNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNewSeatNumber() {
        return newSeatNumber;
    }

    public void setNewSeatNumber(int newSeatNumber) {
        this.newSeatNumber = newSeatNumber;
    }
}
