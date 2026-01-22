/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import java.util.ArrayList;
import model.entities.Clerk;
import model.entities.User;
import model.entities.UserOperations;
import model.data.ClerkData;
/**
 *
 * @author FAMILIA
 */
public class ClerkController implements UserOperations {
    
    ClerkData clerkData = new ClerkData();

    @Override
    public User searchUser(String identification) {

        ArrayList<Clerk> clerks = clerkData.getAllClerks();//lista de operadores
        Clerk clerkToReturn = null;
        for (Clerk clerk : clerks) {
            if (clerk.getId().equalsIgnoreCase(identification)) {
                clerkToReturn = clerk; //herencia 
            }
        }
        return clerkToReturn;

    }

    @Override
    public User searchUser(User user) {
        //punto 10 lab
        ArrayList<Clerk> clerks = clerkData.getAllClerks();//lista de operadores
        Clerk clerkToReturn = null;
        for (Clerk clerk : clerks) {
            if (clerk.getUsername().equalsIgnoreCase(user.getUsername()) 
                    && clerk.getPassword().equals(user.getPassword()) ) {
                clerkToReturn = clerk; //herencia 
            }
        }
        return clerkToReturn;
    }


    public ArrayList<User> sortUsers(Clerk[] allUsers) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public ArrayList<User> sortUsers(String identification, User[] allUsers) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
