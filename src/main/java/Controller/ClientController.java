package Controller;

import java.util.ArrayList;
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
            return "Invalid data, please try again";
        }

        Client client = new Client(id, name, phone, isPreferential);
        boolean added = clientData.addClient(client);

        return added ? "Client registered successfully" : "Client already exists";

    }

    public ArrayList<Client> getAllClients() {

        return clientData.getAllClients();
    }

    public Client findClientById(String id) {

        return clientData.findClientById(id);
    }

}
