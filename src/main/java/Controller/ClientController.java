package Controller;

import java.util.ArrayList;
import javax.xml.crypto.Data;
import model.data.ClientData;
import model.entity.Client;

/**
 *
 * @author Jimena
 */
public class ClientController {

    private ClientData clientData;

    public ClientController() {
        clientData = new ClientData();
    }

    public String registerClient(String id, String name, String phone, boolean isPreferential) {

        if (id == null || id.isEmpty() || name == null || name.isEmpty()) {
            return "Información invalida, por favor intente de nueva";
        }

        Client client = new Client(id, name, phone, isPreferential);
        boolean added = clientData.addClient(client);

        return added ? "Cliente registrado con exito" : "Cliente ya existe";

    }

    public ArrayList<Client> getAllClients() {

        return clientData.getAllClients();
    }

    public Client findClientById(String id) {

        return clientData.findClientById(id);
    }

    public String updateClient(String id, String name, String phone, boolean preferential) {

        Client client = new Client(id, name, phone, preferential);

        return clientData.update(client) ? "Cliente actualizado con exito" : "Cliente no encontrado";
    }

    public String deleteClient(String id) {

        return clientData.delete(id) ? "Cliente eliminado correctamente" : "Cliente no encontrado";
    }

}
