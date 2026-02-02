/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import java.util.ArrayList;

/**
 *
 * @author FAMILIA
 */
public class Clerk extends User{
    private int employeeCode;
   private String shedule;
   private int age;
   private ArrayList<ParkingLot> parkingLot;
   //+ los atributos de User por herencia

    public Clerk() {
    }

    public Clerk(int employeeCode, String shedule, int age, ArrayList<ParkingLot> parkingLot) {
        this.employeeCode = employeeCode;
        this.shedule = shedule;
        this.age = age;
        this.parkingLot = parkingLot;
    }

    public Clerk(int employeeCode, String shedule, int age, ArrayList<ParkingLot> parkingLot, String id, String name, String username, String password) {
        super(id, name, username, password);
        this.employeeCode = employeeCode;
        this.shedule = shedule;
        this.age = age;
        this.parkingLot = parkingLot;
    }

 

    public int getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(int employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getShedule() {
        return shedule;
    }

    public void setShedule(String shedule) {
        this.shedule = shedule;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public ArrayList<ParkingLot> getParkingLot() {
        return parkingLot;
    }

    public void setParkingLot(ArrayList<ParkingLot> parkingLot) {
        this.parkingLot = parkingLot;
    }

    @Override
    public String toString() {
        return "Clerk{" + "employeeCode=" + employeeCode + ", shedule=" + shedule + ", age=" + age + ", parkingLot(s)=" + parkingLot + '}';
    }

   

    
    @Override
    public boolean verifyUserLogin(String[] loginDetails) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
