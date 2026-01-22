/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

/**
 *
 * @author FAMILIA
 */
public abstract class User {
    private String id;
    private String name;
    private String username;
    private String password;

    public User() {
    }

    
    public User(String id, String name, String username, String password) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
    }
    
    
    
     public abstract boolean verifyUserLogin(String[] loginDetails);
     
     //mètodo intencionalmente poliforfico, pero que no vamos a usar 
     //si los usuarios se autentican de forma diferente
    public boolean verifyUserLogin(String[] loginDetails, int id){
        return username.equals(loginDetails[0]) && 
                password.equals(loginDetails[1]);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
