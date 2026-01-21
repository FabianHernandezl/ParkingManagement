/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.ArrayList;
import model.data.ParkingLotData;
import model.entity.ParkingLot;
import model.entity.Vehicle;
import model.entity.Space;

/**
 *
 * @author FAMILIA
 */
public class ParkingLotController {

    private ParkingLotData parkingLotData = new ParkingLotData();
//public ParkingLot registerParkingLot(String name, Space spaces[]) 
    public ParkingLot registerParkingLot(ParkingLot parkingLot) {
        
        return parkingLotData.addParkingLot(parkingLot); //addParkingLot(name, spaces);
    }

    public int registerVehicleInParkingLot(Vehicle vehicle, ParkingLot parkingLot) {

        return parkingLotData.registerVehicleInParkingLot(vehicle, parkingLot);

    }

    public void removeVehicleFromParkingLot(Vehicle vehicle, ParkingLot parkingLot) {

        parkingLotData.removeVehicleFromParkingLot(vehicle, parkingLot);
    }

    public ParkingLot findParkingLotById(int id) {

        return parkingLotData.findParkingLotById(id);
    }

    public ArrayList<ParkingLot> getAllParkingLots() {

        return parkingLotData.getAllParkingLots();
    }

    public String updateParkingLot(int id, ParkingLot newParkingLot) {
        String result = "";
        if (parkingLotData.findParkingLotById(id) != null) { //rev this creo que es !=
            parkingLotData.updateParkingLot(newParkingLot);
            result = "El parqueo fue actualizado correctamente";
        } else {
            result = "El parqueo no se actualizó porque no se encontró en la base de datos.";
        }
        return result;
    }

    public String removeParkingLot(ParkingLot parkingLot) { //será mejor hacerlo por id? 
        String result = "";
        if (parkingLotData.findParkingLotById(parkingLot.getId()) != null) { 
            parkingLotData.deleteParkingLot(parkingLot);
            result = "El parqueo se eliminó ";
        } else {
            result = "El parqueo no se eliminó porque no se encontró en la base de datos.";
        }
        return result;
    }

}
