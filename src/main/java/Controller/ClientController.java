package Controller;

import java.util.ArrayList;
import model.data.ClientData;
import model.data.VehicleData;
import model.entities.Client;

/**
 *
 * @author Jimena
 */
public class ClientController {

    private ClientData clientData;
    private VehicleData vehicleData;

    public ClientController() {
        clientData = new ClientData();
        vehicleData = new VehicleData();
    }

    public String registerClient(String id, String name, String phone, boolean isPreferential, String email) {

        if (id == null || id.isEmpty() || name == null || name.isEmpty()) {
            return "Información invalida, por favor intente de nueva";
        }
        Client client = new Client(id, name, phone, isPreferential, email);
        boolean added = clientData.addClient(client);

        return added ? "Cliente registrado con exito" : "Cliente ya existe";

    }

    public ArrayList<Client> getAllClients() {

        return clientData.getAllClients();
    }

    public Client findClientById(String id) {

        return clientData.findClientById(id);
    }

    public String updateClient(String id, String name, String phone, boolean preferential, String email) {

        Client client = new Client(id, name, phone, preferential, email);

        return clientData.update(client) ? "Cliente actualizado con exito" : "Cliente no encontrado";
    }

    public String deleteClient(String id) {

        Client client = clientData.findClientById(id);
        if (client == null) {
            return "Cliente no encontrado";
        }
        if (vehicleData.findVehicle(client) != null) {
            return "No se puede eliminar: El cliente tiene vehículos asociados.";
        }

        boolean eliminado = clientData.delete(id, vehicleData);

        if (eliminado) {
            return "Cliente eliminado correctamente";
        } else {
            return "Error al intentar eliminar el cliente";
        }
    }

}
