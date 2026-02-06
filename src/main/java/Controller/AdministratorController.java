/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.ArrayList;
import model.data.AdministratorData;
import model.entities.Administrator;
import model.entities.Clerk;
import model.entities.User;
import model.entities.UserOperations;

/**
 *
 * @author FAMILIA
 */
public class AdministratorController implements UserOperations {
     AdministratorData administratorData = new AdministratorData();

     //CRUD 
     public ArrayList<Administrator> getAllAdministrators() {
        return administratorData.getAllAdministrators();
    }
     
        public Administrator addAdministrator(Administrator admin) {
            return administratorData.addAdministrator(admin);
    }
     
     
      public boolean updateAdministrator(Administrator updatedAdministrator) {
        
        return administratorData.updateAdministrator(updatedAdministrator);
    }
     
      public boolean removeAdministrator(String id) {
       
        return administratorData.removeAdministrator(id);
    }
      
     
     
     
    @Override
    public User searchUser(String identification) {
        ArrayList<Administrator> administrators = administratorData.getAllAdministrators();//lista de admins
        Administrator administratorToReturn = null;
        for (Administrator administrator : administrators) {
            if (administrator.getId().equalsIgnoreCase(identification)) {
                administratorToReturn = administrator; //herencia 
            }
        }
        return administratorToReturn;
    }

    @Override
    public User searchUser(User user) {
    
        ArrayList<Administrator> administrators = administratorData.getAllAdministrators();//list of admins 
        Administrator adminToReturn = null;
        for (Administrator admin : administrators) {
            if (admin.getUsername().equalsIgnoreCase(user.getUsername()) 
                    && admin.getPassword().equals(user.getPassword()) ) {
                adminToReturn = admin; //herencia 
            }
        }
        return adminToReturn;
    }
    
    
    public int findLastAdminNumber() {
       
        return administratorData.findLastIdNumberOfAdmins();
    }

      //for login
    public Administrator findAdminByUsername(String username) {
       return administratorData.findAdminByUsername(username);
    }
      
    public Administrator findAdministratorById(String id) {
       
        return administratorData.findAdministratorById(id);
    }
   
    public ArrayList<User> sortUsers(Clerk[] allUsers) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<User> sortUsers(String identification, User[] allUsers) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
