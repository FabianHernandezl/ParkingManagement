/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

/**
 *
 * @author Jimena Calvo Soto
 */
public class ParkingAssignment {

    private ParkingLot parkingLot;
    private Space space;

    public ParkingAssignment(ParkingLot parkingLot, Space space) {
        this.parkingLot = parkingLot;
        this.space = space;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public Space getSpace() {
        return space;
    }

}
