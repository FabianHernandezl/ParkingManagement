/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

/**
 *
 * @author FAMILIA
 */
public class Administrator extends User{
    private int adminNumber;
    private ParkingLot parkingLot;

    public Administrator() {
    }

    public Administrator(int adminNumber, ParkingLot parkingLot) {
        this.adminNumber = adminNumber;
        this.parkingLot = parkingLot;
    }

    public Administrator(int adminNumber, ParkingLot parkingLot, String id, String name, String username, String password) {
        super(id, name, username, password);
        this.adminNumber = adminNumber;
        this.parkingLot = parkingLot;
    }

    

    public int getAdminNumber() {
        return adminNumber;
    }

    public void setAdminNumber(int adminNumber) {
        this.adminNumber = adminNumber;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    @Override
    public String toString() {
        return "Administrator{" + "adminNumber=" + adminNumber + ", parkingLot=" + parkingLot + '}';
    }

    @Override
    public boolean verifyUserLogin(String[] loginDetails) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
