package model.data;

import java.util.ArrayList;
import model.entity.ParkingLot;
import model.entity.Space;
import model.entity.Vehicle;

public class ParkingLotData {

    public ArrayList<ParkingLot> parkingLots;
    static int parkingLotId = 0;

    public ParkingLotData() {
        parkingLots = new ArrayList<>();
    }

    //create parkinglot
    public ParkingLot registerParkingLot(String name, Space spaces[]) {

        ParkingLot parkingLot = new ParkingLot();
        parkingLotId++;
        parkingLot.setId(parkingLotId);
        parkingLot.setName(name);
        parkingLot.setSpaces(spaces);
        parkingLots.add(parkingLot);

        return parkingLot;

    }

    public int registerVehicleInParkingLot(Vehicle vehicle, ParkingLot parkingLot) {

        // Validaciones básicas
        if (parkingLot == null || vehicle == null) {
            return 0;
        }

        Space[] spaces = parkingLot.getSpaces();

        // 🚨 ESTE ERA EL PROBLEMA
        if (spaces == null || spaces.length == 0) {
            System.out.println("El parqueo no tiene espacios inicializados");
            return 0;
        }

        ArrayList<Vehicle> vehiclesInParkingLot = parkingLot.getVehicles();

        if (vehiclesInParkingLot == null) {
            vehiclesInParkingLot = new ArrayList<>();
            parkingLot.setVehicles(vehiclesInParkingLot);
        }

        boolean hasDisability = vehicle.hasPreferentialClient();
        int vehicleTypeId = vehicle.getVehicleType().getId();

        for (Space space : spaces) {

            if (space == null || space.isSpaceTaken()) {
                continue;
            }

            if (space.getVehicleType().getId() != vehicleTypeId) {
                continue;
            }

            if (hasDisability && !space.isDisabilityAdaptation()) {
                continue;
            }

            if (!hasDisability && space.isDisabilityAdaptation()) {
                continue;
            }

            // Registrar vehículo
            vehiclesInParkingLot.add(vehicle);
            space.setSpaceTaken(true);

            return space.getId(); // ← retorno inmediato
        }

        // No se encontró espacio
        return 0;
    }
    public ParkingLot findParkingLotById(int id) {

        ParkingLot parkingLotToBeReturned = null;

        for (ParkingLot parkingLot : parkingLots) {

            if (parkingLot.getId() == id) {

                parkingLotToBeReturned = parkingLot;
                break;
            }
        }
        return parkingLotToBeReturned;
    }
      
    public ArrayList<ParkingLot> getAllParkingLots(){
        return parkingLots;
    }
    public void removeVehicleFromParkingLot(Vehicle vehicle, ParkingLot parkingLot) {

        ArrayList<Vehicle> vehiclesInParkingLot = parkingLot.getVehicles();
        Space spaces[] = parkingLot.getSpaces();
        //recorre la lista de vehículos para ver en qué posición
        //debemos retirar al vehículo actual
        for (int i = 0; i < vehiclesInParkingLot.size(); i++) {

            if (vehiclesInParkingLot.get(i) == vehicle) {

                vehiclesInParkingLot.remove(vehicle);
                spaces[i].setSpaceTaken(false);
                break;
            }

        }
        //*************actualizamos los espacios liberados
        //y los vehículos registrados en el parqueo

        parkingLot.setSpaces(spaces);
        parkingLot.setVehicles(vehiclesInParkingLot);

    }
    //update parkinglot
      public ParkingLot updateParkingLot(int id, ParkingLot newParkingLot){
         ParkingLot parkingLotToReturn = null;
         ParkingLot originalParkingLot = findParkingLotById(id);
         
        if(originalParkingLot != null){//el id parking  existe?
            parkingLotToReturn.setId(newParkingLot.getId());
            parkingLotToReturn.setName(newParkingLot.getName());
            parkingLotToReturn.setNumberOfSpaces(newParkingLot.getNumberOfSpaces());
            parkingLotToReturn.setSpaces(newParkingLot.getSpaces());
            parkingLotToReturn.setVehicles(newParkingLot.getVehicles());
        }
        return parkingLotToReturn;
     }
      //remove parkinglot
       public void removeParkingLot(ParkingLot parkingLot){
        parkingLots.remove(parkingLot);
    }
     
      
}
