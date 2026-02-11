package model.entities;

public class ParkingRate {

    private int parkingLotId;        // ID del parqueo
    private String parkingLotName;   // Nombre del parqueo 
    private String vehicleType;      // Carro, Moto, Camión
    private double hourPrice;
    private double halfHourPrice;
    private double dayPrice;
    private double weekPrice;
    private double monthPrice;
    private double yearPrice;

    public ParkingRate() {
    }

    public ParkingRate(int parkingLotId, String parkingLotName, String vehicleType,
            double hourPrice, double halfHourPrice, double dayPrice,
            double weekPrice, double monthPrice, double yearPrice) {
        this.parkingLotId = parkingLotId;
        this.parkingLotName = parkingLotName;
        this.vehicleType = vehicleType;
        this.hourPrice = hourPrice;
        this.halfHourPrice = halfHourPrice;
        this.dayPrice = dayPrice;
        this.weekPrice = weekPrice;
        this.monthPrice = monthPrice;
        this.yearPrice = yearPrice;
    }

    // Getters y Setters
    public int getParkingLotId() {
        return parkingLotId;
    }

    public void setParkingLotId(int parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getHourPrice() {
        return hourPrice;
    }

    public void setHourPrice(double hourPrice) {
        this.hourPrice = hourPrice;
    }

    public double getHalfHourPrice() {
        return halfHourPrice;
    }

    public void setHalfHourPrice(double halfHourPrice) {
        this.halfHourPrice = halfHourPrice;
    }

    public double getDayPrice() {
        return dayPrice;
    }

    public void setDayPrice(double dayPrice) {
        this.dayPrice = dayPrice;
    }

    public double getWeekPrice() {
        return weekPrice;
    }

    public void setWeekPrice(double weekPrice) {
        this.weekPrice = weekPrice;
    }

    public double getMonthPrice() {
        return monthPrice;
    }

    public void setMonthPrice(double monthPrice) {
        this.monthPrice = monthPrice;
    }

    public double getYearPrice() {
        return yearPrice;
    }

    public void setYearPrice(double yearPrice) {
        this.yearPrice = yearPrice;
    }

    @Override
    public String toString() {
        return parkingLotName + " - " + vehicleType + " [Hora: ₡" + hourPrice + "]";
    }
}
