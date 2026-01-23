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
       //punto 10 lab
        ArrayList<Administrator> administrators = administratorData.getAllAdministrators();//list of admins just we 4
        Administrator adminToReturn = null;
        for (Administrator admin : administrators) {
            if (admin.getUsername().equalsIgnoreCase(user.getUsername()) 
                    && admin.getPassword().equals(user.getPassword()) ) {
                adminToReturn = admin; //herencia 
            }
        }
        return adminToReturn;
    }

      //for login
    public Administrator findClerkByUsername(String username) {
       return administratorData.findClerkByUsername(username);
    }
   
    public ArrayList<User> sortUsers(Clerk[] allUsers) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<User> sortUsers(String identification, User[] allUsers) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
