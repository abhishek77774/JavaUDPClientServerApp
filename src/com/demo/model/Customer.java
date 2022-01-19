package com.demo.model;

import java.io.Serializable;

/**
 * Customer Model Object
 */
public class Customer implements Serializable {

    private String clientId;
    private int pinNumber;
    private Boolean status;
    private int numberOfTravels;
    private double totalCost;

    public Customer() {}

    public Customer(String clientId, int pinNumber, Boolean status, int numberOfTravels) {
        this.clientId = clientId;
        this.pinNumber = pinNumber;
        this.status = status;
        this.numberOfTravels = numberOfTravels;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(int pinNumber) {
        this.pinNumber = pinNumber;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public int getNumberOfTravels() {
        return numberOfTravels;
    }

    public void setNumberOfTravels(int numberOfTravels) {
        this.numberOfTravels = numberOfTravels;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }
}
