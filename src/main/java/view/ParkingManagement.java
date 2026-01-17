
package view;

import model.data.ClientData;
import model.entity.Client;

/**
 *
 * @author fabia
 */
public class ParkingManagement {

    public static void main(String[] args) {
        
        //Pequeño test de clients
        ClientData clientData = new ClientData();
        
        Client c1 = new Client("101" , "Juan Perez", "88881234", true);
        Client c2 = new Client("102", "Maria Lopez", "87776655", false);
        
        //Add
        clientData.addClient(c1);
        clientData.addClient(c2);
        
        //List
        for (Client c : clientData.getAllClients()) {
            System.out.println(c); 
        }
        
        //Consult
        Client found = clientData.findClientById("101");
        System.out.println("Found: " + found);
        
        
    }
}
