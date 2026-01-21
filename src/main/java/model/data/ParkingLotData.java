package model.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import model.entity.Client;
import model.entity.ParkingLot;
import model.entity.Space;
import model.entity.Vehicle;

public class ParkingLotData {

    private static final String FILE_PATH = "src/main/resources/parkinglots.json";
    private ArrayList<ParkingLot> parkingLots;
    static int parkingLotId = 0;

    private final Gson gson;

    public ParkingLotData() {
        gson = new Gson();
        parkingLots = loadParkingLots();

    }

    /*
    Loads clients from JSON file
     */
    private ArrayList<ParkingLot> loadParkingLots() {

        try (FileReader reader = new FileReader(FILE_PATH)) {

            Type listType = new TypeToken<ArrayList<ParkingLot>>() {
            }.getType();
            ArrayList<ParkingLot> loadedParkingLots = gson.fromJson(reader, listType);

            return (loadedParkingLots != null) ? loadedParkingLots : new ArrayList<>();

        } catch (Exception e) {

            return new ArrayList<>();
        }
    }

    /*
    Add new parkingLot
     */
    public ParkingLot addParkingLot(ParkingLot parkingLot) {

        ParkingLot parkingLotToReturn = new ParkingLot();

        if (parkingLot == null || findParkingLotById(parkingLot.getId()) != null) {

            parkingLotToReturn = parkingLot;

        }

        parkingLots.add(parkingLot);
        saveParkingLots();

        return parkingLotToReturn;
    }

    /*
    Returns all registered parkingLot
     */
    public ArrayList<ParkingLot> getAllParkingLots() {
        return parkingLots;
    }

    /*
    Finds a parkingLots by id
     */
    public ParkingLot findParkingLotById(int id) {

        for (ParkingLot parkingLot : parkingLots) {

            if (parkingLot.getId()==(id)){

                return parkingLot;

            }

        }

        return null;
    }

    /*
    Saves clients to JSON file
     */
    private void saveParkingLots() {

        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(parkingLots, writer);
        } catch (Exception e) {
            System.out.println("Error saving parking lots: " + e.getMessage());
        }

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
    
    
     public boolean updateParkingLot(ParkingLot updatedParkingLot) {

        boolean updated = false;

        if (updatedParkingLot != null && updatedParkingLot.getId()!= 0) {

            ParkingLot existing = findParkingLotById(updatedParkingLot.getId());

            if (existing != null) {

                if (updatedParkingLot.getName()!= null) {
                    existing.setName(updatedParkingLot.getName());
                }

                if (updatedParkingLot.getNumberOfSpaces()!= 0) {
                    existing.setNumberOfSpaces(updatedParkingLot.getNumberOfSpaces());
                }

                if (updatedParkingLot.getSpaces()!= null) {
                    existing.setSpaces(updatedParkingLot.getSpaces());
                }

                if (updatedParkingLot.getVehicles()!= null) {
                    existing.setVehicles(updatedParkingLot.getVehicles());
                }

                updated = true;
                saveParkingLots();
            }
        }

        return updated;
    }

    public boolean deleteParkingLot(ParkingLot parkingLot) {

        boolean deleted = false;

        if (parkingLot.getId() != 0) {

            ParkingLot parkingLotToDelete = findParkingLotById(parkingLot.getId());

            if (parkingLot != null) {
                parkingLots.remove(parkingLotToDelete);
                deleted = true;
                saveParkingLots();
            }
        }

        return deleted;
    }

   

}
