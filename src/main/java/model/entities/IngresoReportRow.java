/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author fabia
 */
public class IngresoReportRow {

    private String parkingLotName;
    private int cantidadVehiculos;
    private double totalRecaudado;
    private String fechaInicio;
    private String fechaFin;
    private double promedioPorVehiculo;

    public IngresoReportRow() {
    }

    public IngresoReportRow(String parkingLotName,
            int cantidadVehiculos,
            double totalRecaudado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        this.parkingLotName = parkingLotName;
        this.cantidadVehiculos = cantidadVehiculos;
        this.totalRecaudado = totalRecaudado;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.fechaInicio = fechaInicio != null ? fechaInicio.format(formatter) : "N/A";
        this.fechaFin = fechaFin != null ? fechaFin.format(formatter) : "N/A";
        this.promedioPorVehiculo = cantidadVehiculos > 0 ? totalRecaudado / cantidadVehiculos : 0;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public int getCantidadVehiculos() {
        return cantidadVehiculos;
    }

    public void setCantidadVehiculos(int cantidadVehiculos) {
        this.cantidadVehiculos = cantidadVehiculos;
        this.promedioPorVehiculo = cantidadVehiculos > 0 ? totalRecaudado / cantidadVehiculos : 0;
    }

    public double getTotalRecaudado() {
        return totalRecaudado;
    }

    public void setTotalRecaudado(double totalRecaudado) {
        this.totalRecaudado = totalRecaudado;
        this.promedioPorVehiculo = cantidadVehiculos > 0 ? totalRecaudado / cantidadVehiculos : 0;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public double getPromedioPorVehiculo() {
        return promedioPorVehiculo;
    }
}
