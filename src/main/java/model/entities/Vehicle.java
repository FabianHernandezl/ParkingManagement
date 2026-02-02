package model.entities;

import java.util.ArrayList;

/**
 *
 * @author fabian
 */
public class Vehicle {

    private String plate;
    private String color;
    private String brand;
    private String model;
    private ArrayList<Client> clients;
    private VehicleType vehicleType;

    public Vehicle() {
        this.clients = new ArrayList<>();
    }

    public Vehicle(String plate, String color, String brand, String model, Client client, VehicleType vehicleType) {
        this.plate = plate;
        this.color = color;
        this.brand = brand;
        this.model = model;
        this.vehicleType = vehicleType;
        this.clients = new ArrayList<>();
        this.clients.add(client);
    }

    public Vehicle(String plate, String color, String brand, String model) {
        this.plate = plate;
        this.color = color;
        this.brand = brand;
        this.model = model;
        this.clients = new ArrayList<>();
    }

    public String getPlate() {
        return plate;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public void setClients(ArrayList<Client> clients) {
        this.clients = clients;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void addClient(Client client) {
        if (client != null && !clients.contains(client)) {
            clients.add(client);
        }
    }

    public boolean hasPreferentialClient() {
        for (Client c : clients) {
            if (c.isIsPreferential()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {

        StringBuilder infoClients = new StringBuilder();

        if (clients != null && !clients.isEmpty()) {
            for (Client c : clients) {
                if (c != null) {
                    infoClients.append("- ")
                            .append(c.getName() != null ? c.getName() : "Sin nombre")
                            .append("\n");
                }
            }
        } else {
            infoClients.append("No hay clientes asociados\n");
        }

        return "========== VEHÍCULO ==========\n"
                + "Placa: " + (plate != null ? plate : "No definida") + "\n"
                + "Marca: " + (brand != null ? brand : "No definida") + "\n"
                + "Modelo: " + (model != null ? model : "No definido") + "\n"
                + "Color: " + (color != null ? color : "No definido") + "\n"
                + "Tipo: " + (vehicleType != null ? vehicleType.getDescription() : "No definido") + "\n"
                + "Clientes:\n"
                + infoClients
                + "==============================";
    }

    public String getIcon() {
        if (vehicleType.getDescription().equalsIgnoreCase("Carro")) {
            return "🚗";
        }
        if (vehicleType.getDescription().equalsIgnoreCase("Moto")) {
            return "🏍";
        }
        if (vehicleType.getDescription().equalsIgnoreCase("Camión")) {
            return "🚚";
        }
        return "❓";
    }

}
