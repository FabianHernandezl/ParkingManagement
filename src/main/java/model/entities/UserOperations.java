/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package model.entities;

import java.util.ArrayList;

/**
 *
 * @author FAMILIA
 */
public interface UserOperations {
    //buscar
    public User searchUser(String identification);
    public User searchUser(User user);
    //ordena 
   // public ArrayList<User> sortUsers (Customer allUsers[]); todo ver esto
    public ArrayList<User> sortUsers (String identification, User allUsers[]);
}
