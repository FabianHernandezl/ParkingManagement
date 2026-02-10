/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

/**
 *
 * @author jimen
 */
public class ParkingLotReportRow {

    private String parkingLotName;
    private int spaceNumber;
    private String status;
    private String vehicleType;
    private String plate;

    public ParkingLotReportRow() {
    }

    public ParkingLotReportRow(String parkingLotName,
            int spaceNumber,
            String status,
            String vehicleType,
            String plate) {
        this.parkingLotName = parkingLotName;
        this.spaceNumber = spaceNumber;
        this.status = status;
        this.vehicleType = vehicleType;
        this.plate = plate;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public int getSpaceNumber() {
        return spaceNumber;
    }

    public void setSpaceNumber(int spaceNumber) {
        this.spaceNumber = spaceNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

}
